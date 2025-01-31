
package net.craftstars.general.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import net.craftstars.general.General;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MessageOfTheDay {
	public static String parseMotD(CommandSender sender, String original) {
		String displayName = getDisplayName(sender), name = getName(sender), location = getLocation(sender);
		double health = getHealth(sender);
		String address = getAddress(sender), balance = getBalance(sender), currency = getCurrency();
		int numPlayers = General.plugin.getServer().getOnlinePlayers().length;
		String online = getOnline(), world = getWorld(sender), time = getTime(sender);
		original = Messaging.substitute(original, new String[] {
			"++", "+dname,+d,&dname;", "+name,+n,&name;", "+location,+l,&location;",
			"+health,+h,&health;", "+ip,+a,&ip;", "+balance,+$,&balance;", "+currency,+m,&currency;",
			"+online,+c,&online;", "+list,+p,&list;", "+world,+w,&world;", "+time,+t,&time;", "~!@#$%^&*()"
		}, new Object[] {
			"~!@#$%^&*()", displayName, name, location,
			health, address, balance, currency,
			numPlayers, online, world, time, "+"
		});
		return Messaging.format(original, 
			"dname", displayName, "name", name, "location", location, "health", health,
			"ip", address, "balance", balance, "currency", currency, "online", numPlayers,
			"list", online, "world", world, "time", time
		);
	}
	
	private static String getOnline() {
		List<String> lines = formatPlayerList(Toolbox.getPlayerList(General.plugin, null));
		StringBuilder stuff = new StringBuilder();
		for(String line : lines)
			stuff.append(line + " ");
		return stuff.toString();
	}
	
	public static List<String> formatPlayerList(List<String> players) {
		List<String> list = new ArrayList<String>();
		if(players.size() == 0) {
			list.add(LanguageText.MOTD_NOONE.value());
			return list;
		}
		StringBuilder playerList = new StringBuilder();
		for(String who : players) {
			if(playerList.length() + who.length() > 54) {
				list.add(playerList.toString());
				playerList.setLength(0);
			}
			playerList.append(who);
			playerList.append(", ");
		}
		int i = playerList.lastIndexOf(", ");
		if(i > 0) playerList.delete(i, playerList.length());
		list.add(playerList.toString());
		return list;
	}
	
	private static String getCurrency() {
		if(General.plugin.economy == null) return "none";
		return General.plugin.economy.getCurrency();
	}
	
	private static String getAddress(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getAddress().getAddress().getHostAddress();
		return "127.0.0.1";
	}
	
	private static String getBalance(CommandSender sender) {
		if(General.plugin.economy == null || ! (sender instanceof Player)) return "0";
		return General.plugin.economy.getBalanceForDisplay((Player) sender);
	}
	
	private static double getHealth(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getHealth() / 2.0;
		return 0;
	}
	
	private static String getTime(CommandSender sender) {
		if(sender instanceof Player) {
			long t = ((Player) sender).getWorld().getTime();
			return Time.formatTime(t);
		}
		return LanguageText.MOTD_UNKNOWN.value();
	}
	
	private static String getLocation(CommandSender sender) {
		if(sender instanceof Player) {
			Formatter fmt = new Formatter();
			Location loc = ((Player) sender).getLocation();
			return fmt.format("(%d, %d, %d)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).toString();
		}
		return LanguageText.MOTD_UNKNOWN.value();
	}
	
	private static String getWorld(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getWorld().getName();
		return LanguageText.MOTD_UNKNOWN.value();
	}
	
	private static String getDisplayName(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getDisplayName();
		return "CONSOLE";
	}
	
	private static String getName(CommandSender sender) {
		if(sender instanceof Player) return ((Player) sender).getName();
		return "CONSOLE";
	}
	
	public static void showMotD(CommandSender sender) {
		File dataFolder = General.plugin.getDataFolder();
		if(!dataFolder.exists()) dataFolder.mkdirs();
		Scanner f;
		try {
			File helpFile = new File(dataFolder, "general.motd");
			f = new Scanner(helpFile);
		} catch(FileNotFoundException e) {
			Messaging.send(sender, LanguageText.MOTD_UNAVAILABLE);
			return;
		}
		Toolbox.showFile(sender, f, true);
	}
}
