package net.clashwars.cwchat.bukkit.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

					if (!recp.hasPermission("fwchat.staffchat") && !recp.hasPermission("fwChat.*"))
						event.getRecipients().remove(recp);
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

			ChatPrefix dP = PrefixConfig.defPrefix;
			ArrayList<String> prefixes = new ArrayList<String>();
			ArrayList<String> suffixes = new ArrayList<String>();
			if (dP.getChatPrefix() != null && !dP.getChatPrefix().trim().isEmpty())
				prefixes.add(dP.getChatPrefix());
			if (dP.getChatSuffix() != null && !dP.getChatSuffix().trim().isEmpty())
				suffixes.add(dP.getChatSuffix());
			for (ChatPrefix cP : PrefixConfig.prefixes) {
				if (player.hasPermission("fwchat.*") || player.hasPermission("*") || player.isOp() || player.hasPermission(cP.getPermissionNode())) {
					if (player.hasPermission("fwchat.owner") && !cP.isOwner())
						continue;

					if (cP.getChatPrefix() != null && !cP.getChatPrefix().trim().isEmpty())
						prefixes.add(cP.getChatPrefix());
					if (cP.getChatSuffix() != null && !cP.getChatSuffix().trim().isEmpty())
						suffixes.add(cP.getChatSuffix());
				}
			}

			if (player.hasPermission("fwchat.owner") || player.isOp()) {
				String custom = PrefixConfig.customPrefixes.get(player.getName().toLowerCase());

				if (custom != null) {
					prefixes.clear();
					prefixes.add(custom);
				}
			}

			if (!player.hasPermission("cwchat.bypasslinks") && !player.hasPermission("fwChat.*")) {
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
			} else {
				format = Utils.integrateColour(cwc.getFormat());
			}
			
			format = format.replace("{GROUP}", cwc.getGroups().hasGroup(player) ? Utils.integrateColour(cwc.getGroups().getGroupName(player)) : "").trim();
			format = format.replace("{PREFIX}", Utils.integrateColour(Utils.implodeOld(prefixes, " ").trim()));
			format = format.replace("{SUFFIX}", Utils.integrateColour(Utils.implodeOld(suffixes, " ").trim()));
			format = format.replace("{DISPLAYNAME}", Utils.integrateColour(player.getDisplayName())).trim();
			format = format.replace("{NAME}", Utils.integrateColour(player.getName())).trim();
			
			format = format.replace(
					"{MESSAGE}",
					(player.hasPermission("cwchat.color") || player.hasPermission("cwchat.*") || player.isOp() ? Utils.integrateColour(message,
							player.hasPermission("cwchat.format") || player.isOp()) : message)).trim();
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
}
