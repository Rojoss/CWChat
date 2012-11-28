package com.pqqqqq.fwchat.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.pqqqqq.fwchat.wrappers.ChatGroup;

public class GroupConfig extends Config {
	public static ChatGroup				defGroup;
	public static ArrayList<ChatGroup>	chats	= new ArrayList<ChatGroup>();

	private YamlConfiguration			cfg;
	private File						dir		= new File("plugins" + File.separator + "FWChat" + File.separator);
	private File						file	= new File(dir + File.separator + "groups.yml");

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
			defGroup = new ChatGroup("Local");
			chats.clear();

			defGroup.setPermissionNode(ConfigUtil.getString(cfg, file, "groups.Local.permission", ""));
			defGroup.setListNode(ConfigUtil.getString(cfg, file, "groups.Local.list-permission", ""));
			defGroup.setPrefix(ConfigUtil.getString(cfg, file, "groups.Local.prefix", "[&6G&f]"));
			defGroup.setPriority(ConfigUtil.getInt(cfg, file, "groups.Local.priority", 1));
			defGroup.setChatRadius(ConfigUtil.getInt(cfg, file, "groups.Local.chat-radius", 500));
			defGroup.setListName(ConfigUtil.getString(cfg, file, "groups.Local.list-name", "Local"));
			defGroup.setDisplayDefault(false);
			defGroup.setIgnoreClick(false);

			ConfigurationSection sec = cfg.getConfigurationSection("groups");
			for (String key : sec.getKeys(false)) {
				if (key.equals("Local"))
					continue;
				ChatGroup c = new ChatGroup(key);
				c.setPermissionNode(ConfigUtil.getString(cfg, file, "groups." + key + ".permission", "fwChat.group." + key));
				c.setListNode(ConfigUtil.getString(cfg, file, "groups." + key + ".list-permission", "fwChat.list." + key));
				c.setListName(ConfigUtil.getString(cfg, file, "groups." + key + ".list-name", key));
				c.setPrefix(ConfigUtil.getString(cfg, file, "groups." + key + ".prefix", "[" + key + "]"));
				c.setPriority(ConfigUtil.getInt(cfg, file, "groups." + key + ".priority", 0));
				c.setChatRadius(ConfigUtil.getInt(cfg, file, "groups." + key + ".chat-radius", -1));
				c.setDisplayDefault(ConfigUtil.getBoolean(cfg, file, "groups." + key + ".display-default-list", false));
				c.setIgnoreClick(ConfigUtil.getBoolean(cfg, file, "groups." + key + ".ignore.right-click", false));
				chats.add(c);
			}
			Collections.sort(chats, new SortGroup());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class SortGroup implements Comparator<ChatGroup> {

		@Override
		public int compare(ChatGroup arg0, ChatGroup arg1) {
			if (arg0.getPriority() > arg1.getPriority())
				return -1;
			else if (arg0.getPriority() < arg1.getPriority())
				return 1;
			return 0;
		}
	}
}
