package com.pqqqqq.fwchat.util;

import java.util.Collection;
import java.util.Random;

import org.bukkit.ChatColor;

public class Utils {

	public static String implode(String[] args, String connect) {
		String ret = "";
		if (args.length <= 0)
			return ret;

		for (String arg : args) {
			ret += arg + connect;
		}
		return ret.substring(0, ret.length() - connect.length());
	}

	public static String implode(Collection<String> args, String connect) {
		if (args.isEmpty())
			return "";
		return implode(args.toArray(new String[args.size()]), connect);
	}

	public static String integrateColour(String str) {
		for (ChatColor c : ChatColor.values()) {
			str = str.replaceAll(
					"&" + c.getChar() + "|&"
							+ Character.toUpperCase(c.getChar()), c.toString());
		}
		return str;
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
