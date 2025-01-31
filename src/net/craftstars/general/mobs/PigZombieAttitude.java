package net.craftstars.general.mobs;

import net.craftstars.general.util.Messaging;
import net.craftstars.general.util.Toolbox;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;

public class PigZombieAttitude extends MobData {
	private int anger = 0;
	private final static String[] values = new String[] {"regular","angry"};
	
	@Override
	public boolean hasPermission(CommandSender byWhom) {
		if(anger > 0)
			return Toolbox.hasPermission(byWhom, "general.mobspawn.variants", "general.mobspawn.pig-zombie.angry", "general.mobspawn.neutral.angry");
		return true;
	}
	
	@Override
	public void setForMob(LivingEntity mob) {
		if(anger == 0) return; // No need to set it to 0 when it already is, right?
		if(!(mob instanceof PigZombie)) return;
		PigZombie zom = (PigZombie) mob;
		zom.setAnger(anger);
	}
	
	@Override
	public void parse(CommandSender setter, String data) {
		if(Toolbox.equalsOne(data, MobType.PIG_ZOMBIE.getDataList("calm"))) anger = 0;
		else if(Toolbox.equalsOne(data, MobType.PIG_ZOMBIE.getDataList("angry"))) anger = 400;
		else {
			try {
				anger = Integer.parseInt(data);
			} catch(NumberFormatException e) {
				invalidate();
			}
		}
	}
	
	@Override
	public String getCostNode(String baseNode) {
		if(anger > 0) return baseNode + ".angry";
		return baseNode + ".regular";
	}
	
	@Override
	public void lacksPermission(CommandSender fromWhom) {
		if(anger > 0) Messaging.lacksPermission(fromWhom, "general.mobspawn.pig-zombie.angry");
	}

	@Override
	public String[] getValues() {
		return values.clone();
	}
	
}
