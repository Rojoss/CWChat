package net.clashwars.cwchat.config;

import java.io.File;
import java.io.IOException;

import net.clashwars.cwchat.CWChat;

import org.bukkit.configuration.file.YamlConfiguration;

public class MainConfig extends Config {
	private CWChat							cwc;

	private YamlConfiguration				cfg;
	private File							dir			= new File("plugins" + File.separator + "CWChat" + File.separator);
	private File							file		= new File(dir + File.separator + "cwchat.yml");

	public MainConfig(CWChat cwc) {
		this.cwc = cwc;
	}

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

			cwc.setFormat(ConfigUtil.getString(cfg, file, "chat-format.default", "{PREFIX} {DISPLAYNAME}: {SUFFIX} {MESSAGE}"));
			cwc.setAdminFormat(ConfigUtil.getString(cfg, file, "chat-format.admin", "{PREFIX} {DISPLAYNAME}: {SUFFIX} {MESSAGE}"));
			cwc.setGroupFormat(ConfigUtil.getString(cfg, file, "chat-format.groups", "{PREFIX} {DISPLAYNAME}: {SUFFIX} {MESSAGE}"));
			cwc.setFacFormat(ConfigUtil.getString(cfg, file, "chat-format.factions.faction", "{FACTION} {PREFIX} {DISPLAYNAME}: {SUFFIX} {MESSAGE}"));
			cwc.setAllyFormat(ConfigUtil.getString(cfg, file, "chat-format.factions.ally", "{FACTION} {PREFIX} {DISPLAYNAME}: {SUFFIX} {MESSAGE}"));
			cwc.setTruceFormat(ConfigUtil.getString(cfg, file, "chat-format.factions.truce", "{FACTION} {PREFIX} {DISPLAYNAME}: {SUFFIX} {MESSAGE}"));
			cwc.setAllowedLinks(ConfigUtil.getStringList(cfg, file, "links.allowed-links", "clashwars.net"));

			cwc.setGroupsEnabled(ConfigUtil.getBoolean(cfg, file, "groups.enablegroups", false));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		try {
			cfg.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
