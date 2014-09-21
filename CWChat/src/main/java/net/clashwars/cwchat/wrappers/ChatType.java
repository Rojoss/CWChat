package net.clashwars.cwchat.wrappers;

import java.util.HashMap;
import java.util.Map;

public enum ChatType {
    PUBLIC(0, "Public", "&7{PREFIX}{NAME}&8:&f{SUFFIX} {MESSAGE}"),
    STAFF(1, "Staff", "&8[&3STAFF&8] &3{NAME}&8:&b {MESSAGE}"),
    FACTION(2, "Faction", "&8[&a&lF&8] &6[rank]&a[fac] &8| &7{DISPLAYNAME}&8: &7{MESSAGE}"),
    ALLY(3, "Ally", "&8[&5&lA&8] &6[rank]&5[fac] &8| &7{DISPLAYNAME}&8: &7{MESSAGE}"),
    TRUCE(4, "Truce", "&8[&d&lT&8] &6[rank]&d[fac] &8| &7{DISPLAYNAME}&8: &7{MESSAGE}");

    private static Map<Integer, ChatType> types;
    private int id;
    private String label;
    private String syntax;

    private ChatType(int id, String label, String syntax) {
        this.id = id;
        this.label = label;
        this.syntax = syntax;
    }

    //Get chatType by ID
    public static ChatType getChat(int i) {
        if (types == null) {
            initTypes();
        }
        return types.get(i);
    }

    //Get ID
    public int getID() {
        return id;
    }

    //Get label
    public String getLabel() {
        return label;
    }

    //Get syntax
    public String getSyntax() {
        return syntax;
    }

    //Fill hashmap with types.
    private static void initTypes() {
        types = new HashMap<Integer, ChatType>();
        for (ChatType t : values()) {
            types.put(t.id, t);
        }
    }
}