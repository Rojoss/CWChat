package com.clashwars.cwchat.util;

import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.Random;

public class Utils {

    public static String formatMsg(String msg) {
        return integrateColour("&8[&4CW Chat&8] &6" + msg);
    }

    public static String[] trimFirst(String[] args) {
        String[] ret = new String[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            ret[i - 1] = args[i];
        }
        return ret;
    }

    public static String implodeOld(String[] args, String connect) {
        String ret = "";
        if (args.length <= 0)
            return ret;

        for (String arg : args) {
            ret += arg + connect;
        }
        return ret.substring(0, ret.length() - connect.length());
    }

    public static String implodeOld(Collection<String> args, String connect) {
        if (args.isEmpty())
            return "";
        return implode(args.toArray(new String[args.size()]), connect);
    }


    public static String implode(String[] arr, String glue, int start, int end) {
        String ret = "";

        if (arr == null || arr.length <= 0)
            return ret;

        for (int i = start; i <= end && i < arr.length; i++) {
            ret += arr[i] + glue;
        }

        return ret.substring(0, ret.length() - glue.length());
    }

    public static String implode(String[] arr, String glue, int start) {
        return implode(arr, glue, start, arr.length - 1);
    }

    public static String implode(String[] arr, String glue) {
        return implode(arr, glue, 0);
    }

    public static String implode(String[] arr) {
        return implode(arr, " ");
    }


    public static String integrateColor(String str, boolean format, boolean magic) {
        char ch;
        for (ChatColor c : ChatColor.values()) {
            ch = c.getChar();

            if (!format && (ch == 'k' || ch == 'l' || ch == 'n' || ch == 'o' || ch == 'm' || ch == 'K' || ch == 'L' || ch == 'N' || ch == 'O' || ch == 'M')) {
                continue;
            }

            if (!magic && (ch == 'k' || ch == 'K')) {
                continue;
            }

            str = str.replaceAll("&" + ch + "|&" + Character.toUpperCase(ch), c.toString());
        }
        return str;
    }

    public static String integrateColour(String str) {
        return integrateColor(str, true, true);
    }

    public static String generateString(char[] acChars, int length) {
        Random random = new Random();
        String ret = "";

        for (int i = 0; i < length; i++) {
            ret += acChars[random.nextInt(acChars.length)];
        }
        return ret;
    }
}
