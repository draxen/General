
package net.craftstars.general.command.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Time;
import net.craftstars.general.util.Toolbox;

public class weatherCommand extends CommandBase {
	private static Random lightning = new Random();
	
	public weatherCommand(General instance) {
		super(instance);
	}
	
	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		switch(args.length) {
		case 0: // /weather -- toggles the weather
			if(!isPlayer) return null;
			setCommand("togglestorm");
			params.put("world", ((Player)sender).getWorld());
		break;
		case 1: // /weather on|off|zap|thunder|<duration>
			if(isPlayer && parseSimpleWeather(sender, args[0], ((Player)sender).getLocation(), params));
			else {
				// /weather <world> -- weather report on a specified world
				World world = Toolbox.matchWorld(args[0]);
				// /weather <player> -- a lightning strike near a player
				if(world == null) {
					Player player = Toolbox.matchPlayer(args[0]);
					if(player != null) {
						setCommand("lightning");
						params.put("location", player.getLocation());
//						doLightning(sender, player.getWorld(), player.getLocation());
					} else Messaging.invalidWorld(sender, args[0]);
				} else {
					setCommand("weatherreport");
					params.put("world", world);
//					showWeatherInfo(sender, world);
				}
			}
		break;
		case 2: // /weather thunder on|off|<duration>
			if(isPlayer && isThunder(args[0]) && parseThunder(sender, args[1], ((Player)sender).getWorld(), params));
			else if(isLightning(args[0])) {
				// /weather zap <player>
				Player player = Toolbox.matchPlayer(args[1]);
				World world;
				Location loc;
				if(player == null) {
					// /weather zap <world>
					world = Toolbox.matchWorld(args[1]);
					if(world == null) {
						Messaging.invalidPlayer(sender, args[1]);
						return null;
					}
					loc = world.getSpawnLocation();
				} else {
					world = player.getWorld();
					loc = player.getLocation();
				}
				setCommand("lightning");
				params.put("location", loc);
//				doLightning(sender, world, loc);
			} else { // weather <world> on|off|zap|thunder|<duration>
				World world = Toolbox.matchWorld(args[0]);
				if(world == null) {
					Messaging.invalidWorld(sender, args[0]);
					return null;
				}
				if(!parseSimpleWeather(sender, args[1], world.getSpawnLocation(), params)) return null;
//				doWeather(sender, args[1], world, world.getSpawnLocation());
			}
		break;
		case 3: // /weather <world> thunder on|off|<duration>
			if(isThunder(args[1])) {
				World world = Toolbox.matchWorld(args[0]);
				if(world == null) {
					Player player = Toolbox.matchPlayer(args[0]);
					if(player == null) {
						Messaging.invalidWorld(sender, args[0]);
						return null;
					}
					world = player.getWorld();
				}
				if(!parseThunder(sender, args[2], world, params)) return null;
//				doThunder(sender, world, (int) duration);
				break;
			} // fallthrough intentional
		default:
			return null;
		}
		return params;
	}
	
	private boolean parseSimpleWeather(CommandSender sender, String key, Location where, Map<String, Object> params) {
		World in = where.getWorld();
		params.put("world", in);
		// /weather [<world>] thunder -- toggles the thunder
		if(isThunder(key)) {
			setCommand("togglethunder");
		// /weather [<world>] lightning -- a lightning strike near the sender/spawn
		} else if(isLightning(key)) {
			setCommand("lightning");
			params.remove("world");
			params.put("location", where);
//			doLightning(sender, in, where);
		// /weather [<world>] start -- starts a storm for a random duration
		} else if(isStart(key)) {
			setCommand("storm");
			params.put("duration", -1);
//			doWeather(sender, in, -1);
		// /weather [<world>] stop -- stops a storm
		} else if(isStop(key)) {
			setCommand("storm");
			params.put("duration", 0);
//			doWeather(sender, in, 0);
		// /weather [<world>] <duration> -- starts a storm for a specified duration
		} else try {
			long duration = Time.extractDuration(key);
			if(duration < 0) {
				Messaging.send(sender, Messaging.get("weather.negative",
					"{rose}Only positive durations accepted for weather."));
				return false;
			}
			setCommand("storm");
			params.put("duration", duration);
//			doWeather(sender, in, duration);
		} catch(NumberFormatException e) {
			params.remove("world");
			return false;
		}
		return true;
	}
	
	private boolean parseThunder(CommandSender sender, String key, World in, Map<String, Object> params) {
		long duration;
		// /weather [<world>] thunder start -- starts thunder for a random duration
		if(isStart(key)) duration = -1;
		// /weather [<world>] thunder stop -- stops thunder
		else if(isStop(key)) duration = 0;
		// /weather [<world>] thunder <duration> -- starts thunder for a specified duration
		else try {
			duration = Time.extractDuration(key);
			if(duration < 0) {
				Messaging.send(sender, Messaging.get("weather.negative",
					"{rose}Only positive durations accepted for weather."));
				return false;
			} else if(duration > Integer.MAX_VALUE) {
				Messaging.send(sender, Messaging.get("weather.long",
					"{rose}Duration too large for thunder."));
				return false;
			}
		} catch(NumberFormatException e) {
			Messaging.send(sender, "{rose}Invalid duration: " + e.getMessage());
			return false;
		}
		setCommand("thunder");
		params.put("duration", duration);
		params.put("world", in);
//		doThunder(sender, sender.getWorld(), (int) duration);
		return true;
	}
	
	private boolean isLightning(String cmd) {
		return Toolbox.equalsOne(cmd, "lightning", "strike", "zap");
	}
	
	private boolean isThunder(String cmd) {
		return Toolbox.equalsOne(cmd, "thunder", "boom");
	}
	
	private boolean isStart(String cmd) {
		return Toolbox.equalsOne(cmd, "on", "start");
	}
	
	private boolean isStop(String cmd) {
		return Toolbox.equalsOne(cmd, "off", "stop");
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.weather"))
			return Messaging.lacksPermission(sender, "see or change the weather");
		if(command.equals("weatherreport")) {
			World world = (World) args.get("world");
			showWeatherInfo(sender, world);
		} else if(command.equals("togglestorm")) {
			World world = (World) args.get("world");
			if(world.hasStorm()) doWeather(sender, world, 0);
			else doWeather(sender, world, -1);
		} else if(command.equals("togglethunder")) {
			World world = (World) args.get("world");
			if(world.isThundering()) doThunder(sender, world, 0);
			else doThunder(sender, world, -1);
		} else if(command.equals("lightning")) {
			Location loc = (Location) args.get("location");
			doLightning(sender, loc);
		} else if(command.equals("storm")) {
			World world = (World) args.get("world");
			long duration = (Long) args.get("duration");
			doWeather(sender, world, duration);
		} else if(command.equals("thunder")) {
			World world = (World) args.get("world");
			int duration = ((Long) args.get("duration")).intValue();
			doThunder(sender, world, duration);
		} else return SHOW_USAGE;
		return true;
	}
	
	private void showWeatherInfo(CommandSender sender, World where) {
		if(where.getEnvironment() == Environment.NETHER) {
			Messaging.send(sender, Messaging.get("weather.storm.nether", "{rose}The nether doesn't have weather!"));
			return;
		}
		String storm = Time.formatDuration(where.getWeatherDuration());
		if(where.hasStorm()) Messaging.send(sender, Messaging.format(Messaging.get("weather.storm.active",
			"{blue}World '{white}{world}{blue}' has a storm active for {white}{duration}{blue}."),
			"world", where.getName(), "duration", storm));
		else Messaging.send(sender, Messaging.format(Messaging.get("weather.storm.inactive",
			"{blue}World '{white}{world}{blue}' does not have a storm active."), "world", where.getName()));
		if(where.getEnvironment() == Environment.SKYLANDS) return; // no thunder in sky
		String thunder = Time.formatDuration(where.getThunderDuration());
		if(where.isThundering()) Messaging.send(sender, Messaging.format(Messaging.get("weather.thunder.active",
			"{yellow}World '{white}{world}{yellow}' is thundering for {duration}."),
			"world", where.getName(), "duration", thunder));
		else Messaging.send(sender, Messaging.format(Messaging.get("weather.thunder.inactive",
			"{yellow}World '{white}{world}{yellow}' is not thundering."), "world", where.getName()));
	}
	
	private void doThunder(CommandSender sender, World world, int duration) {
		if(world.getEnvironment() != Environment.NORMAL) {
			Messaging.send(sender, Messaging.get("weather.thunder.nether", "{rose}Only normal worlds have thunder."));
			return;
		}
		if(Toolbox.lacksPermission(sender, "general.weather.thunder"))
			Messaging.lacksPermission(sender, "control thunder");
		if(Toolbox.checkCooldown(sender, world, "thunder", "general.weather.thunder")) return;
		if(!Toolbox.canPay(sender, 1, "economy.weather.thunder")) return;
		boolean state = duration != 0;
		boolean hasThunder = world.isThundering();
		world.setThundering(state);
		if(state && duration != -1) world.setThunderDuration(duration);
		if(duration == 0)
			Messaging.send(sender, "&yellow;Thunder stopped!");
		else if(duration == -1)
			Messaging.send(sender, "&yellow;Thunder started!");
		else if(hasThunder)
			Messaging.send(sender, "&yellow;Thunder will stop in " + duration + " ticks!");
		else Messaging.send(sender, "&yellow;Thunder started for " + duration + " + ticks!");
	}
	
	private void doWeather(CommandSender sender, World world, long duration) {
		if(world.getEnvironment() == Environment.NETHER) {
			Messaging.send(sender, Messaging.get("weather.storm.nether", "{rose}The nether doesn't have weather!"));
			return;
		}
		if(Toolbox.lacksPermission(sender, "general.weather.set"))
			Messaging.lacksPermission(sender, "control the weather");
		if(Toolbox.checkCooldown(sender, world, "storm", "general.weather.set")) return;
		if(!Toolbox.canPay(sender, 1, "economy.weather.storm")) return;
		boolean state = duration != 0;
		boolean hasStorm = world.hasStorm();
		world.setStorm(state);
		if(state && duration != -1) world.setWeatherDuration((int) duration);
		if(duration == 0)
			Messaging.send(sender, Messaging.get("weather.storm.start", "{blue}Weather storm stopped!"));
		else if(duration == -1)
			Messaging.send(sender, Messaging.get("weather.storm.stop", "{blue}Weather storm started!"));
		else if(hasStorm)
			Messaging.send(sender, Messaging.format(Messaging.get("weather.storm.change",
				"{blue}Weather storm will stop in {duration}!"), "duration", duration));
		else Messaging.send(sender, Messaging.format(Messaging.get("weather.storm.set",
				"{blue}Weather storm started for {duration}!"), "duration", duration));
	}
	
	private void doLightning(CommandSender sender, Location centre) {
		World world = centre.getWorld();
		if(Toolbox.lacksPermission(sender, "general.weather.zap"))
			Messaging.lacksPermission(sender, "summon lightning");
		if(Toolbox.checkCooldown(sender, world, "lightning", "general.weather.zap")) return;
		if(!Toolbox.canPay(sender, 1, "economy.weather.zap")) return;
		else {
			int x, y, z;
			x = centre.getBlockX();
			y = 127;
			z = centre.getBlockZ();
			Block block = world.getBlockAt(x, y, z);
			while(block.getType() == Material.AIR)
				block = block.getRelative(BlockFace.DOWN);
			int range = General.plugin.config.getInt("lightning-range", 20);
			x += lightning.nextInt(range * 2) - range;
			y = block.getLocation().getBlockY();
			z += lightning.nextInt(range * 2) - range;
			world.strikeLightning(new Location(world, x, y, z));
			Messaging.send(sender, Messaging.get("weather.lightning", "{yellow}Lightning strike!"));
		}
	}
}
