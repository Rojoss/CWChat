package com.clashwars.cwchat.events;

import com.clashwars.cwchat.CWChat;
import com.clashwars.cwchat.FactionUtil;
import com.clashwars.cwchat.Util;
import com.clashwars.cwchat.wrappers.ChatType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.*;

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
                Set<Player> rec = FactionUtil.setFactionRecipients(player, ct, event.getRecipients());
                event.getRecipients().clear();
                event.getRecipients().addAll(rec);
            }

            String formattedMsg = cwc.getChat().formatMessage(player, message);

            event.setCancelled(true);
            for (Player rec : event.getRecipients()) {
                rec.sendMessage(formattedMsg);
            }
        } catch (Exception e) {
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
                player.sendMessage(Util.formatMsg("&cInsufficient permissions!"));
                return;
            }

            if (cwc.getFactions() == null) {
                player.sendMessage(Util.formatMsg("&cNo factions found."));
                return;
            }

            String[] split = message.split(" ");

            if (split.length >= 3) {
                String arg = split[2];

                if (arg.equalsIgnoreCase("f") || arg.equalsIgnoreCase("fac") || arg.equalsIgnoreCase("faction")) {
                    cwc.playerChat.put(uuid, ChatType.FACTION);
                    player.sendMessage(Util.formatMsg("&6You are now speaking in &aFaction &6chat."));
                    return;
                } else if (arg.equalsIgnoreCase("a") || arg.equalsIgnoreCase("ally")) {
                    cwc.playerChat.put(uuid, ChatType.ALLY);
                    player.sendMessage(Util.formatMsg("&6You are now speaking in &5Ally &6chat."));
                    return;
                } else if (arg.equalsIgnoreCase("t") || arg.equalsIgnoreCase("truce")) {
                    cwc.playerChat.put(uuid, ChatType.TRUCE);
                    player.sendMessage(Util.formatMsg("&6You are now speaking in &dTruce &6chat."));
                    return;
                } else if (arg.equalsIgnoreCase("p") || arg.equalsIgnoreCase("public") || arg.equalsIgnoreCase("g") || arg.equalsIgnoreCase("global")) {
                    cwc.playerChat.put(uuid, ChatType.PUBLIC);
                    player.sendMessage(Util.formatMsg("&6You are now speaking in &7Public &6chat."));
                    return;
                }
            }

            if (cwc.playerChat.get(uuid) == ChatType.FACTION) {
                cwc.playerChat.put(uuid, ChatType.ALLY);
                player.sendMessage(Util.formatMsg("&6You are now speaking in &5Ally &6chat."));
            } else if (cwc.playerChat.get(uuid) == ChatType.ALLY) {
                cwc.playerChat.put(uuid, ChatType.TRUCE);
                player.sendMessage(Util.formatMsg("&6You are now speaking in &dTruce &6chat."));
            } else if (cwc.playerChat.get(uuid) == ChatType.TRUCE) {
                cwc.playerChat.put(uuid, ChatType.PUBLIC);
                player.sendMessage(Util.formatMsg("&6You are now speaking in &7Public &6chat."));
            } else {
                cwc.playerChat.put(uuid, ChatType.FACTION);
                player.sendMessage(Util.formatMsg("&6You are now speaking in &aFaction &6chat."));
            }
        }
    }
}
