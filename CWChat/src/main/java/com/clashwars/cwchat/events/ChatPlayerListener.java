package com.clashwars.cwchat.events;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.clashwars.cwchat.CWChat;
import com.clashwars.cwchat.util.Utils;
import com.clashwars.cwchat.wrappers.ChatType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatPlayerListener implements Listener {
    private CWChat cwc;

    public ChatPlayerListener(CWChat cwc) {
        this.cwc = cwc;
    }

    //Add player to chats on login and set default chat to public.
    @EventHandler
    public void login(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!cwc.playerChat.containsKey(uuid)) {
            cwc.playerChat.put(uuid, ChatType.PUBLIC);
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void chat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        try {
            List<Player> recipients = new ArrayList<Player>(event.getRecipients());
            String message = event.getMessage();

            final Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();

            ChatType ct = cwc.playerChat.get(uuid);

            //Staff chat
            if (ct == ChatType.STAFF) {
                for (Player re : recipients) {
                    if (re == null || re.hasPermission("cwchat.staffchat") || re.hasPermission("cwchat.*") || re.isOp()) {
                        continue;
                    }
                    event.getRecipients().remove(re);
                }
            }


            //Factions chats
            if (cwc.getFactions() != null) {
                UPlayer fPlayer = UPlayer.get(player);
                Faction faction = fPlayer.getFaction();

                if (faction != null) {
                    //Faction chat
                    if (ct == ChatType.FACTION || ct == ChatType.ALLY || ct == ChatType.TRUCE) {
                        event.getRecipients().clear();
                        event.getRecipients().addAll(faction.getOnlinePlayers());
                    }

                    //Ally & Truce chat
                    if (ct == ChatType.ALLY || ct == ChatType.TRUCE) {

                        Map<String, Rel> wishes = faction.getRelationWishes();

                        for (Map.Entry<String, Rel> entry : wishes.entrySet()) {
                            Faction thisFaction = Faction.get(entry.getKey());

                            if (thisFaction == null)
                                continue;

                            Rel relation = faction.getRelationTo(thisFaction, true);

                            if (relation == Rel.TRUCE && ct == ChatType.TRUCE) {
                                event.getRecipients().addAll(thisFaction.getOnlinePlayers());
                            } else if (relation == Rel.ALLY && (ct == ChatType.TRUCE || ct == ChatType.ALLY)) {
                                event.getRecipients().addAll(thisFaction.getOnlinePlayers());
                            }
                        }
                    }
                }
            }

            String formattedMsg = cwc.getChat().formatMessage(player, message);

            event.setCancelled(true);
            for (Player rec : event.getRecipients()) {
                rec.sendMessage(formattedMsg);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

	/*
    @EventHandler(priority = EventPriority.MONITOR)
	public void chatMonitor(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;

		event.setCancelled(true);
		for (Player p : event.getRecipients()) {
			p.sendMessage(event.getFormat());
		}
	}
	*/


    @EventHandler(priority = EventPriority.LOWEST)
    public void cmd(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String message = event.getMessage();

        if (message.equalsIgnoreCase("/f c") || message.toLowerCase().startsWith("/f c ") || message.equalsIgnoreCase("/f chat") || message.toLowerCase().startsWith("/f chat ")) {
            event.setCancelled(true);

            if (!player.hasPermission("cwchat.factions.chat") && !player.isOp()) {
                player.sendMessage(Utils.formatMsg("&cInsufficient permissions!"));
                return;
            }

            if (cwc.getFactions() == null) {
                player.sendMessage(Utils.formatMsg("&cNo factions found."));
                return;
            }

            String[] split = message.split(" ");

            if (split.length >= 3) {
                String arg = split[2];

                if (arg.equalsIgnoreCase("f") || arg.equalsIgnoreCase("fac") || arg.equalsIgnoreCase("faction")) {
                    cwc.playerChat.put(uuid, ChatType.FACTION);
                    player.sendMessage(Utils.formatMsg("&6You are now speaking in &aFaction &6chat."));
                    return;
                } else if (arg.equalsIgnoreCase("a") || arg.equalsIgnoreCase("ally")) {
                    cwc.playerChat.put(uuid, ChatType.ALLY);
                    player.sendMessage(Utils.formatMsg("&6You are now speaking in &5Ally &6chat."));
                    return;
                } else if (arg.equalsIgnoreCase("t") || arg.equalsIgnoreCase("truce")) {
                    cwc.playerChat.put(uuid, ChatType.TRUCE);
                    player.sendMessage(Utils.formatMsg("&6You are now speaking in &dTruce &6chat."));
                    return;
                } else if (arg.equalsIgnoreCase("p") || arg.equalsIgnoreCase("public") || arg.equalsIgnoreCase("g") || arg.equalsIgnoreCase("global")) {
                    cwc.playerChat.put(uuid, ChatType.PUBLIC);
                    player.sendMessage(Utils.formatMsg("&6You are now speaking in &7Public &6chat."));
                    return;
                }
            }

            if (cwc.playerChat.get(uuid) == ChatType.FACTION) {
                cwc.playerChat.put(uuid, ChatType.ALLY);
                player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &5Ally &6chat."));
            } else if (cwc.playerChat.get(uuid) == ChatType.ALLY) {
                cwc.playerChat.put(uuid, ChatType.TRUCE);
                player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &dTruce &6chat."));
            } else if (cwc.playerChat.get(uuid) == ChatType.TRUCE) {
                cwc.playerChat.put(uuid, ChatType.PUBLIC);
                player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &7Public &6chat."));
            } else {
                cwc.playerChat.put(uuid, ChatType.FACTION);
                player.sendMessage(Utils.integrateColour("&8[&4CWChat&8] &6You are now speaking in &aFaction &6chat."));
            }
        }
    }
}
