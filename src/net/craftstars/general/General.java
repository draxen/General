
package net.craftstars.general;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import net.craftstars.general.items.Items;
import net.craftstars.general.items.Kits;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.text.HelpHandler;
import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;
import net.craftstars.general.util.CommandManager;
import net.craftstars.general.util.EconomyManager;
import net.craftstars.general.util.Option;
import net.craftstars.general.util.PermissionManager;
import net.craftstars.general.util.PluginLogger;

import org.bukkit.event.Event.Priority;
import static org.bukkit.event.Event.Type.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class General extends JavaPlugin {
	public static General plugin = null;
	public static final boolean DEBUG = true;
	public static final String codename = "Webern";
	public static final PluginLogger logger = PluginLogger.getLogger("General", DEBUG);
	private Configuration config;
	public static PlayerManager players = new PlayerManager();
	
	@Override
	public void onEnable() {
		// This is a test
		boolean alreadyLoaded = false;
		if(plugin != null) {
			General.logger.warn(LanguageText.LOG_TWICE.value());
			alreadyLoaded = true;
		}
		plugin = this;
		logger.setPluginVersion(this.getDescription().getVersion());
		loadAllConfigs();
		logger.info("[Codename: " + General.codename + "] " + LanguageText.LOG_SUCCESS.value());
		if(alreadyLoaded) {
			PermissionManager.refreshItemGroups();
			return;
		}
		PermissionManager.setup();
		CommandManager.setup(config);
		HelpHandler.setup();
		registerEvents();
	}

	public void loadAllConfigs() {
		// The load order here is very delicate, as LanguageText is used nearly everywhere
		// and Messaging.load uses Option.
		LanguageText.setLanguage("en", getDataFolder(), "messages_en.yml"); // Make sure we have a default language!
		config = getConfiguration();
		loadConfiguration();
		Option.setConfiguration(config);
		Messaging.load();
		
		Items.setup();
		MobType.setup();
		Kits.load();
		EconomyManager.setup();
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(PLAYER_JOIN, players, Priority.Monitor, this);
		pm.registerEvent(PLAYER_CHAT, players, Priority.Monitor, this);
		pm.registerEvent(PLAYER_LOGIN, players, Priority.Monitor, this);
	}
	
	@Override
	public void onDisable() {
		Items.save();
		Messaging.save();
		if(Option.AUTO_SAVE.get()) {
			Kits.save();
			config.save();
		}
		General.logger.info(LanguageText.LOG_DISABLED.value());
	}
	
	private void loadConfiguration() {
		try {
			File dataFolder = this.getDataFolder();
			if(!dataFolder.exists()) dataFolder.mkdirs();
			File configFile = new File(dataFolder, "config.yml");
			
			if(!configFile.exists()) {
				createDefaultConfig(configFile);
				General.logger.info(LanguageText.LOG_CONFIG_SUCCESS.value());
			}
		} catch(Exception ex) {
			General.logger.warn(LanguageText.LOG_CONFIG_ERROR.value("file", "config.yml"), ex);
		}
		config.load();
	}

	public static void createDefaultConfig(File configFile) throws IOException {
		General.logger.info(LanguageText.LOG_CONFIG_DEFAULT.value("file", configFile.getName()));
		InputStream defaultConfig = General.class.getResourceAsStream(File.separator + configFile.getName());
		FileWriter out = new FileWriter(configFile);
		Scanner lines = new Scanner(defaultConfig);
		while(lines.hasNextLine())
			out.write(lines.nextLine() + "\n");
		out.flush();
		out.close();
		defaultConfig.close();
	}
}
