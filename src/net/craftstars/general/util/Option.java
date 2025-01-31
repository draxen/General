package net.craftstars.general.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.util.config.Configuration;

public abstract class Option {
	public static OptionBoolean AWAY_SLEEP = new OptionBoolean("away-sleep", true);
	public static OptionBoolean SHOW_USAGE = new OptionBoolean("show-usage-on-fail", true);
	public static OptionString TAG_FORMAT = new OptionString("tag-fmt", "name:");
	public static OptionBoolean SHOW_MOTD = new OptionBoolean("show-motd", true);
	public static OptionString ECONOMY_SYSTEM = new OptionString("economy.system", "None");
	public static OptionString PERMISSIONS_SYSTEM = new OptionString("permissions.system", "Basic");
	public static OptionBoolean AUTO_SAVE = new OptionBoolean("auto-save", false);
	public static OptionBoolean LOG_COMMANDS = new OptionBoolean("log-commands", false);
	public static OptionStringList OPS_ONLY = new OptionStringList("permissions.ops-only", null);
	public static OptionBoolean SHOW_WORLD = new OptionBoolean("playerlist.show-world", false);
	public static OptionBoolean SHOW_HEALTH = new OptionBoolean("playerlist.show-health", true);
	public static OptionBoolean SHOW_COORDS = new OptionBoolean("playerlist.show-coords", true);
	public static OptionBoolean SHOW_IP = new OptionBoolean("playerlist.show-ip", false);
	public static OptionBoolean ALLOW_OVERRIDE = new OptionBoolean("playerlist.allow-all", false);
	public static OptionDouble ECONOMY_SELL = new OptionDouble("economy.give.sell", 100);
	public static OptionString ECONOMY_TAKE_SELL = new OptionString("economy.give.take", "sell");
	public static OptionString ECONOMY_CLEAR_SELL = new OptionString("economy.give.clear", "sell");
	public static OptionInteger GIVE_MASS = new OptionInteger("give.mass", 64);
	public static OptionInteger LIGHTNING_RANGE = new OptionInteger("lightning-range", 20);
	public static OptionInteger SUMMON_RANGE = new OptionInteger("summon-range", 30);
	public static OptionStringList TELEPORT_BASICS = new OptionStringList("teleport.basics",
		Arrays.asList("world", "player", "home", "spawn"));
	public static OptionInteger TELEPORT_WARMUP = new OptionInteger("teleport.warm-up", 0);
	public static OptionString KIT_METHOD = new OptionString("economy.give.kits", "individual");
	public static OptionDouble KIT_DISCOUNT = new OptionDouble("economy.give.discount", 80);
	public static OptionString LANGUAGE = new OptionString("language", "en");
	public static OptionBoolean SHOW_TICKS = new OptionBoolean("time.show-ticks", true);
	public static OptionBoolean TIME_FORMAT = new OptionBoolean("time.format-24-hour", false);
	public static OptionBoolean OTHERS4ALL = new OptionBoolean("give.others-for-all", true);
	public static OptionInteger COOLDOWN(String command) {
		return new OptionInteger("cooldown." + command, 0);
	}
	public static OptionDouble ECONOMY_COST(String node) {
		return new OptionDouble(node, 0.0);
	}
	public static OptionIntegerList GROUP(String group) {
		return new OptionIntegerList("give.groups." + group, null);
	}
	protected String node;
	protected Object def;
	protected static Configuration config;
	
	@SuppressWarnings("hiding")
	protected Option(String node, Object def) {
		this.node = node;
		this.def = def;
	}
	
	public abstract Object get();
	
	public void set(Object value) {
		config.setProperty(node, value);
	}
	
	public void reset() {
		set(def);
	}

	public static boolean nodeExists(String node) {
		return Toolbox.nodeExists(config, node);
	}
	
	public static void setConfiguration(Configuration c) {
		config = c;
	}
	
	public static class OptionBoolean extends Option {
		@SuppressWarnings("hiding") OptionBoolean(String node, boolean def) {
			super(node, def);
		}

		@Override
		public Boolean get() {
			return config.getBoolean(node, (Boolean) def);
		}
	}

	public static class OptionString extends Option {
		@SuppressWarnings("hiding") OptionString(String node, String def) {
			super(node, def);
		}

		@Override
		public String get() {
			return config.getString(node, (String) def);
		}
	}

	public static class OptionInteger extends Option {
		@SuppressWarnings("hiding") OptionInteger(String node, int def) {
			super(node, def);
		}

		@Override
		public Integer get() {
			return config.getInt(node, (Integer) def);
		}
	}

	public static class OptionDouble extends Option {
		@SuppressWarnings("hiding") OptionDouble(String node, double def) {
			super(node, def);
		}

		@Override
		public Double get() {
			return config.getDouble(node, (Double) def);
		}
	}

	public static class OptionStringList extends Option {
		@SuppressWarnings("hiding") OptionStringList(String node, List<String> def) {
			super(node, def);
		}

		@Override@SuppressWarnings("unchecked")
		public List<String> get() {
			return config.getStringList(node, (List<String>) def);
		}
	}

	public static class OptionIntegerList extends Option {
		@SuppressWarnings("hiding") OptionIntegerList(String node, List<Integer> def) {
			super(node, def);
		}

		@Override@SuppressWarnings("unchecked")
		public List<Integer> get() {
			return config.getIntList(node, (List<Integer>) def);
		}
	}
}