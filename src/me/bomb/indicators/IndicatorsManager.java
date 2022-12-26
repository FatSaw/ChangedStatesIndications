package me.bomb.indicators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class IndicatorsManager {
	
	protected static final IndicatorsManager indicatorsmanager;
	
	static {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_16_R3":
			indicatorsmanager = new IndicatorsManager_v1_16_R3();
			break;
		case "v1_15_R1":
			indicatorsmanager = new IndicatorsManager_v1_15_R1();
			break;
		case "v1_14_R1":
			indicatorsmanager = new IndicatorsManager_v1_14_R1();
			break;
		default:
			indicatorsmanager = null;
		}
	}
	
	protected boolean hasplaceholdaeapi;
	protected byte bossbarhold;
	protected ArrayList<Double> indicatorsteps;
	protected Object bossbarcolor;
	protected Object bossbarstyle;
	
	protected HashMap<UUID,Object> basecomponent = new HashMap<UUID,Object>();
	protected HashMap<UUID,Float> health = new HashMap<UUID,Float>();
	protected HashMap<UUID,Byte> holder = new HashMap<UUID,Byte>();
	protected ArrayList<UUID> see = new ArrayList<UUID>();

	protected HashMap<UUID,Object> battle = new HashMap<UUID,Object>(); //safe
	protected ArrayList<UUID> targeted = new ArrayList<UUID>(); //safe
	protected HashMap<UUID,HashMap<Object,Byte>> astands = new HashMap<UUID,HashMap<Object,Byte>>(); //safe

	protected void logout(Player player) {
		targeted.remove(player.getUniqueId());
		battle.remove(player.getUniqueId());
		astands.remove(player.getUniqueId());
	}
	
	protected void disable() {
		if(see!=null && astands!=null) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(see.contains(player.getUniqueId())) {
					sendBossRemovePacket(player);
				}
				if(astands.containsKey(player.getUniqueId())) {
					for(Object stand : astands.get(player.getUniqueId()).keySet()) {
						sendIndicatorDestroyPacket(player,stand);
					}
				}
			}
		}
		see = null;
		astands = null;
		basecomponent = null;
		health = null;
		holder = null;
		targeted = null;
		battle = null;
	}
	
	public abstract boolean updateBossBar(Player player,Object entity,Double distance);
	public abstract void createIndicator(Player player,Object entity, double amount,boolean addition);
	public abstract void sendBossRemovePacket(Object player);
	public abstract void sendBossCreatePacket(Object player);
	public abstract void sendBossUpdateNamePacket(Object player);
	public abstract void sendBossUpdateProcentPacket(Object player);
	public abstract void sendIndicatorCreatePacket(Object player,Object stand);
	public abstract void sendIndicatorTeleportPacket(Object player,Object stand);
	public abstract void sendIndicatorDestroyPacket(Object player,Object stand);
}
