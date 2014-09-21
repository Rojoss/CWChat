package com.clashwars.cwchat;

import com.clashwars.cwchat.events.ChatPlayerListener;
import com.clashwars.cwchat.commands.Commands;
import com.clashwars.cwchat.config.Config;
import com.clashwars.cwchat.config.PrefixConfig;
import com.clashwars.cwchat.wrappers.ChatType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;


public class CWChat extends JavaPlugin {
    private static CWChat instance;

    private Chat chat;

    private Commands cmds;

    public HashMap<UUID, ChatType> playerChat = new HashMap<UUID, ChatType>();

    public HashMap<UUID, Long> coloredChatCD = new HashMap<UUID, Long>();

    private Config pcfg;
    private Config mcfg;
    private final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        log("disabled");
    }

    @Override
    public void onEnable() {
        instance = this;

        UUID uuid;
        for (Player player : getServer().getOnlinePlayers()) {
            uuid = player.getUniqueId();
            if (!playerChat.containsKey(uuid)) {
                playerChat.put(uuid, ChatType.PUBLIC);
            }
        }

        chat = new Chat(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChatPlayerListener(this), this);

        pcfg = new PrefixConfig();
        pcfg.init();
        pcfg.load();

        cmds = new Commands(this);
        cmds.populateCommands();

        log("loaded successfully");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        return cmds.executeCommand(sender, lbl, args);
    }

    public void log(Object msg) {
        log.info("[CWChat " + getDescription().getVersion() + "]: " + msg.toString());
    }

    public static CWChat inst() {
        return instance;
    }

	
	/* Getters & Setters */

    public Config getMainConfig() {
        return mcfg;
    }

    public Config getPrefixConfig() {
        return pcfg;
    }

    public Chat getChat() {
        return chat;
    }
}