package com.clashwars.cwchat.commands;

import com.clashwars.cwchat.CWChat;
import com.clashwars.cwchat.util.Utils;
import com.clashwars.cwchat.wrappers.ChatType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Commands {
    private CWChat cwc;
    private final List<Method> commands = new ArrayList<Method>();

    public Commands(CWChat cwc) {
        this.cwc = cwc;
    }

    /* Start of commands */

    @Command(permissions = {"cwchat.reload", "cwchat.*"}, aliases = {"cwchat"}, description = "Reload the configurations.", usage = "/<command>")
    public boolean CWChat(CommandSender sender, String label, String argument, String... args) {

        cwc.getPrefixConfig().load();
        cwc.getMainConfig().load();
        sender.sendMessage(Utils.formatMsg("&6Reloaded"));
        return true;
    }

    @Command(permissions = {"cwchat.staffchat", "cwchat.*"}, aliases = {"a", "s"}, description = "Staff chat", usage = "/<command> [msg]")
    public boolean StaffChat(CommandSender sender, String label, String argument, String... args) {
        Player player = (Player) sender;
        if (!player.hasPermission("cwchat.staffchat")) {
            player.sendMessage(Utils.integrateColour(Utils.formatMsg("&cNo permissions to use this.")));
            return true;
        }

        UUID uuid = player.getUniqueId();
        if (args.length <= 0) {
            if (cwc.playerChat.get(uuid) == ChatType.STAFF) {
                cwc.playerChat.put(uuid, ChatType.PUBLIC);
                player.sendMessage(Utils.formatMsg("&bStaff &6chat disabled!"));
            } else {
                cwc.playerChat.put(uuid, ChatType.STAFF);
                player.sendMessage(Utils.formatMsg("&bStaff &6chat enabled!"));
            }
        } else {
            for (Player p : cwc.getServer().getOnlinePlayers()) {
                if (p.hasPermission("cwchat.staffchat") || p.hasPermission("cwchat.*") || p.isOp()) {
                    p.sendMessage(cwc.getChat().formatMessage(player, ChatType.STAFF, Utils.implode(args, " ")));
                }
            }
        }

        return true;
    }
    
    /*
    @Command(permissions = { "cwchat.message" , "cwchat.*" }, aliases = { "msg" , "message", "tell" }, description = "Send a private message.", usage = "/<command>")
    public boolean Message(CommandSender sender, String label, String argument, String... args) {
    	
    	if (args.length <= 0) {
    		sender.sendMessage(pf + ChatColor.RED + "Invalid arguments. " + ChatColor.DARK_RED + "/msg <player> <msg>");
    		return true;
    	}
    	
    	Player player = cwc.getServer().getPlayer(args[0]);
    	if (player == null) {
    		sender.sendMessage(pf + ChatColor.RED + "Player not found.");
    		return true;
    	}
    	
    	String message = Utils.implode(args, " ", 1);
    	if (message == null || message == "" || message == " ") {
    		sender.sendMessage(pf + ChatColor.RED + "Invalid message.");
    		return true;
    	}
    	
    	String name = "";
    	if (sender instanceof Player) {
			name = ((Player)sender).getDisplayName();
    	} else {
    		name = "&dserver";
    	}
    	
    	sender.sendMessage(Utils.integrateColour(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "you" + ChatColor.GRAY + " -> " + ChatColor.RED + player.getDisplayName() + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + message));
    	player.sendMessage(Utils.integrateColour(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + name + ChatColor.GRAY + " -> " + ChatColor.RED + "you" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + message));
    	
		return true;
	}
	*/
    
    /*
    @Command(permissions = {}, aliases = { "list" }, description = "List staff and players online.", usage = "/<command>")
    public boolean List(CommandSender sender, String label, String argument, String... args) {
    	
    	Player player = (Player) sender;
    	if (args.length <= 0) {
    		
    		String admins = "";
    		String mods = "";
    		String helpers = "";
    		int ai = 0;
    		int mi = 0;
    		int hi = 0;
    		
    		for (Player p : cwc.getServer().getOnlinePlayers()) {
    			if (player.canSee(p)) {
	    			if (p.hasPermission("cwchat.list.admin")) {
	    				if (ai == 0) {
	    					ai++;
	    					admins += ChatColor.RED + p.getName();
	    				} else {
	    					admins += ChatColor.DARK_GRAY + ", " + ChatColor.RED + p.getName();
	    				}
	    			} else if (p.hasPermission("cwchat.list.mod")) {
	    				if (mi == 0) {
	    					mi++;
	    					mods += ChatColor.GREEN + p.getName();
	    				} else {
	    					mods += ChatColor.DARK_GRAY + ", " + ChatColor.GREEN + p.getName();
	    				}
	    			} else if (p.hasPermission("cwchat.list.helper")) {
	    				if (hi == 0) {
	    					hi++;
	    					helpers += ChatColor.YELLOW + p.getName();
	    				} else {
	    					helpers += ChatColor.DARK_GRAY + ", " + ChatColor.YELLOW + p.getName();
	    				}
	    			}
    			}
			}
    		sender.sendMessage(ChatColor.DARK_GRAY + "========= " + ChatColor.DARK_RED + "Online Staff Members" + ChatColor.DARK_GRAY + " =========");
    		sender.sendMessage(ChatColor.DARK_GRAY + "=");
    		sender.sendMessage(ChatColor.DARK_GRAY + "=- " + ChatColor.DARK_RED + "Admins" + ChatColor.GRAY + ": " + admins);
    		sender.sendMessage(ChatColor.DARK_GRAY + "=- " + ChatColor.DARK_GREEN + "Mods" + ChatColor.GRAY + ": " + mods);
    		sender.sendMessage(ChatColor.DARK_GRAY + "=- " + ChatColor.GOLD + "Helpers" + ChatColor.GRAY + ": " + helpers);
    		sender.sendMessage(ChatColor.DARK_GRAY + "=");
    		sender.sendMessage(ChatColor.DARK_GRAY + "===== " + ChatColor.DARK_RED + "Use " + ChatColor.RED + "/list all" + ChatColor.DARK_RED + " to list all players" + ChatColor.DARK_GRAY + " =====");
    	} else {
    		String list = "";
    		int i = 0;
    		for (Player p : cwc.getServer().getOnlinePlayers()) {
				if (player.canSee(p)) {
					if (i == 0) {
						list += ChatColor.GRAY + p.getDisplayName();
					} else {
						list += ChatColor.DARK_GRAY + ", " + ChatColor.GRAY + p.getDisplayName();
					}
					i++;
				}
			}
    		sender.sendMessage(ChatColor.DARK_GRAY + "===== " + ChatColor.DARK_RED + "Online Players" + ChatColor.DARK_GRAY + " =====");
    		sender.sendMessage(Utils.integrateColour(list));
    	}
		return true;
	}
    */
    

    /* End of commands */

    public void populateCommands() {
        commands.clear();

        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(Command.class) && method.getReturnType().equals(boolean.class)) {
                commands.add(method);
            }
        }
    }

    public boolean executeCommand(CommandSender sender, String lbl, String... args) {
        try {
            for (Method method : commands) {
                Command command = method.getAnnotation(Command.class);
                String[] permissions = command.permissions();
                String[] aliases = command.aliases();
                String[] saliases = command.secondaryAliases();

                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.formatMsg("&cOnly players can use this command."));
                    return true;
                }

                for (String alias : aliases) {
                    if (alias.equalsIgnoreCase(lbl)) {
                        if ((saliases == null || saliases.length <= 0) && (!command.twoDimensional() || args.length == 0)) {
                            return (Boolean) method.invoke(this, sender, lbl, null, args);
                        }

                        if (args.length <= 0) {
                            continue;
                        }

                        for (String salias : saliases) {
                            if (salias.equalsIgnoreCase(args[0])) {
                                check:
                                if (!sender.isOp() && permissions != null && permissions.length > 0) {
                                    for (String p : permissions) {
                                        if (sender.hasPermission(p)) {
                                            break check;
                                        }
                                    }
                                    sender.sendMessage(Utils.formatMsg("&cinsufficient permissions! &7- &8'&4" + permissions[0] + "&8'"));
                                    return true;
                                }

                                return (Boolean) method.invoke(this, sender, lbl, args[0], Utils.trimFirst(args));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
