package me.bomb.indicators;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CSI extends JavaPlugin implements Listener {
	private YamlConfiguration config;
	private HashMap<UUID,Double> health = new HashMap<UUID,Double>();
	private double range = 0.0;
	private double bossrange = 0.0;
	private double raycaststep = 0.0;
	private static double boxsizemodificator = 0.0;
	private HashMap<UUID,LivingEntity> lastentitys = new HashMap<UUID,LivingEntity>();
	private IndicatorsManager indicatorsmanager;
	private ArrayList<UUID> canrecivepackets = new ArrayList<UUID>();
	private boolean langloaded = false;
	private boolean supported = false;
	protected static Version version = null;
	public void onDisable() {
		indicatorsmanager.disable();
		indicatorsmanager = null;
		lastentitys = null;
		canrecivepackets = null;
	}
	protected static double getBoxSizeModificator() {
		return boxsizemodificator;
	}
	public void onEnable() {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_16_R3":
			indicatorsmanager = new IndicatorsManager_v1_16_R3();
			supported = true;
			try {
				Reader langstream = new InputStreamReader(this.getResource("lang_rgb.yml"), "UTF8");
				if (langstream != null) {
					new Lang(YamlConfiguration.loadConfiguration(langstream));
					langloaded = true;
				}
			} catch (UnsupportedEncodingException e) {
			}
			version = Version.v1_16_R3;
			break;
		case "v1_15_R1":
			indicatorsmanager = new IndicatorsManager_v1_15_R1();
			supported = true;
			try {
				Reader langstream = new InputStreamReader(this.getResource("lang_old.yml"), "UTF8");
				if (langstream != null) {
					new Lang(YamlConfiguration.loadConfiguration(langstream));
					langloaded = true;
				}
			} catch (UnsupportedEncodingException e) {
			}
			version = Version.v1_15_R1;
			break;
		case "v1_14_R1":
			indicatorsmanager = new IndicatorsManager_v1_14_R1();
			supported = true;
			try {
				Reader langstream = new InputStreamReader(this.getResource("lang_old.yml"), "UTF8");
				if (langstream != null) {
					new Lang(YamlConfiguration.loadConfiguration(langstream));
					langloaded = true;
				}
			} catch (UnsupportedEncodingException e) {
			}
			version = Version.v1_14_R1;
			break;
		}
		if(supported) {
			if (new File(getDataFolder() + File.separator + "lang.yml").exists()) {
				new Lang(YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "lang.yml")));
			}
			if (!new File(getDataFolder() + File.separator + "config.yml").exists()) {
				saveResource("config.yml", true);
			}
			config = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "config.yml"));
			range = config.getDouble("range", 16);
			bossrange = config.getDouble("bossrange", 6);
			raycaststep = config.getDouble("raycaststep", 0.1);
			boxsizemodificator = config.getDouble("boxsizemodificator", 0.0);
			new BukkitRunnable() {
				@Override
				public void run() {
					for(Player player : Bukkit.getOnlinePlayers()) {
						canrecivepackets.add(player.getUniqueId());
					}
				}
			}.runTaskLater(this, 30L);
			new BukkitRunnable() {
				@Override
				public void run() {
					if(indicatorsmanager!=null) {
						for(Player player : Bukkit.getOnlinePlayers()) {
							if(canrecivepackets.contains(player.getUniqueId())) {
								if(!player.isDead()) {
							    	float yaw = player.getLocation().getYaw();
									float pitch = player.getLocation().getPitch();
									Vector origin = new Vector(player.getEyeLocation().getX(),player.getEyeLocation().getY(),player.getEyeLocation().getZ());
									RayCast rc = new RayCast(origin,yaw,pitch);
							    	ArrayList<Entity> entitys = (ArrayList<Entity>) player.getNearbyEntities(bossrange+0.5,bossrange+0.5,bossrange+0.5);
							    	double distance = bossrange;
							    	LivingEntity entity = null;
							    	for(Entity aentity : entitys) {
							    		if(aentity instanceof LivingEntity) {
							    			BoundingBox entitybb = new BoundingBox(aentity);
								    		Vector el = rc.positionOfIntersection(entitybb, bossrange+0.4, raycaststep);
								    		if(el!=null) {
								    			if(distance>el.distance(origin)) {
								    				distance = el.distance(origin);
								    				entity = (LivingEntity) aentity;
								    			}
								    		}
							    		}
							    	}
							    	if(entity==null) {
							    		if(lastentitys.containsKey(player.getUniqueId())) {
							    			LivingEntity lastentity = lastentitys.get(player.getUniqueId());
								    			if(indicatorsmanager.updateBossBar(player, lastentity, false)) {
								    				lastentity = null;
									    			lastentitys.remove(player.getUniqueId());
									    		}
							    		}
							    	} else {
							    		if(player.getTargetBlockExact((int) Math.round(distance))==null) {
							    			indicatorsmanager.updateBossBar(player, entity, true);
							    			lastentitys.put(player.getUniqueId(), entity);
							    		}
						    		}
								} else {
									indicatorsmanager.updateBossBar(player, null, false);
									lastentitys.remove(player.getUniqueId());
									//dead
								}
							}
						}
					}
				}
			}.runTaskTimer(this, 0L, (byte)config.getInt("bossbarupdate", 5));
			
			new BukkitRunnable() {
				@Override
				public void run() {
					HashMap<UUID,Double> newhealth = new HashMap<UUID,Double>();
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(health.containsKey(player.getUniqueId())) {
							double previoushealth = health.get(player.getUniqueId());
							double ahealth = player.getHealth();
							if(Math.round(ahealth-previoushealth)>0) {
								if(!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
									for(Player nearplayer : player.getWorld().getPlayers()) {
										if(canrecivepackets.contains(nearplayer.getUniqueId())) {
											if(!nearplayer.getUniqueId().equals(player.getUniqueId()) && player.getLocation().distance(nearplayer.getLocation()) < range) {
												indicatorsmanager.createIndicator(nearplayer,player,(int)Math.round(ahealth-previoushealth),true);
											}
										}
									}
								}
								newhealth.put(player.getUniqueId(), player.getHealth());
							} else if(Math.round(ahealth-previoushealth)<0) {
								if(!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
									for(Player nearplayer : player.getWorld().getPlayers()) {
										if(!nearplayer.getUniqueId().equals(player.getUniqueId()) && player.getLocation().distance(nearplayer.getLocation()) < range) {
											indicatorsmanager.createIndicator(nearplayer,player,(int)Math.round(previoushealth-ahealth),false);
										};
									}
								}
								newhealth.put(player.getUniqueId(), player.getHealth());
							} else {
								newhealth.put(player.getUniqueId(), player.getHealth());
							}
						} else {
							newhealth.put(player.getUniqueId(), player.getHealth());
						}
					}
					health.clear();
					health.putAll(newhealth);
				}
			}.runTaskTimer(this, 0L, 1L);
			Bukkit.getPluginManager().registerEvents(this, this);
			if(langloaded) {
				getLogger().log(Level.INFO, "Language file successfully loaded!");
			} else {
				getLogger().log(Level.WARNING, "Language file not loaded!");
			}
			getLogger().log(Level.INFO, "Plugin enabeled!");
		} else {
			getLogger().log(Level.WARNING, "Unsupported version!");
			getLogger().log(Level.WARNING, "Supported versions: 1.14(+3),1.15(+2),1.16.4(+1)");
			getServer().getPluginManager().disablePlugin(this);
		}
		
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		indicatorsmanager.logout(event.getPlayer());
		lastentitys.remove(event.getPlayer().getUniqueId());
		canrecivepackets.remove(event.getPlayer().getUniqueId());
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(event.getPlayer().isOnline()) canrecivepackets.add(event.getPlayer().getUniqueId());
			}
		}.runTaskLater(this, 30L);
	}
	@EventHandler
	public void onEntityRegainHealthEvent(EntityRegainHealthEvent event) {
		if(!event.isCancelled()) {
			if(event.getEntity() instanceof LivingEntity && !event.getEntity().getType().equals(EntityType.PLAYER)) {
				LivingEntity entity = (LivingEntity) event.getEntity();
				if(!entity.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					double amount = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - entity.getHealth();
					if(amount>event.getAmount()) amount = event.getAmount();
					if(Math.round(amount)>0) {
						for(Player player : entity.getWorld().getPlayers()) {
							if(entity.getLocation().distance(player.getLocation()) < range) {
								indicatorsmanager.createIndicator(player,entity,(int)Math.round(amount),true);
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if(!event.isCancelled()) {
			if(event.getEntity() instanceof LivingEntity && !event.getEntity().getType().equals(EntityType.PLAYER)) {
				LivingEntity entity = (LivingEntity) event.getEntity();
				if(!entity.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					double amount = entity.getHealth();
					if(amount>event.getFinalDamage()) amount = event.getFinalDamage();
					if(Math.round(amount)>0) {
						for(Player player : entity.getWorld().getPlayers()) {
							if(entity.getLocation().distance(player.getLocation()) < range) {
								indicatorsmanager.createIndicator(player,entity,(int)Math.round(amount),false);
							}
						}
					}
				}
			}
		}
	}
}