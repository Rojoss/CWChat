package net.clashwars.cwchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.clashwars.cwchat.bukkit.CWChatPlugin;
import net.clashwars.cwchat.bukkit.events.ChatPlayerListener;
import net.clashwars.cwchat.commands.Commands;
import net.clashwars.cwchat.config.Config;
import net.clashwars.cwchat.config.MainConfig;
import net.clashwars.cwchat.config.PrefixConfig;
import net.clashwars.cwchat.group.Group;
import net.clashwars.cwchat.group.Groups;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;


public class CWChat {
	private CWChatPlugin							cwc;
	private Commands              	   		        cmds;
	//private ArrayList<String>						spy				= new ArrayList<String>();
	private ArrayList<String>						staffC			= new ArrayList<String>();
	private ArrayList<String>						groupC			= new ArrayList<String>();
	public ArrayList<String>						factionC		= new ArrayList<String>();
	public ArrayList<String>						allyC			= new ArrayList<String>();
	public ArrayList<String>						truceC			= new ArrayList<String>();
	private List<String>							allowedLinks;
	
	private HashMap<String, ArrayList<Group>>	    invites			= new HashMap<String, ArrayList<Group>>();
	
	private String									pf	 			= ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "CW" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD;
	private boolean									enableGroups	= false;
	private String									format;
	private String									adminFormat;
	private String									groupFormat;
	private String									facFormat;
	private String									allyFormat;
	private String									truceFormat;
	
	private Groups									groups;
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
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ChatPlayerListener(this), cwc);

		pcfg = new PrefixConfig();
		pcfg.init();
		pcfg.load();

		mcfg = new MainConfig(this);
		mcfg.init();
		mcfg.load();

		groups = new Groups();
		
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
	
	public String getPrefix() {
		return pf;
	}
	
	public Config getMainConfig() {
        return mcfg;
    }

    public Config getPrefixConfig() {
        return pcfg;
    }
    
    public ArrayList<String> getStaffChat() {
    	return staffC;
    }
    
    public ArrayList<String> getGroupChat() {
    	return groupC;
    }
    
    public Groups getGroups() {
    	return groups;
    }
    
    public void setGroupsEnabled(Boolean set) {
    	this.enableGroups = set;
    }
    
    public Boolean getGroupsEnabled() {
    	return enableGroups;
    }
    
    public void setAllowedLinks(List<String> set) {
    	this.allowedLinks = set;
    }
    
    public List<String> getAllowedLinks() {
    	return allowedLinks;
    }
    
    public void setFormat(String set) {
    	this.format = set;
    }
    
    public String getFormat() {
    	return format;
    }
    
    public void setAdminFormat(String set) {
    	this.adminFormat = set;
    }
    
    public String getAdminFormat() {
    	return adminFormat;
    }
    
    public void setGroupFormat(String set) {
    	this.groupFormat = set;
    }
    
    public String getGroupFormat() {
    	return groupFormat;
    }
    
    public void setInvites(HashMap<String, ArrayList<Group>> set) {
    	this.invites = set;
    }
    
    public HashMap<String, ArrayList<Group>> getInvites() {
    	return invites;
    }
    
    public void setFacFormat(String set) {
    	this.facFormat = set;
    }
    
    public String getFacFormat() {
    	return facFormat;
    }
    
    public void setAllyFormat(String set) {
    	this.allyFormat = set;
    }
    
    public String getAllyFormat() {
    	return allyFormat;
    }

    public void setTruceFormat(String set) {
    	this.truceFormat = set;
    }
    
    public String getTruceFormat() {
    	return truceFormat;
    }
}