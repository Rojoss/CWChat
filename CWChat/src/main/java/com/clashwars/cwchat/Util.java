package com.clashwars.cwchat;

import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.Random;

public class Util {

    public static String formatMsg(String msg) {
        return CWUtil.integrateColor("&8[&4CW Chat&8] &6" + msg);
    }
}
