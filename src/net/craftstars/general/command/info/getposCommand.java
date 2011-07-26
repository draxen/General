
package net.craftstars.general.command.info;

import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;
import static java.lang.Math.round;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.craftstars.general.command.CommandBase;
import net.craftstars.general.General;
import net.craftstars.general.util.LanguageText;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class getposCommand extends CommandBase {
	public getposCommand(General instance) {
		super(instance);
	}

	@Override
	public Map<String, Object> parse(CommandSender sender, Command command, String label, String[] args, boolean isPlayer) {
		HashMap<String,Object> params = new HashMap<String,Object>();
		if(isPlayer && args.length == 0) params.put("player", sender);
		else if(args.length <= 2) {
			Player who = Toolbox.matchPlayer(args[0]);
			if(who != null) {
				params.put("player", who);
			} else {
				Messaging.invalidPlayer(sender, args[0]);
				return null;
			}
			if(args.length == 2) {
				if(!Toolbox.equalsOne(args[1], "dir", "direction", "brief", "pos", "where", "compass", "full",
					"short", "long", "facing", "rotation", "pointing", "rotation", "rot")) {
					Messaging.send(sender, LanguageText.GETPOS_INVALID.value("option", args[1]));
					return null;
				}
				setCommand(args[1]);
			} else params.put("option", "full");
		} else return null;
		return params;
	}

	@Override
	public boolean execute(CommandSender sender, String command, Map<String, Object> args) {
		if(Toolbox.lacksPermission(sender, "general.getpos", "general.basic"))
			return Messaging.lacksPermission(sender, "general.getpos");
		Player whose = (Player) args.get("player");
		if(!whose.equals(sender) && Toolbox.lacksPermission(sender, "general.getpos.other"))
			return Messaging.lacksPermission(sender, "general.getpos.other");
		double degrees = getRotation(whose);
		double compass = getCompass(whose);
		String player = whose.getName();
		if(Toolbox.equalsOne(command, "dir", "direction")) {
			Messaging.send(sender, LanguageText.GETPOS_DIR.value("player", player,
				"direction", this.getDirection(degrees)));
		} else if(Toolbox.equalsOne(command, "brief", "pos", "where", "short")) {
			String msg = LanguageText.GETPOS_POS.value("player", player, "x", whose.getLocation().getX(),
				"y", whose.getLocation().getY(), "z", whose.getLocation().getZ());
			if(General.plugin.config.getBoolean("playerlist.show-world", false))
				msg += LanguageText.GETPOS_WORLD.value("world", whose.getWorld().getName());
			Messaging.send(sender, msg);
		} else if(Toolbox.equalsOne(command, "rotation", "rot", "facing")) {
			Messaging.send(sender, LanguageText.GETPOS_ROTATION.value("player", player, 
				"yaw", whose.getLocation().getYaw(), "pitch", whose.getLocation().getPitch()));
		} else if(Toolbox.equalsOne(command, "compass", "pointing")) {
			Messaging.send(sender, LanguageText.GETPOS_COMPASS.value("player", player,
				"direction", this.getDirection(compass)));
		} else if(Toolbox.equalsOne(command, "long", "full")) {
			String msg = LanguageText.GETPOS_POS.value("player", player, "x", whose.getLocation().getX(),
				"y", whose.getLocation().getY(), "z", whose.getLocation().getZ());
			if(General.plugin.config.getBoolean("playerlist.show-world", false))
				msg += LanguageText.GETPOS_WORLD.value("world", whose.getWorld().getName());
			msg += "\n" + LanguageText.GETPOS_ROTATION.value("player", player, 
				"yaw", whose.getLocation().getYaw(), "pitch", whose.getLocation().getPitch());
			msg += "\n" + LanguageText.GETPOS_DIR.value("player", player,
				"direction", this.getDirection(degrees));
			msg += LanguageText.GETPOS_ANGLE.value("angle", round(degrees * 10) / 10.0);
			Messaging.send(sender, msg);
		} else return SHOW_USAGE;
		return false;
	}
	
	private double getRotation(Player whose) {
		double degreeRotation = ((whose.getLocation().getYaw() - 90) % 360);
		if(degreeRotation < 0) degreeRotation += 360.0;
		return degreeRotation;
	}
	
	private double getCompass(Player whose) {
		Location player = whose.getLocation(), compass = whose.getCompassTarget();
		Location northOfPlayer = player.add(-5, 0, 0);
		// c^2 = a^2 + b^2 + 2ab*cosC
		// cosC = (c^2 - a^2 - b^2) / 2ab
		double a = player.distance(northOfPlayer);
		double b = player.distance(compass);
		double c = northOfPlayer.distance(compass);
		double angle = acos((c*c - a*a - b*b) / (2*a*b));
		angle = toDegrees(angle);
		if(angle < 0) angle += 360.0;
		return angle;
	}
	
	private String getDirection(double degrees) {
		if(0 <= degrees && degrees < 22.5)
			return "N";
		else if(22.5 <= degrees && degrees < 67.5)
			return "NE";
		else if(67.5 <= degrees && degrees < 112.5)
			return "E";
		else if(112.5 <= degrees && degrees < 157.5)
			return "SE";
		else if(157.5 <= degrees && degrees < 202.5)
			return "S";
		else if(202.5 <= degrees && degrees < 247.5)
			return "SW";
		else if(247.5 <= degrees && degrees < 292.5)
			return "W";
		else if(292.5 <= degrees && degrees < 337.5)
			return "NW";
		else if(337.5 <= degrees && degrees < 360.0)
			return "N";
		else return "ERR";
	}
}
