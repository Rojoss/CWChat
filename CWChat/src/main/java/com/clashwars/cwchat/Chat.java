package com.clashwars.cwchat;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.clashwars.cwchat.config.PrefixConfig;
import com.clashwars.cwchat.util.Utils;
import com.clashwars.cwchat.wrappers.ChatPrefix;
import com.clashwars.cwchat.wrappers.ChatType;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Chat {
    private CWChat cwc;

    public Chat(CWChat cwc) {
        this.cwc = cwc;
    }

    public String formatMessage(Player player, String message) {
        return formatMessage(player, cwc.playerChat.get(player.getUniqueId()), message);
    }

    public String formatMessage(Player player, ChatType chatType, String message) {

		
		/* PREFIXES */
        ArrayList<String> prefixes = new ArrayList<String>();
        ArrayList<String> suffixes = new ArrayList<String>();

        //Add default prefix.
        ChatPrefix dP = PrefixConfig.defPrefix;
        if (dP.getChatPrefix() != null && !dP.getChatPrefix().trim().isEmpty())
            prefixes.add(dP.getChatPrefix());
        if (dP.getChatSuffix() != null && !dP.getChatSuffix().trim().isEmpty())
            suffixes.add(dP.getChatSuffix());

        //Add all other prefixes
        for (ChatPrefix cP : PrefixConfig.prefixes) {
            if (player.hasPermission("cwchat.*") || player.hasPermission("*") || player.isOp() || player.hasPermission(cP.getPermissionNode())) {
                if (player.isOp() && !cP.isOwner())
                    continue;

                if (cP.getChatPrefix() != null && !cP.getChatPrefix().trim().isEmpty())
                    prefixes.add(cP.getChatPrefix());
                if (cP.getChatSuffix() != null && !cP.getChatSuffix().trim().isEmpty())
                    suffixes.add(cP.getChatSuffix());
            }
        }

        //Add custom prefixes
        if (player.hasPermission("cwchat.owner") || player.isOp()) {
            String custom = PrefixConfig.customPrefixes.get(player.getUniqueId());

            if (custom != null) {
                prefixes.clear();
                prefixes.add(custom);
            }
        }
		
		
		
		
		/* SYNTAX */
        String syntax = Utils.integrateColour(chatType.getSyntax());

        syntax = syntax.replace("{PREFIX}", Utils.integrateColour(Utils.implodeOld(prefixes, " ").trim()));
        syntax = syntax.replace("{SUFFIX}", Utils.integrateColour(Utils.implodeOld(suffixes, " ").trim()));
        syntax = syntax.replace("{DISPLAYNAME}", Utils.integrateColour(player.getDisplayName())).trim();
        syntax = syntax.replace("{NAME}", Utils.integrateColour(player.getName())).trim();

        UPlayer fPlayer = UPlayer.get(player);
        Faction faction = fPlayer.getFaction();

        if (faction != null) {
            //syntax = syntax.replace("{FACTION}", Utils.integrateColour(faction.getName())).trim();
            syntax = syntax.replace("[fac]", Utils.integrateColour(faction.getName())).trim();
            String rank = "";
            if (faction.getLeader() != null && faction.getLeader().equals(fPlayer)) {
                rank = "**";
            } else if (faction.getUPlayersWhereRole(Rel.OFFICER).contains(fPlayer)) {
                rank = "*";
            } else if (faction.getUPlayersWhereRole(Rel.MEMBER).contains(fPlayer)) {
                rank = "-";
            } else if (faction.getUPlayersWhereRole(Rel.RECRUIT).contains(fPlayer)) {
                rank = "+";
            }
            syntax = syntax.replace("[rank]", rank);
        }
		
		
		
		
		/* COLORED AND FORMATED CHAT */
        if (player.isOp() || player.hasPermission("cwchat.*")) {
            syntax = syntax.replace("{MESSAGE}", Utils.integrateColor(message, true, true)).trim();
        } else {
            if (player.hasPermission("cwchat.color")) {
                if (!Utils.integrateColor(message, player.hasPermission("cwchat.format"), false).equals(message)) {
                    int cdTime = 120000;
                    if (player.hasPermission("cwchat.lesscooldown")) {
                        cdTime = 60000;
                    }
                    if (cwc.coloredChatCD.containsKey(player.getUniqueId())) {
                        Long time = cwc.coloredChatCD.get(player.getUniqueId());
                        if (time - System.currentTimeMillis() <= 0) {
                            syntax = syntax.replace("{MESSAGE}", Utils.integrateColor(message, player.hasPermission("cwchat.format"), false)).trim();
                            cwc.coloredChatCD.put(player.getUniqueId(), System.currentTimeMillis() + cdTime);
                        } else {
                            syntax = syntax.replace("{MESSAGE}", message).trim();
                        }
                    } else {
                        syntax = syntax.replace("{MESSAGE}", Utils.integrateColor(message, player.hasPermission("cwchat.format"), false)).trim();
                        cwc.coloredChatCD.put(player.getUniqueId(), System.currentTimeMillis() + cdTime);
                    }
                } else {
                    syntax = syntax.replace("{MESSAGE}", message).trim();
                }
            } else {
                syntax = syntax.replace("{MESSAGE}", message).trim();
            }
        }


        return syntax;
    }
}
