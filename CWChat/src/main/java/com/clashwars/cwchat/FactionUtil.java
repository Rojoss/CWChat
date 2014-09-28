package com.clashwars.cwchat;

import com.clashwars.cwchat.wrappers.ChatType;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FactionUtil {

    public static Set<Player> setFactionRecipients(Player player, ChatType ct, Set<Player> recipients) {
        UPlayer fPlayer = UPlayer.get(player);
        Faction faction = fPlayer.getFaction();

        if (faction != null) {
            //Faction chat
            if (ct == ChatType.FACTION || ct == ChatType.ALLY || ct == ChatType.TRUCE) {
                recipients.clear();
                recipients.addAll(faction.getOnlinePlayers());
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
                        recipients.addAll(thisFaction.getOnlinePlayers());
                    } else if (relation == Rel.ALLY && (ct == ChatType.TRUCE || ct == ChatType.ALLY)) {
                        recipients.addAll(thisFaction.getOnlinePlayers());
                    }
                }
            }
        }
        return recipients;
    }

}
