
package net.craftstars.general.command.misc;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.craftstars.general.General;
import net.craftstars.general.command.CommandBase;
import net.craftstars.general.mobs.MobType;
import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

public class mobspawnCommand extends CommandBase {
	protected mobspawnCommand(General instance) {
		super(instance);
	}

	@Override
	public boolean fromPlayer(Player sender, Command command, String commandLabel, String[] args) {
		if(Toolbox.lacksPermission(sender, "general.mobspawn"))
			return Messaging.lacksPermission(sender, "spawn mobs");
		Location where = Toolbox.getTargetBlock(sender);
		switch(args.length) {
		case 1:
			if(args[0].equalsIgnoreCase("SpiderJockey"))
				doCompoundSpawn(sender, "Skeleton", "Spider", where);
			else doSimpleSpawn(sender, args[0], where);
			return true;
		case 2:
			try {
				int n = Integer.valueOf(args[1]);
				if(n > 5 && Toolbox.lacksPermission(sender, "general.mobspawn.mass"))
					return Messaging.lacksPermission(sender, "spawn mobs en masse");
				if(args[0].equalsIgnoreCase("SpiderJockey"))
					while(n-- > 0)
						doCompoundSpawn(sender, "Skeleton", "Spider", where);
				else while(n-- > 0)
					doSimpleSpawn(sender, args[0], where);
			} catch(NumberFormatException e) {
				doCompoundSpawn(sender, args[0], args[1], where);
			}
			return true;
		case 3:
			try {
				int n = Integer.valueOf(args[2]);
				if(n > 5 && Toolbox.lacksPermission(sender, "general.mobspawn.mass"))
					return Messaging.lacksPermission(sender, "spawn mobs en masse");
				if(args[0].equals("-") || args[1].equals("-"))
					while(n-- > 0)
						doSimpleSpawn(sender, (args[0] + args[1]).replace("-", ""), where);
				else while(n-- > 0)
					doCompoundSpawn(sender, args[0], args[1], where);
			} catch(NumberFormatException e) {
				return false;
			}
			return true;
		default:
			return false;
		}
	}
	
	private LivingEntity doSimpleSpawn(Player sender, String mobName, Location where) {
		String[] split;
		split = mobName.split("[.,:/\\|]", 2);
		MobType mob = MobType.getMob(split[0]);
		if(mob == null) {
			Messaging.send(sender, "&cInvalid mob type: " + mobName);
			return null;
		}
		LivingEntity entity = mob.spawn(sender, where);
		if(split.length == 2) {
			if(!mob.setData(entity, sender, split[1])) Messaging.send(sender, "&cError setting the data.");
		}
		return entity;
	}
	
	private LivingEntity doCompoundSpawn(Player sender, String riderName, String mountName, Location where) {
		LivingEntity rider = doSimpleSpawn(sender, riderName, where);
		if(rider == null) return null;
		LivingEntity mount = doSimpleSpawn(sender, mountName, where);
		if(mount == null) {
			rider.remove();
			return null;
		}
		mount.setPassenger(rider);
		return mount;
	}
	
	@Override
	public boolean fromConsole(ConsoleCommandSender sender, Command command, String commandLabel,
			String[] args) {
		Messaging.send(sender, "&cSorry, this command can only be used by a player.");
		return true;
	}
}
