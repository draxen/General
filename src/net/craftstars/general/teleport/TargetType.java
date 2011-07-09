
package net.craftstars.general.teleport;

import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.entity.Player;

public enum TargetType {
	SELF("yourself") {
		@Override
		public boolean hasPermission(Player who) {
			if(Toolbox.hasPermission(who, "general.teleport.basic")) return true;
			return super.hasPermission(who);
		}
	}, OTHER("others"), MOB("mobs");
	private String msg;
	
	private TargetType(String message) {
		msg = message;
	}
	
	public boolean hasPermission(Player who) {
		if(Toolbox.hasPermission(who, getPermission())) return true;
		Messaging.lacksPermission(who, "teleport " + msg);
		return false;
	}
	
	public String getPermission() {
		return "general.teleport." + this.toString().toLowerCase();
	}
	
	public String getName() {
		return msg;
	}
}
