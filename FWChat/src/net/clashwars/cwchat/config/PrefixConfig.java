package net.clashwars.cwchat.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.clashwars.cwchat.util.Utils;
import net.clashwars.cwchat.wrappers.ChatPrefix;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;


public class PrefixConfig extends Config {
	public static ChatPrefix			defPrefix;
	public static ArrayList<ChatPrefix>	prefixes		= new ArrayList<ChatPrefix>();
	public static Map<UUID, String>	customPrefixes	= new HashMap<UUID, String>();

	private YamlConfiguration			cfg;
	private File						dir				= new File("plugins" + File.separator + "CWChat" + File.separator);
	private File						file			= new File(dir + File.separator + "prefix.yml");

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
			prefixes.clear();
			customPrefixes.clear();

			defPrefix = new ChatPrefix("Default");
			defPrefix.setChatPrefix(ConfigUtil.getString(cfg, file, "prefix.Default.chat-prefix", ""));
			defPrefix.setChatSuffix(ConfigUtil.getString(cfg, file, "prefix.Default.chat-suffix", ""));
			defPrefix.setPermissionNode(ConfigUtil.getString(cfg, file, "prefix.Default.permission", ""));
			defPrefix.setOwner(false);

			ConfigurationSection sec = cfg.getConfigurationSection("prefix");
			for (String key : sec.getKeys(false)) {
				if (key.equals("Global"))
					continue;
				ChatPrefix p = new ChatPrefix(key);
				p.setChatPrefix(ConfigUtil.getString(cfg, file, "prefix." + key + ".chat-prefix", ""));
				p.setChatSuffix(ConfigUtil.getString(cfg, file, "prefix." + key + ".chat-suffix", ""));
				p.setPermissionNode(ConfigUtil.getString(cfg, file, "prefix." + key + ".permission", ""));
				p.setOwner(ConfigUtil.getBoolean(cfg, file, "prefix." + key + ".owner", false));
				prefixes.add(p);
			}

			if (!cfg.isConfigurationSection("custom-prefixes")) {
				cfg.createSection("custom-prefixes");
				cfg.save(file);
			}

			for (String uuid : cfg.getConfigurationSection("custom-prefixes").getKeys(false)) {
				String name = ConfigUtil.getString(cfg, file, "custom-prefixes." + uuid, null);

				if (name != null) {
					customPrefixes.put(UUID.fromString(uuid), Utils.integrateColour(name));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
