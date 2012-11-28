package com.pqqqqq.fwchat.config;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class MainConfig extends Config {
	public static String					format;
	public static String					factionFormat;
	public static String					allyFormat;
	public static String					truceFormat;
	// public static String mcmmoFormat;
	public static String					adminFormat;
	public static ItemStack					prizeWord	= null;
	public static ItemStack					prizeCode	= null;
	public static int						repM;
	public static int						cLen;

	public static Calendar					startCal;

	public static List<String>				allowedLinks;
	public static HashMap<String, String>	monthNames	= new HashMap<String, String>();

	private YamlConfiguration				cfg;
	private File							dir			= new File("plugins" + File.separator + "FWChat" + File.separator);
	private File							file		= new File(dir + File.separator + "config.yml");

	@Override
	public void init() {
		try {
			dir.mkdir();
			file.createNewFile();
			cfg = YamlConfiguration.loadConfiguration(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		try {
			cfg.load(file);
			monthNames.clear();

			format = ConfigUtil.getString(cfg, file, "chat-format", "{CHAT} {PREFIX} {DISPLAYNAME}: {MESSAGE}");
			factionFormat = ConfigUtil.getString(cfg, file, "faction-chat-format", "YOU DO IT");
			allyFormat = ConfigUtil.getString(cfg, file, "ally-chat-format", "YOU DO IT");
			truceFormat = ConfigUtil.getString(cfg, file, "truce-chat-format", "YOU DO IT");
			// mcmmoFormat = ConfigUtil.getString(cfg, file,
			// "mcmmo.chat-format",
			// "{CHAT} {PREFIX} {DISPLAYNAME}: {MESSAGE}");
			adminFormat = ConfigUtil.getString(cfg, file, "admin.chat-format", "{CHAT} {PREFIX} {DISPLAYNAME}: {MESSAGE}");
			allowedLinks = ConfigUtil.getStringList(cfg, file, "links.allowed-links", "fantasywar.net");
			int itemID = ConfigUtil.getInt(cfg, file, "word.prize.item-id", 1);
			int itemAmt = ConfigUtil.getInt(cfg, file, "word.prize.item-amt", 1);
			byte itemDmg = (byte) ConfigUtil.getInt(cfg, file, "word.prize.item-damage", 0);
			prizeWord = new ItemStack(itemID, itemAmt, itemDmg);

			itemID = ConfigUtil.getInt(cfg, file, "code.prize.item-id", 1);
			itemAmt = ConfigUtil.getInt(cfg, file, "code.prize.item-amt", 1);
			itemDmg = (byte) ConfigUtil.getInt(cfg, file, "code.prize.item-damage", 0);
			prizeCode = new ItemStack(itemID, itemAmt, itemDmg);

			repM = ConfigUtil.getInt(cfg, file, "code.set-time-minutes", 10);
			cLen = ConfigUtil.getInt(cfg, file, "code.code-length", 10);

			startCal = Calendar.getInstance();
			int startingMonth = ((startingMonth = ConfigUtil.getInt(cfg, file, "starting-date.month", startCal.get(Calendar.MONTH))) > 12 || startingMonth <= 0) ? startCal
					.get(Calendar.MONTH) : startingMonth;
			startCal.set(Calendar.MONTH, startingMonth);

			int startingYear = ConfigUtil.getInt(cfg, file, "starting-date.year", startCal.get(Calendar.YEAR));
			startCal.set(Calendar.YEAR, startingYear);

			int maxDays = startCal.getMaximum(Calendar.DAY_OF_MONTH);
			int startingDay = ((startingDay = ConfigUtil.getInt(cfg, file, "starting-date.day", startCal.get(Calendar.DAY_OF_MONTH))) > maxDays || startingDay <= 0) ? startCal
					.get(Calendar.DAY_OF_MONTH) : startingDay;
			startCal.set(Calendar.DAY_OF_MONTH, startingDay);

			startCal.set(Calendar.HOUR, 1);
			startCal.set(Calendar.HOUR_OF_DAY, 1);

			monthNames.put("January", ConfigUtil.getString(cfg, file, "month-names.january", "January"));
			monthNames.put("February", ConfigUtil.getString(cfg, file, "month-names.february", "February"));
			monthNames.put("March", ConfigUtil.getString(cfg, file, "month-names.march", "March"));
			monthNames.put("April", ConfigUtil.getString(cfg, file, "month-names.april", "April"));
			monthNames.put("May", ConfigUtil.getString(cfg, file, "month-names.may", "May"));
			monthNames.put("June", ConfigUtil.getString(cfg, file, "month-names.june", "June"));
			monthNames.put("July", ConfigUtil.getString(cfg, file, "month-names.july", "July"));
			monthNames.put("August", ConfigUtil.getString(cfg, file, "month-names.august", "August"));
			monthNames.put("September", ConfigUtil.getString(cfg, file, "month-names.september", "September"));
			monthNames.put("October", ConfigUtil.getString(cfg, file, "month-names.october", "October"));
			monthNames.put("November", ConfigUtil.getString(cfg, file, "month-names.november", "November"));
			monthNames.put("December", ConfigUtil.getString(cfg, file, "month-names.december", "December"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		try {
			cfg.set("starting-date.month", startCal.get(Calendar.MONTH));
			cfg.set("starting-date.year", startCal.get(Calendar.YEAR));
			cfg.set("starting-date.day", startCal.get(Calendar.DAY_OF_MONTH));

			cfg.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
