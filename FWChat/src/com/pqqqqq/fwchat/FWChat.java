package com.pqqqqq.fwchat;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.pqqqqq.fwchat.config.Config;
import com.pqqqqq.fwchat.config.GroupConfig;
import com.pqqqqq.fwchat.config.MainConfig;
import com.pqqqqq.fwchat.config.PrefixConfig;
import com.pqqqqq.fwchat.group.Group;
import com.pqqqqq.fwchat.group.Groups;
import com.pqqqqq.fwchat.listeners.ChatPlayerListener;
import com.pqqqqq.fwchat.util.Utils;
import com.pqqqqq.fwchat.wrappers.ChatGroup;

public class FWChat extends JavaPlugin {
	public final ArrayList<String>					nonGlobalC	= new ArrayList<String>();
	public final ArrayList<String>					globalC		= new ArrayList<String>();
	public final ArrayList<String>					shoutW		= new ArrayList<String>();
	public final ArrayList<String>					spy			= new ArrayList<String>();
	public final ArrayList<String>					adminC		= new ArrayList<String>();
	public final ArrayList<String>					local		= new ArrayList<String>();

	public final ArrayList<String>					factionC	= new ArrayList<String>();
	public final ArrayList<String>					allyC		= new ArrayList<String>();
	public final ArrayList<String>					truceC		= new ArrayList<String>();

	public static final ArrayList<String>			groupC		= new ArrayList<String>();
	public static final HashMap<Player, Player>		tps			= new HashMap<Player, Player>();
	public static final ArrayList<Player>			teleing		= new ArrayList<Player>();
	private final HashMap<String, ArrayList<Group>>	invites		= new HashMap<String, ArrayList<Group>>();
	//private SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
	private boolean									saidDate	= false;
	public static Groups							groups;

	public final char[]								cList		= new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };

	public List<String>								curWord		= new ArrayList<String>();
	public String									curCode		= null;

	public boolean									enabled		= true;

	private Config									cfg;
	private Config									pcfg;
	private Config									mcfg;
	private final Logger							log			= Logger.getLogger("Minecraft");

	@Override
	public void onDisable() {
		enabled = false;

		mcfg.save();
		Bukkit.getScheduler().cancelTasks(this);
		log("disabled");
	}

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ChatPlayerListener(this), this);

		cfg = new GroupConfig();
		cfg.init();
		cfg.load();

		pcfg = new PrefixConfig();
		pcfg.init();
		pcfg.load();

		mcfg = new MainConfig();
		mcfg.init();
		mcfg.load();

		groups = new Groups();

		Thread codeT = new Thread(new Runnable() {
			@Override
			public void run() {
				while (enabled) {
					try {
						int time = MainConfig.repM * 60 * 1000;
						int cur = 0;
						while (cur < time && enabled) {
							Thread.sleep(5);
							cur += 5;
						}

						if (enabled) {
							curCode = Utils.generateString(cList, MainConfig.cLen);
							broadcast(ChatColor.LIGHT_PURPLE + "[" + ChatColor.DARK_PURPLE + "FantasyWar" + ChatColor.LIGHT_PURPLE + "] "
									+ ChatColor.GOLD + "The first person to say \"" + ChatColor.GREEN + curCode + ChatColor.GOLD + "\" wins a prize!");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		codeT.start();

		Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				World def = getServer().getWorlds().get(0);
				long time = def.getTime() + 6000;
				final long ticksPerDay = 24000;

				final long days = time / ticksPerDay;
				time = time - days * ticksPerDay;

				int hour = (int) Math.floor(time / 1000);

				if (saidDate && hour != 6)
					saidDate = false;
				else if (!saidDate && hour == 6) {
					saidDate = true;

					MainConfig.startCal.add(Calendar.DAY_OF_MONTH, 1);
					MainConfig.startCal.set(Calendar.HOUR, 1);
					MainConfig.startCal.set(Calendar.HOUR_OF_DAY, 1);

					getServer().broadcastMessage(ChatColor.DARK_PURPLE + "Date: " + ChatColor.GOLD + getDate(def));
				}
			}
		}, 0, 10);

		log("loaded successfully");
	}

	public String getDate(World world) {
		DateFormatSymbols symb = new DateFormatSymbols();

		String month = symb.getMonths()[MainConfig.startCal.get(Calendar.MONTH)];
		int day = MainConfig.startCal.get(Calendar.DAY_OF_MONTH);
		int year = MainConfig.startCal.get(Calendar.YEAR);

		String toD = day + " " + month + " " + year;

		for (Map.Entry<String, String> m : MainConfig.monthNames.entrySet()) {
			toD = toD.replace(m.getKey(), m.getValue());
		}

		long time = world.getTime() + 6000;

		final long ticksPerDay = 24000;
		final long ticksPerHour = 1000;
		final double ticksPerMinute = 1000D / 60D;

		final long days = time / ticksPerDay;
		time = time - days * ticksPerDay;

		final long hours = time / ticksPerHour;
		time = time - hours * ticksPerHour;

		final long minutes = (long) Math.floor(time / ticksPerMinute);

		String hourS = (hours < 10 ? "0" + hours : Long.toString(hours));
		String minuteS = (minutes < 10 ? "0" + minutes : Long.toString(minutes));

		return toD + " " + hourS + ":" + minuteS;
	}

	public static void broadcast(String msg) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.sendMessage(msg);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("[FWChat] You need to be a player to use these commands");
			return true;
		}
		final Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("p")) {

		}
		if (cmd.getName().equalsIgnoreCase("e")) {
			if (!player.hasPermission("fwChat.empire") && !player.hasPermission("fwChat.*")) {
				player.sendMessage(ChatColor.RED + "[FwChat] Insufficient permissions!");
				return true;
			}
			if (nonGlobalC.contains(player.getName())) {
				player.sendMessage(ChatColor.GOLD + "You are no longer speaking in empire chat");
				nonGlobalC.remove(player.getName());
				return true;
			}
			player.sendMessage(ChatColor.GOLD + "You are now speaking in empire chat");
			globalC.remove(player.getName());
			nonGlobalC.add(player.getName());
			return true;
		} else if (cmd.getName().equalsIgnoreCase("fwchat")) {
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					if (!player.hasPermission("fwChat.reload") && !player.hasPermission("fwChat.*") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "[FWChat] Insufficient permissions");
						return true;
					}
					cfg.load();
					pcfg.load();
					mcfg.load();
					player.sendMessage(ChatColor.GREEN + "FWChat Reloaded");
					return true;
				} else if (args[0].equalsIgnoreCase("spy")) {
					if (!player.hasPermission("fwChat.spy") && !player.hasPermission("fwChat.*") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "[FWChat] Insufficient permissions");
						return true;
					}
					if (spy.contains(player.getName())) {
						player.sendMessage(ChatColor.GOLD + "You are not longer spying on chats");
						spy.remove(player.getName());
						return true;
					}
					spy.add(player.getName());
					player.sendMessage(ChatColor.GOLD + "You are now spying on chats");
					return true;
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("loc")) {
			if (local.remove(player.getName())) {
				player.sendMessage(ChatColor.BLUE + "Switched to global chat mode.");
			} else {
				local.add(player.getName());
				player.sendMessage(ChatColor.BLUE + "Switched to local chat mode.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("list")) {
			if (args.length <= 0)
				notD(player);
			else {
				ArrayList<Player> p = new ArrayList<Player>();
				ArrayList<ChatGroup> temp = GroupConfig.chats;
				temp.add(GroupConfig.defGroup);
				for (ChatGroup c : temp) {
					String sh = "";
					int i = 0;
					for (Player pla : getServer().getOnlinePlayers()) {
						if (!p.contains(pla) && (c.getListNode().isEmpty() || pla.hasPermission(c.getListNode()) || pla.isOp())) {
							sh += pla.getDisplayName() + ", ";
							i++;
							p.add(pla);
						}
					}
					if (i > 0 && c.getGroupName().equalsIgnoreCase(args[0])) {
						sender.sendMessage(ChatColor.GOLD + "Online " + Utils.integrateColour(c.getListName()) + ChatColor.GOLD + " members "
								+ ChatColor.GRAY + "(" + ChatColor.DARK_GRAY + i + ChatColor.GRAY + ")" + ChatColor.WHITE + ":");
						sender.sendMessage(ChatColor.GRAY + sh.substring(0, sh.length() - 2));
						sender.sendMessage(ChatColor.GOLD + "If you have questions ask one of these staff members.");
						sender.sendMessage(ChatColor.GOLD + "If there are no staff members online use " + ChatColor.DARK_PURPLE
								+ "/modreq <question>");
						return true;
					} else if (c.getGroupName().equalsIgnoreCase(args[0])) {
						sender.sendMessage(ChatColor.GOLD + "There are no " + Utils.integrateColour(c.getListName()) + ChatColor.GOLD
								+ " members online!");
						return true;
					}
				}
				sender.sendMessage(ChatColor.RED + "No such group");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("word")) {
			if (args.length >= 1) {
				if (!player.hasPermission("fwChat.word") && !player.hasPermission("fwChat.*")) {
					player.sendMessage(ChatColor.RED + "[FwChat] Insufficient permissions!");
					return true;
				}

				if (args.length >= 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set"))) {
					curWord.add(args[1].toLowerCase());
					player.sendMessage(ChatColor.GREEN + "Successfully added word: " + args[1]);
					return true;
				} else if (args.length >= 2
						&& (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete"))) {
					if (!curWord.contains(args[1].toLowerCase())) {
						player.sendMessage(ChatColor.RED + "[FWChat] \"" + args[1] + "\" is not on the list!");
						return true;
					}
					curWord.remove(args[1].toLowerCase());
					player.sendMessage(ChatColor.GREEN + "Successfully removed word: " + args[1]);
					return true;
				} else if (args[0].equalsIgnoreCase("list")) {
					player.sendMessage(ChatColor.DARK_PURPLE + "List of words: " + ChatColor.GOLD + curWord.toString());
					return true;
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("ooc")) {
			if (!player.hasPermission("fwChat.ooc") && !player.hasPermission("fwChat.*")) {
				player.sendMessage(ChatColor.RED + "[FwChat] Insufficient permissions!");
				return true;
			}
			if (globalC.contains(player.getName())) {
				player.sendMessage(ChatColor.GOLD + "You are no longer speaking in out of character chat");
				globalC.remove(player.getName());
				return true;
			}
			player.sendMessage(ChatColor.GOLD + "You are now speaking in out of character chat");
			nonGlobalC.remove(player.getName());
			globalC.add(player.getName());
			return true;
		} else if (cmd.getName().equalsIgnoreCase("chat")) {
			if (!player.hasPermission("fwChat.check-chat") && !player.hasPermission("fwChat.*")) {
				player.sendMessage(ChatColor.RED + "[FwChat] Insufficient permissions!");
				return true;
			}

			Player p = args.length >= 1 && (p = getServer().getPlayer(args[0])) != null ? p : player;

			if (nonGlobalC.contains(player.getName()))
				player.sendMessage(ChatColor.DARK_PURPLE + p.getName() + ChatColor.GOLD + " is speaking in Empire chat");
			else if (globalC.contains(player.getName()))
				player.sendMessage(ChatColor.DARK_PURPLE + p.getName() + ChatColor.GOLD + " is speaking in OOC chat");
			else
				player.sendMessage(ChatColor.DARK_PURPLE + p.getName() + ChatColor.GOLD + " is speaking in Public chat");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("lookup")) {
			if (!player.hasPermission("fwChat.lookup") && !player.hasPermission("fwChat.*")) {
				player.sendMessage(ChatColor.RED + "[FwChat] Insufficient permissions!");
				return true;
			}

			Player p = getServer().getPlayer(args[0]);

			if (p == null) {
				player.sendMessage(ChatColor.LIGHT_PURPLE + "[" + ChatColor.DARK_PURPLE + "FW" + ChatColor.LIGHT_PURPLE + "] " + ChatColor.GOLD
						+ "\"" + args[0] + "\" is " + ChatColor.RED + "OFFLINE");
				return true;
			}
			player.sendMessage(ChatColor.LIGHT_PURPLE + "[" + ChatColor.DARK_PURPLE + "FW" + ChatColor.LIGHT_PURPLE + "] " + ChatColor.GOLD
					+ p.getName() + " is " + ChatColor.GREEN + "ONLINE");
			ArrayList<ChatGroup> temp = GroupConfig.chats;
			temp.add(GroupConfig.defGroup);
			for (ChatGroup c : temp) {
				if ((c.getListNode().isEmpty() || p.hasPermission(c.getListNode())) || p.isOp()) {
					player.sendMessage(ChatColor.BLUE + "[FW] " + ChatColor.GOLD + p.getName() + " is in empire \"" + ChatColor.DARK_PURPLE
							+ c.getGroupName() + "\"");
					return true;
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("a")) {
			if (!player.hasPermission("fwChat.admin-chat") && !player.hasPermission("fwChat.*")) {
				player.sendMessage(ChatColor.RED + "[FwChat] Insufficient permissions!");
				return true;
			}

			if (args.length <= 0) {
				if (adminC.contains(player.getName())) {
					adminC.remove(player.getName());
					player.sendMessage(ChatColor.BLUE + "[FwChat] Admin chat deactivated!");
				} else {
					adminC.add(player.getName());
					player.sendMessage(ChatColor.BLUE + "[FwChat] Admin chat activated!");
				}
			} else {
				HashSet<Player> recp = new HashSet<Player>();
				for (Player online : getServer().getOnlinePlayers()) {
					if (online.hasPermission("fwChat.admin-chat") || online.hasPermission("fwChat.*"))
						recp.add(online);
				}
				boolean had = adminC.contains(player.getName());

				adminC.add(player.getName());
				AsyncPlayerChatEvent chat = new AsyncPlayerChatEvent(false, player, Utils.implode(args, " "), recp);
				getServer().getPluginManager().callEvent(chat);

				if (!had)
					adminC.remove(player.getName());
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("g")) {
			if (args.length <= 0) {
				if (groupC.contains(player.getName())) {
					groupC.remove(player.getName());
					player.sendMessage(ChatColor.RED + "Group chat disabled!");
				} else {
					groupC.add(player.getName());
					player.sendMessage(ChatColor.GREEN + "Group chat enabled!");
				}
				return true;
			} else {
				if (args[0].equalsIgnoreCase("create")) {
					if (args.length <= 1)
						return false;

					Group group = groups.getGroup(args[1]);

					if (groups.hasGroup(player)) {
						player.sendMessage(ChatColor.RED + "You're already in a group!");
						return true;
					}

					if (group != null) {
						player.sendMessage(ChatColor.RED + "A group with this name already exists!");
						return true;
					}

					group = new Group(args[1], player);
					groups.addGroup(group);
					player.sendMessage(ChatColor.GREEN + "Group successfully created!");
					return true;
				} else if (args[0].equalsIgnoreCase("invite")) {
					if (args.length <= 1)
						return false;

					Group mine = groups.getGroup(player);

					if (mine == null) {
						player.sendMessage(ChatColor.RED + "You are not in a group!");
						return true;
					}

					if (!mine.isAdministrative(player)) {
						player.sendMessage(ChatColor.RED + "You can't invite people to this group!");
						return true;
					}

					Player add = getServer().getPlayer(args[1]);

					if (add == null) {
						player.sendMessage(ChatColor.RED + "Could not find player!");
						return true;
					}

					if (mine.containsMember(add)) {
						player.sendMessage(ChatColor.RED + "This player is already in this group!");
						return true;
					}

					ArrayList<Group> gr = new ArrayList<Group>();
					if (invites.containsKey(add.getName())) {
						for (Group g : (gr = invites.get(add.getName()))) {
							if (g.getName().equals(mine.getName())) {
								player.sendMessage(ChatColor.RED + "This player has already been invited to this group!");
								return true;
							}
						}
					}

					gr.add(mine);
					invites.put(add.getName(), gr);
					player.sendMessage(ChatColor.GREEN + "You have invited " + ChatColor.GOLD + add.getName() + ChatColor.GREEN + " to the group!");
					add.sendMessage(ChatColor.GREEN + "You have been invited to the group: " + ChatColor.GOLD + mine.getName());
					add.sendMessage(ChatColor.GRAY + "Use \"/g join " + mine.getName() + "\" to join the group!");
					return true;
				} else if (args[0].equalsIgnoreCase("join")) {
					if (args.length <= 1)
						return false;

					if (!invites.containsKey(player.getName())) {
						player.sendMessage(ChatColor.RED + "You don't have any invitations!");
						return true;
					}

					if (groups.hasGroup(player)) {
						player.sendMessage(ChatColor.RED + "Use \"/g leave\" before joining another group!");
						return true;
					}

					ArrayList<Group> inv = invites.get(player.getName());

					for (int i = 0; i < inv.size(); i++) {
						Group g = inv.get(i);

						if (g.getName().equals(args[1])) {
							player.sendMessage(ChatColor.GREEN + "You have joined the group: " + ChatColor.GOLD + g.getName());
							g.broadcast(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " joined your group!");

							g.getMembers().add(player);
							inv.remove(i);
							invites.put(player.getName(), inv);
							return true;
						}
					}

					player.sendMessage(ChatColor.RED + "Could not find group!");
					return true;
				} else if (args[0].equalsIgnoreCase("kick")) {
					if (args.length <= 1)
						return false;

					Group mine = groups.getGroup(player);

					if (mine == null) {
						player.sendMessage(ChatColor.RED + "You are not in a group!");
						return true;
					}

					if (!mine.isAdministrative(player)) {
						player.sendMessage(ChatColor.RED + "You can't kick people from your group!");
						return true;
					}

					Player add = getServer().getPlayer(args[1]);

					if (add == null) {
						player.sendMessage(ChatColor.RED + "Could not find player!");
						return true;
					}

					if (!mine.containsMember(add)) {
						player.sendMessage(ChatColor.RED + "This player isn't in your group!");
						return true;
					}

					if (mine.isAdministrative(add) && !mine.getOwner().equals(player)) {
						player.sendMessage(ChatColor.RED + "You can't kick other admins from your group!");
						return true;
					}

					if (player.equals(add)) {
						player.sendMessage(ChatColor.RED + "You can't kick yourself!");
						return true;
					}

					mine.kick(add);

					add.sendMessage(ChatColor.RED + "You have been kicked out of your group!");
					mine.broadcast(ChatColor.GOLD + add.getName() + ChatColor.GREEN + " got kicked from the group!");
					return true;
				} else if (args[0].equalsIgnoreCase("leave")) {
					Group mine = groups.getGroup(player);

					if (mine == null) {
						player.sendMessage(ChatColor.RED + "You are not in a group!");
						return true;
					}

					if (mine.getOwner().equals(player)) {
						// owner left, disband
						mine.broadcast(ChatColor.RED + "The owner of your group has left, the group has been disbanded...");

						groups.removeGroup(mine);

						HashMap<String, ArrayList<Group>> inv = new HashMap<String, ArrayList<Group>>();
						inv.putAll(invites);
						invites.clear();

						for (Map.Entry<String, ArrayList<Group>> entry : inv.entrySet()) {
							ArrayList<Group> n = new ArrayList<Group>();

							for (Group g : entry.getValue()) {
								if (!g.getName().equals(mine.getName()))
									n.add(g);
							}
							invites.put(entry.getKey(), n);
						}
					} else {
						mine.kick(player);
						player.sendMessage(ChatColor.GREEN + "You have left your group!");

						mine.broadcast(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " left the group!");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("promote")) {
					if (args.length <= 1)
						return false;

					Group mine = groups.getGroup(player);

					if (mine == null) {
						player.sendMessage(ChatColor.RED + "You are not in a group!");
						return true;
					}

					if (!mine.isAdministrative(player)) {
						player.sendMessage(ChatColor.RED + "You are not an admin!");
						return true;
					}

					Player add = getServer().getPlayer(args[1]);

					if (add == null) {
						player.sendMessage(ChatColor.RED + "Could not find player!");
						return true;
					}

					if (!mine.containsMember(add)) {
						player.sendMessage(ChatColor.RED + "This player isn't in your group!");
						return true;
					}

					if (mine.isAdministrative(add)) {
						player.sendMessage(ChatColor.RED + "This player is already an admin!");
						return true;
					}

					mine.promote(add);
					add.sendMessage(ChatColor.GREEN + "You have been promoted to admin inside your group!");
					player.sendMessage(ChatColor.GREEN + "You have promoted " + ChatColor.GOLD + add.getName() + ChatColor.GREEN + " to admin!");
					return true;
				} else if (args[0].equalsIgnoreCase("demote")) {
					if (args.length <= 1)
						return false;

					Group mine = groups.getGroup(player);

					if (mine == null) {
						player.sendMessage(ChatColor.RED + "You are not in a group!");
						return true;
					}

					if (!mine.getOwner().equals(player)) {
						player.sendMessage(ChatColor.RED + "You are not the owner of this group!");
						return true;
					}

					Player add = getServer().getPlayer(args[1]);

					if (add == null) {
						player.sendMessage(ChatColor.RED + "Could not find player!");
						return true;
					}

					if (!mine.containsMember(add)) {
						player.sendMessage(ChatColor.RED + "This player isn't in your group!");
						return true;
					}

					if (!mine.isAdministrative(add)) {
						player.sendMessage(ChatColor.RED + "This player is not an admin!");
						return true;
					}

					mine.demote(add);
					add.sendMessage(ChatColor.GREEN + "You have been demoted to member inside your group!");
					player.sendMessage(ChatColor.GREEN + "You have demoted " + ChatColor.GOLD + add.getName() + ChatColor.GREEN + " to member!");
					return true;
				}/* else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("tele")) {
					if (args.length <= 1)
						return false;

					Group mine = groups.getGroup(player);

					if (mine == null) {
						player.sendMessage(ChatColor.RED + "You are not in a group!");
						return true;
					}

					Player add = getServer().getPlayer(args[1]);

					if (add == null) {
						player.sendMessage(ChatColor.RED + "Could not find player!");
						return true;
					}

					if (player.equals(add)) {
						player.sendMessage(ChatColor.RED + "You can't tp to yourself!");
						return true;
					}

					if (!mine.containsMember(add)) {
						player.sendMessage(ChatColor.RED + "This player isn't in your group!");
						return true;
					}

					if (tps.containsKey(player) || teleing.contains(player)) {
						player.sendMessage(ChatColor.RED + "You already have a pending tp request!");
						return true;
					}

					if (tps.containsValue(add) || teleing.contains(add)) {
						player.sendMessage(ChatColor.GOLD + add.getName() + ChatColor.RED + " already has a tp request");
						return true;
					}

					tps.put(player, add);
					add.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.DARK_AQUA + " requested a tp from you! Use " + ChatColor.AQUA
							+ "/g tpaccept " + ChatColor.DARK_AQUA + "to accept this tp");
					player.sendMessage(ChatColor.DARK_AQUA + "You have sent a tp request to " + ChatColor.AQUA + add.getName() + ChatColor.DARK_AQUA
							+ " don't move!");

					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								long start = System.currentTimeMillis();

								while ((start + 10000) > System.currentTimeMillis()) {
									if (!tps.containsKey(player))
										return;

									Thread.sleep(100);
								}

								player.sendMessage(ChatColor.BLUE + "Your tp request timed out!");
								tps.remove(player);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
					return true;
				} else if (args[0].equalsIgnoreCase("tpaccept")) {
					for (Map.Entry<Player, Player> entry : tps.entrySet()) {
						if (entry.getValue().equals(player)) {
							final Player tele = entry.getKey();

							tele.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.DARK_AQUA
									+ " accepted your tp, you will be teleported in 10 seconds! Don't move!");
							player.sendMessage(ChatColor.GREEN + "Accepted teleport request");
							tps.remove(tele);
							teleing.add(tele);

							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										Thread.sleep(1000);
										Location orig = tele.getLocation();

										long start = System.currentTimeMillis();
										while ((start + 10000) > System.currentTimeMillis()) {
											Location loc = tele.getLocation();

											if (orig.getBlockX() != loc.getBlockX() || orig.getBlockY() != loc.getBlockY()
													|| orig.getBlockZ() != loc.getBlockZ()) {
												tele.sendMessage(ChatColor.RED + "Teleportation cancelled");
												player.sendMessage(ChatColor.RED + "Teleportation cancelled");
												teleing.remove(tele);
												return;
											}

											Thread.sleep(500);
										}
										tele.teleport(player);
										tele.sendMessage(ChatColor.GREEN + "Teleporting...");
										player.sendMessage(ChatColor.GREEN + "Teleported to you");
										teleing.remove(tele);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}).start();
							return true;
						}
					}

					player.sendMessage(ChatColor.RED + "You don't have a request to accept!");
					return true;
				} else if (args[0].equalsIgnoreCase("tpdecline") || args[0].equalsIgnoreCase("tpdeny")) {
					for (Map.Entry<Player, Player> entry : tps.entrySet()) {
						if (entry.getValue().equals(player)) {
							tps.remove(entry.getKey());

							entry.getKey().sendMessage(ChatColor.GOLD + player.getName() + ChatColor.RED + " declined your tp request");
							player.sendMessage(ChatColor.GREEN + "Request declined");
							return true;
						}
					}

					player.sendMessage(ChatColor.RED + "You don't have a request to decline!");
					return true;
				}*/ else if (args[0].equalsIgnoreCase("list")) {
					Group mine = groups.getGroup(player);

					if (mine == null) {
						player.sendMessage(ChatColor.RED + "You are not in a group!");
						return true;
					}

					player.sendMessage(ChatColor.DARK_AQUA + "Members:");
					String members = "";

					for (Player member : mine.getMembers()) {
						members += member.getName() + ", ";
					}

					if (!members.trim().isEmpty()) {
						player.sendMessage(ChatColor.AQUA + members.substring(0, members.length() - 2));
					}

					player.sendMessage(ChatColor.DARK_AQUA + "Admins:");
					String admins = "";

					for (Player admin : mine.getAdmins()) {
						admins += admin.getName() + ", ";
					}

					if (!admins.trim().isEmpty()) {
						player.sendMessage(ChatColor.AQUA + admins.substring(0, admins.length() - 2));
					}

					player.sendMessage(ChatColor.DARK_AQUA + "Owner: " + ChatColor.AQUA + mine.getOwner().getName());
					return true;
				} else {
					player.sendMessage(ChatColor.BLUE + "FW Group Commands");
					player.sendMessage(ChatColor.AQUA + "/g help " + ChatColor.DARK_AQUA + "- This page");
					player.sendMessage(ChatColor.AQUA + "/g " + ChatColor.DARK_AQUA + "- Toggle group chat");
					player.sendMessage(ChatColor.AQUA + "/g create " + ChatColor.DARK_AQUA + "- Create a group");
					player.sendMessage(ChatColor.AQUA + "/g join " + ChatColor.DARK_AQUA + "- Join a group by invitation");
					player.sendMessage(ChatColor.AQUA + "/g leave " + ChatColor.DARK_AQUA + "- Leave your current group");
					player.sendMessage(ChatColor.AQUA + "/g list " + ChatColor.DARK_AQUA + "- List group members");
					player.sendMessage(ChatColor.AQUA + "/g tp " + ChatColor.DARK_AQUA + "- Request a tp");
					player.sendMessage(ChatColor.AQUA + "/g tpaccept " + ChatColor.DARK_AQUA + "- Accept tp request");
					player.sendMessage(ChatColor.AQUA + "/g tpdecline " + ChatColor.DARK_AQUA + "- Decline tp request");
					player.sendMessage(ChatColor.GRAY + "Admin Commands");
					player.sendMessage(ChatColor.AQUA + "/g invite " + ChatColor.DARK_AQUA + "- Invite a player");
					player.sendMessage(ChatColor.AQUA + "/g kick " + ChatColor.DARK_AQUA + "- Kick a player");
					player.sendMessage(ChatColor.AQUA + "/g promote " + ChatColor.DARK_AQUA + "- Promote a player to mod");
					player.sendMessage(ChatColor.AQUA + "/g demote " + ChatColor.DARK_AQUA + "- Demote a player to member");
					return true;
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("date")) {
			if (!player.hasPermission("fwChat.date") && !player.hasPermission("fwChat.*")) {
				player.sendMessage(ChatColor.RED + "[FwChat] Insufficient permissions!");
				return true;
			}

			player.sendMessage(ChatColor.DARK_PURPLE + "Date: " + ChatColor.GOLD + getDate(player.getWorld()));
			return true;
		}
		return false;
	}

	public void log(Object msg) {
		log.info("[FWChat " + getDescription().getVersion() + "]: " + msg.toString());
	}

	private void notD(Player sender) {
		sender.sendMessage(ChatColor.DARK_PURPLE + "Online staff members:");
		ArrayList<Player> p = new ArrayList<Player>();
		for (ChatGroup cg : GroupConfig.chats) {
			if (cg.isDisplayDefault()) {
				String sh = "";
				int i = 0;
				for (Player pla : getServer().getOnlinePlayers()) {
					if (!p.contains(pla) && (cg.getListNode().isEmpty() || pla.hasPermission(cg.getListNode()) || pla.isOp())) {
						sh += pla.getDisplayName() + ", ";
						i++;
						p.add(pla);
					}
				}
				sender.sendMessage(Utils.integrateColour(cg.getListName()) + ChatColor.GRAY + " (" + ChatColor.DARK_GRAY + i + ChatColor.GRAY + ")"
						+ ChatColor.WHITE + ": " + ChatColor.GOLD + (i > 0 ? sh.substring(0, sh.length() - 2) : sh.trim()));
			}
		}
		sender.sendMessage(ChatColor.GOLD + "If you have questions ask one of these staff members.");
		sender.sendMessage(ChatColor.GOLD + "If there are no staff members online use " + ChatColor.DARK_PURPLE + "/modreq <question>");
		sender.sendMessage(ChatColor.GOLD + "If you want to see the other players online use " + ChatColor.DARK_PURPLE + "/list <empire>");
	}

	public String decolor(String string) {
		string.replaceAll("&1", "");
		return string;
	}
}