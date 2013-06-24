package net.clashwars.cwchat.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.clashwars.cwchat.CWChat;
import net.clashwars.cwchat.group.Group;
import net.clashwars.cwchat.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Commands {
    private CWChat                cwc;
    private String				  pf;
    private final List<Method> commands = new ArrayList<Method>();

    public Commands(CWChat cwc) {
        this.cwc = cwc;
        this.pf = cwc.getPrefix();
    }

    /* Start of commands */

    @Command(permissions = { "cwchat.reload" , "cwchat.*" }, aliases = { "cwchat" }, description = "Reload the configurations.", usage = "/<command>")
    public boolean CWChat(CommandSender sender, String label, String argument, String... args) {
    	
    	cwc.getPrefixConfig().load();
    	cwc.getMainConfig().load();
		sender.sendMessage(pf + "CWChat Reloaded");
		return true;
	}
    	
    @Command(permissions = { "cwchat.staffchat" , "cwchat.*" }, aliases = { "a" , "s" }, description = "Staff chat", usage = "/<command> [msg]")
    public boolean StaffChat(CommandSender sender, String label, String argument, String... args) {
    	
    	Player player = (Player) sender;
    	if (args.length <= 0) {
    		if (cwc.getStaffChat().contains(player.getName())) {
    			cwc.getStaffChat().remove(player.getName());
    			player.sendMessage(pf + ChatColor.AQUA + "Staff" + ChatColor.GOLD + " chat disabled!");
    		} else {
    			cwc.getStaffChat().add(player.getName());
    			player.sendMessage(pf + ChatColor.AQUA + "Staff" + ChatColor.GOLD + " chat enabled!");
    		}
    	} else {
    		HashSet<Player> recp = new HashSet<Player>();
    		for (Player online : cwc.getServer().getOnlinePlayers()) {
    			if (online.hasPermission("cwchat.staffchat") || online.hasPermission("cwchat.*"))
    				recp.add(online);
    		}
    		boolean had = cwc.getStaffChat().contains(player.getName());

    		cwc.getStaffChat().add(player.getName());
    		AsyncPlayerChatEvent chat = new AsyncPlayerChatEvent(false, player, Utils.implode(args, " "), recp);
    		cwc.getServer().getPluginManager().callEvent(chat);

    		if (!had)
    			cwc.getStaffChat().remove(player.getName());
    	}
    	
		return true;
	}
    
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
    
    @Command(permissions = { "cwchat.groups" , "cwchat.*" }, aliases = { "groups" , "group", "g" }, description = "Group managment and chat.", usage = "/<command>")
    public boolean Groups(CommandSender sender, String label, String argument, String... args) {
    	
    	Player player = (Player) sender;
		if (cwc.getGroupsEnabled() == false) {
			player.sendMessage(ChatColor.RED + "Groups aren't enabled here.");
			return true;
		}
		if (args.length <= 0) {
			
			if (!cwc.getGroups().hasGroup(player)) {
				player.sendMessage(pf + ChatColor.RED + "You are not in a group!");
				return true;
			}
			
			if (cwc.getGroupChat().contains(player.getName())) {
				cwc.getGroupChat().remove(player.getName());
				player.sendMessage(pf + "Group chat " + ChatColor.DARK_RED + "disabled");
			} else {
				cwc.getGroupChat().add(player.getName());
				player.sendMessage(pf + "Group chat " + ChatColor.GREEN + "enabled");
			}
			return true;
		} else {
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length <= 1) {
					player.sendMessage(pf + ChatColor.RED + "Invalid arguments. " + ChatColor.DARK_RED + "/g create <name>");
					return true;
				}

				Group group = cwc.getGroups().getGroup(args[1]);

				if (cwc.getGroups().hasGroup(player)) {
					player.sendMessage(pf + ChatColor.RED + "You're already in a group!");
					return true;
				}

				if (group != null) {
					player.sendMessage(pf + ChatColor.RED + "A group with this name already exists!");
					return true;
				}

				group = new Group(args[1], player);
				cwc.getGroups().addGroup(group);
				player.sendMessage(pf + "Group successfully created!");
				return true;
			} else if (args[0].equalsIgnoreCase("invite")) {
				if (args.length <= 1) {
					player.sendMessage(pf + ChatColor.RED + "Invalid arguments. " + ChatColor.DARK_RED + "/g invite <player>");
					return true;
				}

				Group mine = cwc.getGroups().getGroup(player);

				if (mine == null) {
					player.sendMessage(pf + ChatColor.RED + "You are not in a group!");
					return true;
				}

				if (!mine.isAdministrative(player)) {
					player.sendMessage(pf + ChatColor.RED + "You can't invite people to this group!");
					return true;
				}

				Player add = cwc.getServer().getPlayer(args[1]);

				if (add == null) {
					player.sendMessage(pf + ChatColor.RED + "Could not find player!");
					return true;
				}

				if (mine.containsMember(add)) {
					player.sendMessage(pf + ChatColor.RED + "This player is already in this group!");
					return true;
				}

				ArrayList<Group> gr = new ArrayList<Group>();
				if (cwc.getInvites().containsKey(add.getName())) {
					for (Group g : (gr = cwc.getInvites().get(add.getName()))) {
						if (g.getName().equals(mine.getName())) {
							player.sendMessage(pf + ChatColor.RED + "This player has already been invited to this group!");
							return true;
						}
					}
				}

				gr.add(mine);
				cwc.getInvites().put(add.getName(), gr);
				player.sendMessage(pf + "You have invited " + ChatColor.DARK_PURPLE + add.getName() + ChatColor.GOLD + " to the group!");
				add.sendMessage(pf + "You have been invited to the group: " + ChatColor.DARK_PURPLE + mine.getName());
				add.sendMessage(ChatColor.GOLD + "Use: " + ChatColor.DARK_PURPLE + "/g join " + mine.getName() + ChatColor.GOLD +  "to join the group!");
				return true;
			} else if (args[0].equalsIgnoreCase("join")) {
				if (args.length <= 1) {
					player.sendMessage(pf + ChatColor.RED + "Invalid arguments. " + ChatColor.DARK_RED + "/g join <groupname>");
					return true;
				}

				if (!cwc.getInvites().containsKey(player.getName())) {
					player.sendMessage(pf + ChatColor.RED + "You don't have any invitations!");
					return true;
				}

				if (cwc.getGroups().hasGroup(player)) {
					player.sendMessage(pf + ChatColor.RED + "Use " + ChatColor.DARK_RED + "/g leave" + ChatColor.RED + " before joining another group!");
					return true;
				}

				ArrayList<Group> inv = cwc.getInvites().get(player.getName());

				for (int i = 0; i < inv.size(); i++) {
					Group g = inv.get(i);

					if (g.getName().equals(args[1])) {
						player.sendMessage(pf + "You have joined the group: " + ChatColor.DARK_PURPLE + g.getName());
						g.broadcast(pf + ChatColor.DARK_PURPLE + player.getName() + ChatColor.GOLD + " joined your group!");

						g.getMembers().add(player);
						inv.remove(i);
						cwc.getInvites().put(player.getName(), inv);
						return true;
					}
				}

				player.sendMessage(pf + ChatColor.RED + "Could not find group!");
				return true;
			} else if (args[0].equalsIgnoreCase("kick")) {
				if (args.length <= 1) {
					player.sendMessage(pf + ChatColor.RED + "Invalid arguments. " + ChatColor.DARK_RED + "/g kick <player>");
					return true;
				}

				Group mine = cwc.getGroups().getGroup(player);

				if (mine == null) {
					player.sendMessage(pf + ChatColor.RED + "You are not in a group!");
					return true;
				}

				if (!mine.isAdministrative(player)) {
					player.sendMessage(pf + ChatColor.RED + "You can't kick people from your group!");
					return true;
				}

				Player add = cwc.getServer().getPlayer(args[1]);

				if (add == null) {
					player.sendMessage(pf + ChatColor.RED + "Could not find player!");
					return true;
				}

				if (!mine.containsMember(add)) {
					player.sendMessage(pf + ChatColor.RED + "This player isn't in your group!");
					return true;
				}

				if (mine.isAdministrative(add) && !mine.getOwner().equals(player)) {
					player.sendMessage(pf + ChatColor.RED + "You can't kick other admins from your group!");
					return true;
				}

				if (player.equals(add)) {
					player.sendMessage(pf + ChatColor.RED + "You can't kick yourself!");
					return true;
				}

				mine.kick(add);
				cwc.getGroupChat().remove(add.getName());

				add.sendMessage(pf + ChatColor.RED + "You have been kicked out of your group!");
				mine.broadcast(pf + ChatColor.DARK_PURPLE + add.getName() + ChatColor.GOLD + " got kicked from the group!");
				return true;
			} else if (args[0].equalsIgnoreCase("leave")) {
				Group mine = cwc.getGroups().getGroup(player);

				if (mine == null) {
					player.sendMessage(pf + ChatColor.RED + "You are not in a group!");
					return true;
				}

				if (mine.getOwner().equals(player)) {
					mine.broadcast(pf + ChatColor.RED + "The owner of your group has left, the group has been disbanded...");

					cwc.getGroups().removeGroup(mine);

					HashMap<String, ArrayList<Group>> inv = new HashMap<String, ArrayList<Group>>();
					inv.putAll(cwc.getInvites());
					cwc.getInvites().clear();
					
					for (int i = 0; i < cwc.getGroupChat().size(); i++) {
						if (cwc.getGroups().getGroup(cwc.getServer().getPlayer(cwc.getGroupChat().get(i))) == cwc.getGroups().getGroup(player)) {
							cwc.getGroupChat().remove(cwc.getGroupChat().get(i));
						}
					}

					for (Map.Entry<String, ArrayList<Group>> entry : inv.entrySet()) {
						ArrayList<Group> n = new ArrayList<Group>();

						for (Group g : entry.getValue()) {
							if (!g.getName().equals(mine.getName()))
								n.add(g);
						}
						cwc.getInvites().put(entry.getKey(), n);
					}
				} else {
					mine.kick(player);
					cwc.getGroupChat().remove(player.getName());
					player.sendMessage(pf + "You have left your group!");

					mine.broadcast(ChatColor.DARK_PURPLE + player.getName() + ChatColor.GOLD + " left the group!");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("promote")) {
				if (args.length <= 1) {
					player.sendMessage(pf + ChatColor.RED + "Invalid arguments. " + ChatColor.DARK_RED + "/g promote <player>");
					return true;
				}

				Group mine = cwc.getGroups().getGroup(player);

				if (mine == null) {
					player.sendMessage(pf + ChatColor.RED + "You are not in a group!");
					return true;
				}

				if (!mine.isAdministrative(player)) {
					player.sendMessage(pf + ChatColor.RED + "You are not an admin!");
					return true;
				}

				Player add = cwc.getServer().getPlayer(args[1]);

				if (add == null) {
					player.sendMessage(pf + ChatColor.RED + "Could not find player!");
					return true;
				}

				if (!mine.containsMember(add)) {
					player.sendMessage(pf + ChatColor.RED + "This player isn't in your group!");
					return true;
				}

				if (mine.isAdministrative(add)) {
					player.sendMessage(pf + ChatColor.RED + "This player is already an admin!");
					return true;
				}

				mine.promote(add);
				add.sendMessage(pf + "You have been promoted to admin inside your group!");
				player.sendMessage(pf + "You have promoted " + ChatColor.DARK_PURPLE + add.getName() + ChatColor.GOLD + " to admin!");
				return true;
			} else if (args[0].equalsIgnoreCase("demote")) {
				if (args.length <= 1) {
					player.sendMessage(pf + ChatColor.RED + "Invalid arguments. " + ChatColor.DARK_RED + "/g demote <player>");
					return true;
				}

				Group mine = cwc.getGroups().getGroup(player);

				if (mine == null) {
					player.sendMessage(pf + ChatColor.RED + "You are not in a group!");
					return true;
				}

				if (!mine.getOwner().equals(player)) {
					player.sendMessage(pf + ChatColor.RED + "You are not the owner of this group!");
					return true;
				}

				Player add = cwc.getServer().getPlayer(args[1]);

				if (add == null) {
					player.sendMessage(pf + ChatColor.RED + "Could not find player!");
					return true;
				}

				if (!mine.containsMember(add)) {
					player.sendMessage(pf + ChatColor.RED + "This player isn't in your group!");
					return true;
				}

				if (!mine.isAdministrative(add)) {
					player.sendMessage(pf + ChatColor.RED + "This player is not an admin!");
					return true;
				}

				mine.demote(add);
				add.sendMessage(pf + "You have been demoted to member inside your group!");
				player.sendMessage(pf + "You have demoted " + ChatColor.DARK_PURPLE + add.getName() + ChatColor.GOLD + " to member!");
				return true;
			} else if (args[0].equalsIgnoreCase("list")) {
				Group mine = cwc.getGroups().getGroup(player);

				if (mine == null) {
					player.sendMessage(pf + ChatColor.RED + "You are not in a group!");
					return true;
				}

				player.sendMessage(ChatColor.DARK_PURPLE + "Group Members: " + ChatColor.GOLD);
				String members = "";

				for (Player member : mine.getMembers()) {
					members += ChatColor.GOLD + member.getName() + ChatColor.DARK_GRAY + ", ";
				}

				if (!members.trim().isEmpty()) {
					player.sendMessage(ChatColor.GOLD + members.substring(0, members.length() - 2));
				}

				player.sendMessage(ChatColor.RED + "Admins:");
				String admins = "";

				for (Player admin : mine.getAdmins()) {
					admins += ChatColor.GOLD + admin.getName() + ChatColor.DARK_GRAY + ", ";
				}

				if (!admins.trim().isEmpty()) {
					player.sendMessage(ChatColor.GOLD + admins.substring(0, admins.length() - 2));
				}

				player.sendMessage(ChatColor.DARK_RED + "Owner: " + ChatColor.GOLD + mine.getOwner().getName());
				return true;
			} else {
				player.sendMessage(ChatColor.DARK_GRAY + "===== " + ChatColor.DARK_RED + "CW Groups" + ChatColor.DARK_GRAY + "=====");
				player.sendMessage(ChatColor.DARK_PURPLE + "/g help " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " This help page");
				player.sendMessage(ChatColor.DARK_PURPLE + "/g " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " Toggle group chat");
				player.sendMessage(ChatColor.DARK_PURPLE + "/g create <name> " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " Create a group");
				player.sendMessage(ChatColor.DARK_PURPLE + "/g join <group> " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " Join a group by invitation");
				player.sendMessage(ChatColor.DARK_PURPLE + "/g leave " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " Leave your current group");
				player.sendMessage(ChatColor.DARK_PURPLE + "/g list " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " List group members");
				player.sendMessage(ChatColor.DARK_GRAY + "=- " + ChatColor.DARK_RED + "Group Admin Commands");
				player.sendMessage(ChatColor.DARK_PURPLE + "/g invite <player> " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " Invite a player");
				player.sendMessage(ChatColor.DARK_PURPLE + "/g kick <player> " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " Kick a player");
				player.sendMessage(ChatColor.DARK_PURPLE + "/g promote <player> " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " Promote a player to admin");
				player.sendMessage(ChatColor.DARK_PURPLE + "/g demote <player> " + ChatColor.DARK_GRAY + "-" + ChatColor.GOLD + " Demote a player to member");
				return true;
			}
		}
	}
    

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
        			sender.sendMessage(pf + ChatColor.RED + "Only players can use this command.");
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
                                check: if (!sender.isOp() && permissions != null && permissions.length > 0) {
                                    for (String p : permissions) {
                                        if (sender.hasPermission(p)) {
                                            break check;
                                        }
                                    }
                                    sender.sendMessage(pf + ChatColor.RED + "insufficient permissions!" 
                            				+ ChatColor.GRAY + " - " + ChatColor.DARK_GRAY + "'" + ChatColor.DARK_RED + permissions[0] + ChatColor.DARK_GRAY + "'");
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
