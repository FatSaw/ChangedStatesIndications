package me.bomb.indicators;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.v1_15_R1.BossBattleCustom;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.MinecraftKey;
import net.minecraft.server.v1_15_R1.PacketPlayOutBoss;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_15_R1.BossBattle;
import net.minecraft.server.v1_15_R1.BossBattle.BarColor;
import net.minecraft.server.v1_15_R1.BossBattle.BarStyle;
import net.minecraft.server.v1_15_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_15_R1.PacketPlayOutBoss.Action;

final class IndicatorsManager_v1_15_R1 extends IndicatorsManager {
	
	
	protected IndicatorsManager_v1_15_R1() {
		JavaPlugin plugin = CSI.getPlugin(CSI.class);
		hasplaceholdaeapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
		bossbarhold = (byte) plugin.getConfig().getInt("bossbarhold", 8);
		indicatorsteps = (ArrayList<Double>) plugin.getConfig().getDoubleList("indicatorsteps");
		bossbarcolor = BarColor.a(plugin.getConfig().getString("bossbarcolor", "white"));
		bossbarstyle = BarStyle.a(plugin.getConfig().getString("bossbarstyle", "progress"));
		new BukkitRunnable() {
    	    @Override
    	    public void run(){
    	    	if(battle!=null) {
    	    		for(Player player : Bukkit.getOnlinePlayers()) {
        	    		if(battle.containsKey(player.getUniqueId())) {
        	    			if(targeted.contains(player.getUniqueId())) {
        	    				holder.put(player.getUniqueId(), bossbarhold);
        	    			} else {
        	    				if(holder.containsKey(player.getUniqueId())) {
            	    				byte hold = holder.get(player.getUniqueId());
            	    				if(--hold>0) {
            	    					holder.put(player.getUniqueId(), hold);
            	    				} else {
            	    					holder.remove(player.getUniqueId());
            	    				}
            	    			}
        	    			}
        	    			if(holder.containsKey(player.getUniqueId())) {
        	    				BossBattleCustom bossbattle = (BossBattleCustom) battle.get(player.getUniqueId());
        	    				float procent = bossbattle.getProgress();
        	    				IChatBaseComponent text = bossbattle.j();
        	    				if(see.contains(player.getUniqueId())) {
        	    					//update
        	    					if((health.get(player.getUniqueId()) != procent)) {
        	    						sendBossUpdateProcentPacket(player);
        	    						health.put(player.getUniqueId(), procent);
        	    					}
        	    					if((basecomponent.get(player.getUniqueId()) != text)) {
        	    						sendBossUpdateNamePacket(player);
        	    						basecomponent.put(player.getUniqueId(), text);
        	    					}
        	    				} else {
        	    					//send new
        	    					sendBossCreatePacket(player);
        	    					health.put(player.getUniqueId(), procent);
        	    					basecomponent.put(player.getUniqueId(), text);
        	    					see.add(player.getUniqueId());
        	    				}
        	    			} else {
        	    				//remove
        	    				sendBossRemovePacket(player);
        	    				battle.remove(player.getUniqueId());
        	    				targeted.remove(player.getUniqueId());
        	    				see.remove(player.getUniqueId());
        	    				basecomponent.remove(player.getUniqueId());
        	    				health.remove(player.getUniqueId());
        	    			}
        	    		} else {
        	    			see.remove(player.getUniqueId());
        	    			basecomponent.remove(player.getUniqueId());
        	    			health.remove(player.getUniqueId());
        	    			holder.remove(player.getUniqueId());
        	    		}
        	    	}
    	    	}
    	    }
    	}.runTaskTimerAsynchronously(plugin, 0L, (byte)plugin.getConfig().getInt("bossbarupdate", 5));
    	new BukkitRunnable() {
    	    @Override
    	    public void run(){
    	    	if(astands!=null) {
    	    		for(Player player : Bukkit.getOnlinePlayers()) {
    	    			if(astands.containsKey(player.getUniqueId())) {
    	    				HashMap<Object,Byte> stands = new HashMap<Object,Byte>();
    	    				for(Object standobject : astands.get(player.getUniqueId()).keySet()) {
    	    					EntityArmorStand stand = (EntityArmorStand) standobject;
    	    					byte step = astands.get(player.getUniqueId()).get(stand);
    	    					if(step<indicatorsteps.size()) {
    	    						stand.setLocation(stand.locX(), stand.locY()+indicatorsteps.get(step), stand.locZ(), 0, 0);
    	    						sendIndicatorTeleportPacket(player, stand);
    	    						stands.put(stand, ++step);
    	    					} else {
    	    						sendIndicatorDestroyPacket(player, stand);
    	    					}
    	    				}
    	    				astands.put(player.getUniqueId(), stands);
    	    			}
    	    		}
    	    	}
    	    }
    	}.runTaskTimerAsynchronously(plugin, 0L, 1L);
	}
	public boolean updateBossBar(Player player,Object entity,Double distance) {
		if(battle!=null) {
			boolean target = distance!=null;
			if(entity == null) {
				sendBossRemovePacket(player);
				battle.remove(player.getUniqueId());
				see.remove(player.getUniqueId());
    			basecomponent.remove(player.getUniqueId());
    			health.remove(player.getUniqueId());
    			holder.remove(player.getUniqueId());
			} else {
				LivingEntity eentity = (LivingEntity) entity;
				if(battle.containsKey(player.getUniqueId())) {
					BossBattleCustom bossbattle = (BossBattleCustom) battle.get(player.getUniqueId());
					if(bossbattle==null) return true;
					String msg = Lang.getBossbarText(player, eentity, distance);
					if(hasplaceholdaeapi && eentity.getType().equals(EntityType.PLAYER)) {
						msg = PlaceholderAPI.setPlaceholders((Player)entity, msg);
					}
					float procent = (float)(eentity.getHealth()/eentity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
					bossbattle.a(procent);
					bossbattle.a(ChatSerializer.a(msg));
					if(target) {
						if(!targeted.contains(player.getUniqueId())) targeted.add(player.getUniqueId());
					} else {
						targeted.remove(player.getUniqueId());
					}
					battle.put(player.getUniqueId(), bossbattle);
					
				} else if(target) {
					String msg = Lang.getBossbarText(player, eentity, distance);
					if(hasplaceholdaeapi && eentity.getType().equals(EntityType.PLAYER)) {
						msg = PlaceholderAPI.setPlaceholders((Player)entity, msg);
					}
					float procent = (float)(eentity.getHealth()/eentity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
					BossBattleCustom bossbattle = new BossBattleCustom(new MinecraftKey("entityhealth"),ChatSerializer.a(msg));
					bossbattle.a((BarColor)bossbarcolor);
					bossbattle.a((BarStyle)bossbarstyle);
					bossbattle.a(procent);
					targeted.add(player.getUniqueId());
					battle.put(player.getUniqueId(), bossbattle);
				} else return true;
			}
		}
		return false;
	}
	public void createIndicator(Player player,Object entity, double amount,boolean addition) {
		LivingEntity eentity = (LivingEntity) entity;
		Location location = eentity.getLocation();
		EntityArmorStand stand = new EntityArmorStand(EntityTypes.ARMOR_STAND,((CraftWorld) eentity.getWorld()).getHandle());
		stand.setMarker(true);
		stand.setNoGravity(true);
		stand.setInvisible(true);
		stand.setInvulnerable(true);
		stand.setSilent(true);
		stand.setLocation(location.getX(), location.getY()+eentity.getHeight()+0.1d, location.getZ(), 0, 0);
		String indicatormsg = Lang.getIndicatorText(player,eentity,amount,addition);
		if(eentity.getType().equals(EntityType.PLAYER) && hasplaceholdaeapi) {
			indicatormsg = PlaceholderAPI.setPlaceholders((Player)entity, indicatormsg);
		}
		stand.setCustomName(ChatSerializer.a(indicatormsg));
		stand.setCustomNameVisible(true);
		sendIndicatorCreatePacket(player,stand);
		if(astands.containsKey(player.getUniqueId())) {
			HashMap<Object,Byte> stands = astands.get(player.getUniqueId());
			stands.put(stand, (byte) 0);
			astands.put(player.getUniqueId(),stands);
		} else {
			HashMap<Object,Byte> stands = new HashMap<Object,Byte>();
			stands.put(stand, (byte) 0);
			astands.put(player.getUniqueId(),stands);
		}
	}
	
	public void sendBossRemovePacket(Object player) {
		EntityPlayer eplayer = ((CraftPlayer)player).getHandle();
		PacketPlayOutBoss removeboss = new PacketPlayOutBoss(Action.REMOVE, (BossBattle) battle.get(eplayer.getUniqueID()));
		eplayer.playerConnection.sendPacket(removeboss);
	}
	public void sendBossCreatePacket(Object player) {
		EntityPlayer eplayer = ((CraftPlayer)player).getHandle();
		PacketPlayOutBoss createboss = new PacketPlayOutBoss(Action.ADD, (BossBattle) battle.get(eplayer.getUniqueID()));
		eplayer.playerConnection.sendPacket(createboss);
	}
	public void sendBossUpdateNamePacket(Object player) {
		EntityPlayer eplayer = ((CraftPlayer)player).getHandle();
		PacketPlayOutBoss updatenameboss = new PacketPlayOutBoss(Action.UPDATE_NAME, (BossBattle) battle.get(eplayer.getUniqueID()));
		eplayer.playerConnection.sendPacket(updatenameboss);
	}
	public void sendBossUpdateProcentPacket(Object player) {
		EntityPlayer eplayer = ((CraftPlayer)player).getHandle();
		PacketPlayOutBoss updateprocentboss = new PacketPlayOutBoss(Action.UPDATE_PCT, (BossBattle) battle.get(eplayer.getUniqueID()));
		eplayer.playerConnection.sendPacket(updateprocentboss);
	}
	public void sendIndicatorCreatePacket(Object player,Object stand) {
		EntityPlayer eplayer = ((CraftPlayer)player).getHandle();
		EntityArmorStand estand = (EntityArmorStand) stand;
		PacketPlayOutSpawnEntityLiving spawnindicator = new PacketPlayOutSpawnEntityLiving((EntityLiving) stand);
		PacketPlayOutEntityMetadata	metadataindicator = new PacketPlayOutEntityMetadata(estand.getId(), estand.getDataWatcher(), false);
		eplayer.playerConnection.sendPacket(spawnindicator);
		eplayer.playerConnection.sendPacket(metadataindicator);
	}
	public void sendIndicatorTeleportPacket(Object player,Object stand) {
		EntityPlayer eplayer = ((CraftPlayer)player).getHandle();
		PacketPlayOutEntityTeleport teleportindicator = new PacketPlayOutEntityTeleport((EntityLiving) stand);
		eplayer.playerConnection.sendPacket(teleportindicator);
	}
	public void sendIndicatorDestroyPacket(Object player,Object stand) {
		EntityPlayer eplayer = ((CraftPlayer)player).getHandle();
		PacketPlayOutEntityDestroy destroyindicator = new PacketPlayOutEntityDestroy(((EntityLiving) stand).getId());
		eplayer.playerConnection.sendPacket(destroyindicator);
	}
}
