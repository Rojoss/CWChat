package net.clashwars.cwchat.bukkit;

import net.clashwars.cwchat.CWChat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CWChatPlugin extends JavaPlugin {
	private CWChat	cwc;

	@Override
	public void onDisable() {
		cwc.onDisable();
	}

	@Override
	public void onEnable() {
		cwc = new CWChat(this);
		cwc.onEnable();
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        return cwc.onCommand(sender, cmd, lbl, args);
    }
}
