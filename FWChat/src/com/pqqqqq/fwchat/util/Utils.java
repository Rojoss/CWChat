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

	public static String integrateColour(String str, boolean magic) {
		for (ChatColor c : ChatColor.values()) {
			char ch = c.getChar();

			if (!magic && (ch == 'k' || ch == 'l' || ch == 'n' || ch == 'o' || ch == 'm'))
				continue;

			str = str.replaceAll("&" + c.getChar() + "|&" + Character.toUpperCase(c.getChar()), c.toString());
		}
		return str;
	}

	public static String integrateColour(String str) {
		return integrateColour(str, true);
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
