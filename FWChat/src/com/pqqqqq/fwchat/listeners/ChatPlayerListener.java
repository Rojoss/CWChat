package com.pqqqqq.fwchat.listeners;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Rel;
import com.pqqqqq.fwchat.FWChat;
import com.pqqqqq.fwchat.config.GroupConfig;
import com.pqqqqq.fwchat.config.MainConfig;
import com.pqqqqq.fwchat.config.PrefixConfig;
import com.pqqqqq.fwchat.group.Group;
import com.pqqqqq.fwchat.util.Utils;
import com.pqqqqq.fwchat.wrappers.ChatGroup;
import com.pqqqqq.fwchat.wrappers.ChatPrefix;

public class ChatPlayerListener implements Listener {
	private FWChat							predecessor;
	//private ArrayList<String>				tempnm			= new ArrayList<String>();
	private final HashMap<String, String>	suppressGroup	= new HashMap<String, String>();
	private Pattern							urlPattern		= Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,3})(/\\S*)?$");

	public ChatPlayerListener(FWChat predecessor) {
		this.predecessor = predecessor;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void chat(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;

		try {
			final Player player = event.getPlayer();
			String message = event.getMessage();
			List<Player> re = new ArrayList<Player>(event.getRecipients());
			ChatGroup priority = null;

			boolean shout = message.startsWith("!") && (player.hasPermission("fwChat.shout") || player.hasPermission("fwChat.*") || player.isOp());
			boolean autoS = player.hasPermission("fwChat.global") || player.hasPermission("fwChat.*") || player.isOp()
					|| !predecessor.local.contains(player.getName());

			if (!player.hasPermission("fwChat.shout.bypass-time") && !player.isOp() && shout && predecessor.shoutW.contains(player.getName())) {
				player.sendMessage(ChatColor.RED + "Wait 10 more seconds to shout!");
				event.setCancelled(true);
				return;
			}

			if (!shout && !autoS) {
				for (ChatGroup cG : GroupConfig.chats) {
					if (player.hasPermission("fwChat.*") || player.hasPermission("*") || player.isOp() || cG.getPermissionNode() != null
							&& (cG.getPermissionNode().isEmpty() || player.hasPermission(cG.getPermissionNode()))) {
						if (priority == null || priority.getPriority() < cG.getPriority())
							priority = cG;
					}
				}
			}
			//System.out.println("priority: " + priority.getGroupName());

			FPlayer fp = FPlayers.i.get(player);
			Faction faction = fp == null ? null : fp.getFaction();
			String fFormat = null;

			if (predecessor.adminC.contains(player.getName())) {
				for (Player recp : re) {
					if (recp == null || recp.equals(player))
						continue;

					if (!recp.hasPermission("fwChat.admin-chat") && !recp.hasPermission("fwChat.*"))
						event.getRecipients().remove(recp);
				}
			} else if (predecessor.globalC.contains(player.getName()) && !shout) {
				for (Player recp : re) {
					if (recp == null || recp.equals(player))
						continue;

					if (!predecessor.globalC.contains(recp.getName())) {
						event.getRecipients().remove(recp);
					}
				}
			} else if (predecessor.nonGlobalC.contains(player.getName()) && !shout) {
				for (Player recp : re) {
					if (recp == null || recp.equals(player))
						continue;

					/*System.out.println("Check against: " + recp.getName());
					System.out.println(recp.hasPermission("fwChat.global"));
					System.out.println(recp.hasPermission(priority.getPermissionNode()));*/
					if (priority != null
							&& (!recp.hasPermission("fwChat.global") && !recp.hasPermission("fwChat.*") && !recp.isOp() && (priority
									.getPermissionNode() == null || priority.getPermissionNode().isEmpty() || !recp.hasPermission(priority
									.getPermissionNode()))) && !predecessor.spy.contains(recp.getName())) {
						event.getRecipients().remove(recp);
					}
				}
			} else if (!shout) {
				if (faction != null && predecessor.factionC.contains(player.getName())) {
					fFormat = MainConfig.factionFormat;

					event.getRecipients().clear();
					event.getRecipients().addAll(faction.getOnlinePlayers());
				} else if (faction != null && predecessor.allyC.contains(player.getName())) {
					fFormat = MainConfig.allyFormat;

					event.getRecipients().clear();
					event.getRecipients().addAll(faction.getOnlinePlayers());

					Field relWish = Faction.class.getDeclaredField("relationWish");
					relWish.setAccessible(true);
					Map<String, Rel> wishes = (Map<String, Rel>) relWish.get(faction);

					for (Map.Entry<String, Rel> entry : wishes.entrySet()) {
						Faction thisFaction = Factions.i.get(entry.getKey());

						if (thisFaction == null)
							continue;

						Rel relation = faction.getRelationTo(thisFaction, true);

						if (relation != Rel.ALLY)
							continue;

						event.getRecipients().addAll(thisFaction.getOnlinePlayers());
					}
				} else if (faction != null && predecessor.truceC.contains(player.getName())) {
					fFormat = MainConfig.truceFormat;

					event.getRecipients().clear();
					event.getRecipients().addAll(faction.getOnlinePlayers());

					Field relWish = Faction.class.getDeclaredField("relationWish");
					relWish.setAccessible(true);
					Map<String, Rel> wishes = (Map<String, Rel>) relWish.get(faction);

					for (Map.Entry<String, Rel> entry : wishes.entrySet()) {
						Faction thisFaction = Factions.i.get(entry.getKey());

						if (thisFaction == null)
							continue;

						Rel relation = faction.getRelationTo(thisFaction, true);

						if (relation != Rel.ALLY && relation != Rel.TRUCE)
							continue;

						event.getRecipients().addAll(thisFaction.getOnlinePlayers());
					}
				} else {
					if (!predecessor.nonGlobalC.contains(player.getName())) {
						ChatGroup def = GroupConfig.defGroup;
						if (!shout && !autoS)
							priority = def;
						if (re != null && !shout && !autoS) {
							for (Player recp : re) {
								try {
									if (recp == null || !event.getRecipients().contains(recp) || recp.equals(player))
										continue;
									if (!player.getWorld().equals(recp.getWorld())
											|| player.getLocation().distance(recp.getLocation()) > def.getChatRadius())
										event.getRecipients().remove(recp);
								} catch (Exception e) {
									continue;
								}
							}
						}
					}
				}
			}

			if (shout) {
				fFormat = null;
				message = message.substring(1);
				predecessor.shoutW.add(player.getName());
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(10000);
							predecessor.shoutW.remove(player.getName());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			if (shout || autoS)
				priority = null;

			ChatPrefix dP = PrefixConfig.defPrefix;
			ArrayList<String> prefixes = new ArrayList<String>();
			ArrayList<String> suffixes = new ArrayList<String>();
			if (dP.getChatPrefix() != null && !dP.getChatPrefix().trim().isEmpty())
				prefixes.add(dP.getChatPrefix());
			if (dP.getChatSuffix() != null && !dP.getChatSuffix().trim().isEmpty())
				suffixes.add(dP.getChatSuffix());
			for (ChatPrefix cP : PrefixConfig.prefixes) {
				if (player.hasPermission("fwChat.*") || player.hasPermission("*") || player.isOp() || player.hasPermission(cP.getPermissionNode())) {

					if (player.hasPermission("fwChat.owner") && !cP.isOwner())
						continue;

					if (cP.getChatPrefix() != null && !cP.getChatPrefix().trim().isEmpty())
						prefixes.add(cP.getChatPrefix());
					if (cP.getChatSuffix() != null && !cP.getChatSuffix().trim().isEmpty())
						suffixes.add(cP.getChatSuffix());
				}
			}

			if (!player.hasPermission("fwChat.bypass-link-block") && !player.hasPermission("fwChat.*")) {
				String ne = "";
				words: for (String word : message.split(" ")) {
					if (!urlPattern.matcher(word).matches()) {
						ne += word + " ";
						continue;
					}

					for (String allowed : MainConfig.allowedLinks) {
						allowed = allowed.replace("http://", "").replace("https://", "");
						String tWord = word.replace("http://", "").replace("https://", "");
						if (allowed.equalsIgnoreCase(tWord)) {
							ne += word + " ";
							continue words;
						}
					}

					ne += "*BLOCKED* ";
				}
				message = ne.isEmpty() ? ne : ne.substring(0, ne.length() - 1);
			}

			if (predecessor.curWord != null && !predecessor.curWord.isEmpty()) {
				out: for (String word : message.split(" ")) {
					for (String oppose : predecessor.curWord) {
						if (word.equalsIgnoreCase(oppose)) {
							player.sendMessage(ChatColor.GREEN + "You were the first to say \"" + oppose + "\"!");
							ItemStack i = MainConfig.prizeWord;

							player.sendMessage(ChatColor.GOLD + "You win " + i.getAmount() + " " + i.getType().name().toLowerCase().replace("_", " ")
									+ "(s)");

							FWChat.broadcast(ChatColor.LIGHT_PURPLE + "[" + ChatColor.DARK_PURPLE + "FantasyWar" + ChatColor.LIGHT_PURPLE + "] "
									+ ChatColor.GOLD + player.getName() + " found the hidden word \"" + oppose + "\" and got a prize!");

							player.getInventory().addItem(i);
							predecessor.curWord.remove(oppose);
							break out;
						}
					}
				}
			}

			if (predecessor.curCode != null) {
				if (message.trim().equalsIgnoreCase(predecessor.curCode) || message.trim().equalsIgnoreCase(predecessor.curCode + ".")) {
					player.sendMessage(ChatColor.GREEN + "You were the first to say \"" + predecessor.curCode + "\"!");
					ItemStack i = MainConfig.prizeCode;

					player.sendMessage(ChatColor.GOLD + "You win " + i.getAmount() + " " + i.getType().name().toLowerCase().replace("_", " ") + "(s)");

					FWChat.broadcast(ChatColor.LIGHT_PURPLE + "[" + ChatColor.DARK_PURPLE + "FantasyWar" + ChatColor.LIGHT_PURPLE + "] "
							+ ChatColor.GOLD + player.getName() + " said the code first and got a prize!");

					player.getInventory().addItem(i);
					predecessor.curCode = null;
				}
			}

			// PlayerProfile prof = Users.getProfile(player.getName());
			String format = Utils.integrateColour(fFormat != null ? fFormat : predecessor.adminC.contains(player.getName()) ? MainConfig.adminFormat
					: MainConfig.format);
			format = format.replace("{CHAT}", Utils.integrateColour((priority != null ? priority.getPrefix() : ""))).trim();
			format = format.replace("{PREFIX}", Utils.integrateColour(Utils.implode(prefixes, " ").trim()));
			format = format.replace("{SUFFIX}", Utils.integrateColour(Utils.implode(suffixes, " ").trim()));
			format = format.replace("{DISPLAYNAME}", Utils.integrateColour(player.getDisplayName())).trim();
			if (faction != null)
				format = format.replace("{FACTION}", Utils.integrateColour(faction.getTag())).trim();
			format = format.replace("{NAME}", Utils.integrateColour(player.getName())).trim();
			format = format.replace(
					"{MESSAGE}",
					(player.hasPermission("fwChat.colour") || player.hasPermission("fwChat.*") || player.isOp() ? Utils.integrateColour(message,
							player.hasPermission("fwchat.special") || player.isOp()) : message)).trim();
			// format = format.replace("%", "#").trim(); */

			if (fFormat != null) {
				event.setCancelled(true);

				format = format.replace("[fac]", faction.getTag());
				format = format.replace("[rank]", faction.getFPlayerLeader() != null && faction.getFPlayerLeader().equals(fp) ? "**" : faction
						.getFPlayersWhereRole(Rel.OFFICER).contains(fp) ? "*" : "");

				for (Player recp : event.getRecipients()) {
					FPlayer rp = FPlayers.i.get(recp);
					Faction rfaction = rp == null ? null : rp.getFaction();

					if (rfaction == null) {
						recp.sendMessage(format.replace("%R", ChatColor.GRAY.toString()));
					} else {
						if (faction.equals(rfaction)) {
							recp.sendMessage(format.replace("%R", ChatColor.GREEN.toString()));
						} else {
							Rel relation = faction.getRelationTo(rfaction, true);
							switch (relation) {
								case ALLY:
									recp.sendMessage(format.replace("%R", ChatColor.DARK_PURPLE.toString()));
									break;
								case TRUCE:
									recp.sendMessage(format.replace("%R", ChatColor.LIGHT_PURPLE.toString()));
									break;
								case ENEMY:
									recp.sendMessage(format.replace("%R", ChatColor.RED.toString()));
									break;
								case NEUTRAL:
									recp.sendMessage(format.replace("%R", ChatColor.GRAY.toString()));
									break;
							}
						}
					}
				}
				return;
			}

			/*if (predecessor.nonGlobalC.contains(player.getName()) && !shout && !autoS)
				event.setFormat(format);
			else {*/
			event.setCancelled(true);
			for (Player rec : event.getRecipients()) {
				rec.sendMessage(format.replace("[FACTION]", "").trim());
			}
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void chatMonitor(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;

		event.setCancelled(true);
		for (Player p : event.getRecipients()) {
			p.sendMessage(event.getFormat());
		}
	}

	/*@EventHandler
	public void interact(PlayerInteractEntityEvent event) {
		final Player interacter = event.getPlayer();

		if (tempnm.contains(interacter.getName()))
			return;

		if (event.getRightClicked() instanceof Player) {
			Player interactee = (Player) event.getRightClicked();

			try {
				if (CitizensAPI.getNPCRegistry().isNPC(interactee))
					return;
			} catch (Throwable e) {
			}

			ArrayList<ChatGroup> temp = GroupConfig.chats;
			temp.add(GroupConfig.defGroup);
			for (ChatGroup c : temp) {
				if ((c.getListNode().isEmpty() || interactee.hasPermission(c.getListNode())) || interactee.isOp()) {
					if (!c.isIgnoreClick())
						interacter.sendMessage(ChatColor.LIGHT_PURPLE + "[" + ChatColor.DARK_PURPLE + "FW" + ChatColor.LIGHT_PURPLE + "] "
								+ ChatColor.GOLD + interactee.getName() + " is in empire \"" + c.getGroupName() + "\"");
					tempnm.add(interacter.getName());
					Bukkit.getScheduler().scheduleSyncDelayedTask(predecessor, new Runnable() {

						@Override
						public void run() {
							tempnm.remove(interacter.getName());
						}
					}, 40);
					return;
				}
			}
		}
	}*/

	@EventHandler(priority = EventPriority.MONITOR)
	public void join(PlayerJoinEvent event) {
		/*
		 * Player player = event.getPlayer(); CraftPlayer pl = (CraftPlayer)
		 * player; player.setLocalizedName(player.getLocalizedName() + "hi");
		 * 
		 * Packet29DestroyEntity p29 = new
		 * Packet29DestroyEntity(pl.getEntityId()); Packet20NamedEntitySpawn p20
		 * = new Packet20NamedEntitySpawn( pl.getHandle());
		 * 
		 * for (Player p : Bukkit.getOnlinePlayers()) { if (p.equals(player))
		 * continue; ((CraftPlayer)
		 * p).getHandle().netServerHandler.sendPacket(p29); ((CraftPlayer)
		 * p).getHandle().netServerHandler.sendPacket(p20); }
		 */

		// player.setDisplayName(player.getName() + "test");
		/*
		 * CraftPlayer cp = (CraftPlayer) player; EntityPlayer ep =
		 * cp.getHandle();
		 * 
		 * ep.netServerHandler.sendPacket(new Packet29DestroyEntity(player
		 * .getEntityId())); ep.name = ChatColor.GOLD + ep.name +
		 * ChatColor.WHITE; ep.netServerHandler.sendPacket(new
		 * Packet20NamedEntitySpawn(ep));
		 */
	}

	/*@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (event.getMessage().startsWith("*")) {
			event.setFormat("");
			for (Entity entity : event.getPlayer().getNearbyEntities(predecessor.getConfig().getDouble("emote-radius"),
					predecessor.getConfig().getDouble("emote-radius"), predecessor.getConfig().getDouble("emote-radius"))) {
				if (entity instanceof Player) {
					Player player = (Player) entity;
					player.sendMessage(ChatColor.GRAY + event.getMessage().replaceAll("\\*", event.getPlayer().getDisplayName() + " "));
				}
			}
			event.getPlayer().sendMessage(ChatColor.GRAY + event.getMessage().replaceAll("\\*", event.getPlayer().getDisplayName() + " "));
			event.setCancelled(true);
		}
	}*/

	@EventHandler(priority = EventPriority.LOWEST)
	public void dmg(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;

			Player sh11 = null;

			if (ev.getDamager() instanceof Player)
				sh11 = (Player) ev.getDamager();
			else if (ev.getDamager() instanceof Projectile) {
				Projectile p = (Projectile) ev.getDamager();

				if (p.getShooter() instanceof Player)
					sh11 = (Player) p.getShooter();
			}

			final Player sh = sh11;

			if (sh != null) {
				if (ev.getEntity() instanceof Player) {
					final Player pl = (Player) ev.getEntity();

					Group gPL = FWChat.groups.getGroup(pl);
					Group gSH = FWChat.groups.getGroup(sh);

					if (gPL != null && gSH != null && gPL.getName().equals(gSH.getName())) {
						if (!suppressGroup.containsKey(sh.getName()) || !suppressGroup.get(sh.getName()).equalsIgnoreCase(pl.getName())) {
							sh.sendMessage(ChatColor.RED + "You are attacking one of your group members!");
							suppressGroup.put(sh.getName(), pl.getName());

							Bukkit.getScheduler().scheduleSyncDelayedTask(predecessor, new Runnable() {

								@Override
								public void run() {
									suppressGroup.remove(sh.getName());
								}
							}, 800);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void cmd(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		String message = event.getMessage();

		if (message.equalsIgnoreCase("/f c") || message.toLowerCase().startsWith("/f c ")) {
			event.setCancelled(true);

			if (!player.hasPermission("fwchat.factions.chat") && !player.isOp()) {
				player.sendMessage(ChatColor.RED + "[FwChat] Insufficient permissions!");
				return;
			}

			String[] split = message.split(" ");

			if (split.length >= 3) {
				String arg = split[2];

				if (arg.equalsIgnoreCase("f")) {
					predecessor.factionC.add(player.getName());
					predecessor.allyC.remove(player.getName());
					predecessor.truceC.remove(player.getName());
					player.sendMessage(ChatColor.GOLD + "You are now speaking in faction chat");
					return;
					// TODO: Faction only
				} else if (arg.equalsIgnoreCase("a")) {
					predecessor.factionC.remove(player.getName());
					predecessor.allyC.add(player.getName());
					predecessor.truceC.remove(player.getName());
					player.sendMessage(ChatColor.GOLD + "You are now speaking in faction/alliance chat");
					return;
					// TODO: Alliance chat
				} else if (arg.equalsIgnoreCase("t")) {
					predecessor.factionC.remove(player.getName());
					predecessor.allyC.remove(player.getName());
					predecessor.truceC.add(player.getName());
					player.sendMessage(ChatColor.GOLD + "You are now speaking in faction/alliance/truce chat");
					return;
					// TODO: Truce chat
				} else if (arg.equalsIgnoreCase("p")) {
					predecessor.factionC.remove(player.getName());
					predecessor.allyC.remove(player.getName());
					predecessor.truceC.remove(player.getName());
					player.sendMessage(ChatColor.GOLD + "You are now speaking in public chat");
					return;
					// TODO: Public chat
				}
			}

			if (predecessor.factionC.contains(player.getName())) {
				predecessor.factionC.remove(player.getName());
				predecessor.allyC.add(player.getName());
				player.sendMessage(ChatColor.GOLD + "You are now speaking in faction/alliance chat");
			} else if (predecessor.allyC.contains(player.getName())) {
				predecessor.allyC.remove(player.getName());
				predecessor.truceC.add(player.getName());
				player.sendMessage(ChatColor.GOLD + "You are now speaking in faction/alliance/truce chat");
			} else if (predecessor.truceC.contains(player.getName())) {
				predecessor.truceC.remove(player.getName());
				player.sendMessage(ChatColor.GOLD + "You are now speaking in public chat");
			} else {
				predecessor.factionC.add(player.getName());
				player.sendMessage(ChatColor.GOLD + "You are now speaking in faction chat");
			}
			// TODO: cycle
		}
	}
}
