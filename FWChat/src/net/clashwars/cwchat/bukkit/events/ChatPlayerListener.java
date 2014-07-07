package net.clashwars.cwchat.bukkit.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.clashwars.cwchat.CWChat;
import net.clashwars.cwchat.config.PrefixConfig;
import net.clashwars.cwchat.group.Group;
import net.clashwars.cwchat.util.Utils;
import net.clashwars.cwchat.wrappers.ChatPrefix;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;

public class ChatPlayerListener implements Listener {
	private CWChat							cwc;
	private final HashMap<String, String>	suppressGroup	= new HashMap<String, String>();
	private Pattern							urlPattern		= Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,3})(/\\S*)?$");

	public ChatPlayerListener(CWChat cwc) {
		this.cwc = cwc;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void chat(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;

		try {
			final Player player = event.getPlayer();
			String message = event.getMessage();
			List<Player> re = new ArrayList<Player>(event.getRecipients());

			/* Admin chat */
			if (cwc.getStaffChat().contains(player.getName())) {
				for (Player recp : re) {
					if (recp == null || recp.equals(player))
						continue;

					if (!recp.hasPermission("cwchat.staffchat") && !recp.hasPermission("cwchat.*"))
						event.getRecipients().remove(recp);
				}
			}
			
			/* Faction chat */
			UPlayer fp = UPlayer.get(player);
			Faction faction = fp.getFaction();
			String fFormat = null;
			
			if (faction != null && cwc.factionC.contains(player.getName())) {
			    fFormat = cwc.getFacFormat();

			    event.getRecipients().clear();
			    event.getRecipients().addAll(faction.getOnlinePlayers());
			} else if (faction != null && cwc.allyC.contains(player.getName())) {
			    fFormat = cwc.getAllyFormat();

			    event.getRecipients().clear();
			    event.getRecipients().addAll(faction.getOnlinePlayers());

			    Map<String, Rel> wishes = faction.getRelationWishes();

			    for (Map.Entry<String, Rel> entry : wishes.entrySet()) {
			        Faction thisFaction = Faction.get(entry.getKey());

			        if (thisFaction == null)
			            continue;

			        Rel relation = faction.getRelationTo(thisFaction, true);

			        if (relation != Rel.ALLY)
			            continue;

			        event.getRecipients().addAll(thisFaction.getOnlinePlayers());
			    }
			} else if (faction != null && cwc.truceC.contains(player.getName())) {
			    fFormat = cwc.getTruceFormat();

			    event.getRecipients().clear();
			    event.getRecipients().addAll(faction.getOnlinePlayers());

			    Map<String, Rel> wishes = faction.getRelationWishes();

			    for (Map.Entry<String, Rel> entry : wishes.entrySet()) {
			        Faction thisFaction = Faction.get(entry.getKey());

			        if (thisFaction == null)
			            continue;

			        Rel relation = faction.getRelationTo(thisFaction, true);

			        if (relation != Rel.ALLY && relation != Rel.TRUCE)
			            continue;

			        event.getRecipients().addAll(thisFaction.getOnlinePlayers());
			    }
			}
			
			
			
			
			/* Group chat */
			if (cwc.getGroupChat().contains(player.getName())) {
				for (Player recp : re) {
					if (recp == null || recp.equals(player))
						continue;
					
					if (!cwc.getGroups().hasGroup(player) || cwc.getGroups().getGroup(player) == null) {
						continue;
					}
					Group group = cwc.getGroups().getGroup(player);

					if (!cwc.getGroups().hasGroup(recp) || cwc.getGroups().getGroup(recp) != group)
						event.getRecipients().remove(recp);
				}
			}
			
			
			/* Prefix */
			ChatPrefix dP = PrefixConfig.defPrefix;
			ArrayList<String> prefixes = new ArrayList<String>();
			ArrayList<String> suffixes = new ArrayList<String>();
			if (dP.getChatPrefix() != null && !dP.getChatPrefix().trim().isEmpty())
				prefixes.add(dP.getChatPrefix());
			if (dP.getChatSuffix() != null && !dP.getChatSuffix().trim().isEmpty())
				suffixes.add(dP.getChatSuffix());
			for (ChatPrefix cP : PrefixConfig.prefixes) {
				if (player.hasPermission("cwchat.*") || player.hasPermission("*") || player.isOp() || player.hasPermission(cP.getPermissionNode())) {
					if (player.hasPermission("cwchat.owner") && !cP.isOwner())
						continue;

					if (cP.getChatPrefix() != null && !cP.getChatPrefix().trim().isEmpty())
						prefixes.add(cP.getChatPrefix());
					if (cP.getChatSuffix() != null && !cP.getChatSuffix().trim().isEmpty())
						suffixes.add(cP.getChatSuffix());
				}
			}

			if (player.hasPermission("cwchat.owner") || player.isOp()) {
				String custom = PrefixConfig.customPrefixes.get(player.getUniqueId());

				if (custom != null) {
					prefixes.clear();
					prefixes.add(custom);
				}
			}

			if (!player.hasPermission("cwchat.bypasslinks") && !player.hasPermission("cwchat.*")) {
				String ne = "";
				words: for (String word : message.split(" ")) {
					if (!urlPattern.matcher(word).matches()) {
						ne += word + " ";
						continue;
					}

					for (String allowed : cwc.getAllowedLinks()) {
						allowed = allowed.replace("http://", "").replace("https://", "");
						String tWord = word.replace("http://", "").replace("https://", "");
						if (tWord.toLowerCase().startsWith(allowed.toLowerCase())) {
							ne += word + " ";
							continue words;
						}
					}

					ne += "*BLOCKED* ";
				}
				message = ne.isEmpty() ? ne : ne.substring(0, ne.length() - 1);
			}
			
			String format = "";
			if (cwc.getStaffChat().contains(player.getName())) {
				format = Utils.integrateColour(cwc.getAdminFormat());
			} else if (cwc.getGroupChat().contains(player.getName())) {
				format = Utils.integrateColour(cwc.getGroupFormat());
			} else if (faction != null && fFormat != null) {
				format = Utils.integrateColour(fFormat);
			} else {
				format = Utils.integrateColour(cwc.getFormat());
			}
			
			format = format.replace("{GROUP}", cwc.getGroups().hasGroup(player) ? Utils.integrateColour(cwc.getGroups().getGroupName(player)) : "").trim();
			format = format.replace("{PREFIX}", Utils.integrateColour(Utils.implodeOld(prefixes, " ").trim()));
			format = format.replace("{SUFFIX}", Utils.integrateColour(Utils.implodeOld(suffixes, " ").trim()));
			format = format.replace("{DISPLAYNAME}", Utils.integrateColour(player.getDisplayName())).trim();
			format = format.replace("{NAME}", Utils.integrateColour(player.getName())).trim();
			if (faction != null)
				format = format.replace("{FACTION}", Utils.integrateColour(faction.getName())).trim();
			
			format = format.replace(
					"{MESSAGE}",
					(player.hasPermission("cwchat.color") || player.hasPermission("cwchat.*") || player.isOp() ? Utils.integrateColour(message,
							player.hasPermission("cwchat.format") || player.isOp()) : message)).trim();
			
			if (fFormat != null) {
			    event.setCancelled(true);

			    format = format.replace("[fac]", faction.getName());
			    String rank = "";
			    if (faction.getLeader().equals(fp)) {
			    	rank = "**";
			    } else if (faction.getUPlayersWhereRole(Rel.OFFICER).contains(fp)) {
			    	rank = "*";
			    } else if (faction.getUPlayersWhereRole(Rel.MEMBER).contains(fp)) {
			    	rank = "-";
			    } else if (faction.getUPlayersWhereRole(Rel.RECRUIT).contains(fp)) {
			    	rank = "+";
			    }
			    format = format.replace("[rank]", rank);

			    for (Player recp : event.getRecipients()) {
			    	UPlayer rp = UPlayer.get(recp);
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
								default:
									break;
			                }
			            }
			        }
			    }
			    return;
			}
			
			event.setCancelled(true);
			for (Player rec : event.getRecipients()) {
				rec.sendMessage(format.trim());
			}
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

					Group gPL = cwc.getGroups().getGroup(pl);
					Group gSH = cwc.getGroups().getGroup(sh);

					if (gPL != null && gSH != null && gPL.getName().equals(gSH.getName())) {
						if (!suppressGroup.containsKey(sh.getName()) || !suppressGroup.get(sh.getName()).equalsIgnoreCase(pl.getName())) {
							sh.sendMessage(ChatColor.RED + "You are attacking one of your group members!");
							suppressGroup.put(sh.getName(), pl.getName());

							Bukkit.getScheduler().scheduleSyncDelayedTask(cwc.getPlugin(), new Runnable() {

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

	    if (message.equalsIgnoreCase("/f c") || message.toLowerCase().startsWith("/f c ") || message.equalsIgnoreCase("/f chat") || message.toLowerCase().startsWith("/f chat ")) {
	        event.setCancelled(true);

	        if (!player.hasPermission("cwchat.factions.chat") && !player.isOp()) {
	            player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &cInsufficient permissions!"));
	            return;
	        }

	        String[] split = message.split(" ");

	        if (split.length >= 3) {
	            String arg = split[2];
	            
	            if (arg.equalsIgnoreCase("f") || arg.equalsIgnoreCase("fac") || arg.equalsIgnoreCase("faction")) {
	                cwc.factionC.add(player.getName());
	                cwc.allyC.remove(player.getName());
	                cwc.truceC.remove(player.getName());
	                player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &aFaction &6chat."));
	                return;
	            } else if (arg.equalsIgnoreCase("a") || arg.equalsIgnoreCase("ally")) {
	            	cwc.factionC.remove(player.getName());
	            	cwc.allyC.add(player.getName());
	            	cwc.truceC.remove(player.getName());
	                player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &5Ally &6chat."));
	                return;
	            } else if (arg.equalsIgnoreCase("t") || arg.equalsIgnoreCase("truce")) {
	            	cwc.factionC.remove(player.getName());
	            	cwc.allyC.remove(player.getName());
	                cwc.truceC.add(player.getName());
	                player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &dTruce &6chat."));
	                return;
	            } else if (arg.equalsIgnoreCase("p") || arg.equalsIgnoreCase("public") || arg.equalsIgnoreCase("g") || arg.equalsIgnoreCase("global")) {
	            	cwc.factionC.remove(player.getName());
	            	cwc.allyC.remove(player.getName());
	            	cwc.truceC.remove(player.getName());
	                player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &7Public &6chat."));
	                return;
	            }
	        }

	        if (cwc.factionC.contains(player.getName())) {
	        	cwc.factionC.remove(player.getName());
	        	cwc.allyC.add(player.getName());
	        	player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &5Ally &6chat."));
	        } else if (cwc.allyC.contains(player.getName())) {
	        	cwc.allyC.remove(player.getName());
	        	cwc.truceC.add(player.getName());
	        	player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &dTruce &6chat."));
	        } else if (cwc.truceC.contains(player.getName())) {
	        	cwc.truceC.remove(player.getName());
	        	player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &7Public &6chat."));
	        } else {
	        	cwc.factionC.add(player.getName());
	        	player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &aFaction &6chat."));
	        }
	    }
	}
}
