package net.clashwars.cwchat;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import net.clashwars.cwchat.bukkit.CWChatPlugin;
import net.clashwars.cwchat.bukkit.events.ChatPlayerListener;
import net.clashwars.cwchat.commands.Commands;
import net.clashwars.cwchat.config.Config;
import net.clashwars.cwchat.config.PrefixConfig;
import net.clashwars.cwchat.wrappers.ChatType;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;


public class CWChat {
	private CWChatPlugin							cwc;
	
	private Chat chat;
	
	private Commands              	   		        cmds;
	
	public HashMap<UUID, ChatType> playerChat = new HashMap<UUID, ChatType>();
	
	public HashMap<UUID, Long> 						coloredChatCD 	= new HashMap<UUID, Long>();

	private Config									pcfg;
	private Config									mcfg;
	private final Logger							log				= Logger.getLogger("Minecraft");

	public CWChat(CWChatPlugin cwc) {
		this.cwc = cwc;
	}
	
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(getPlugin());
		log("disabled");
	}

	public void onEnable() {
		UUID uuid;
		for (Player player : getServer().getOnlinePlayers()) {
			uuid = player.getUniqueId();
			if (!playerChat.containsKey(uuid)) {
				playerChat.put(uuid, ChatType.PUBLIC);
			}
		}
		
		chat = new Chat(this);
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ChatPlayerListener(this), cwc);

		pcfg = new PrefixConfig();
		pcfg.init();
		pcfg.load();
		
		cmds = new Commands(this);
		cmds.populateCommands();
		
		log("loaded successfully");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        return cmds.executeCommand(sender, lbl, args);
    }
	
	public void log(Object msg) {
		log.info("[CWChat " + getPlugin().getDescription().getVersion() + "]: " + msg.toString());
	}
	
	public void broadcast(String msg) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(msg);
		}
	}
	
	public CWChatPlugin getPlugin() {
		return cwc;
	}
	
	public Server getServer() {
		return getPlugin().getServer();
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