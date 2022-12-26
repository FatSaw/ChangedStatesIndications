package me.bomb.indicators;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.plugin.java.JavaPlugin;

import me.clip.placeholderapi.PlaceholderAPI;

final class Lang {
	
	private static final boolean hasplaceholdaeapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
	
	private static final DecimalFormat format = new DecimalFormat("#.##");
	
	protected static String getBossbarText(Player player,LivingEntity entity,Double distance) {
		if(entity.getType()==EntityType.PLAYER) {
			String msg = distance == null?Options.bossbarplayer.text:Options.bossbardistanceplayer.text;
			if(hasplaceholdaeapi) {
				msg = PlaceholderAPI.setPlaceholders((Player)entity,msg);
			}
			if(msg.contains("%csientityname%")) {
				msg = msg.replaceAll("%csientityname%", entity.getName());
			}
			return applyLocalPlaceholders(msg, entity);
		}
		String name = entity.getCustomName();
		String msg = distance==null?name==null?Options.bossbar.text:Options.bossbarnamed.text:name==null?Options.bossbardistance.text:Options.bossbardistancenamed.text;
		if(distance!=null&&msg.contains("%csidistance%")) {
			msg = msg.replaceAll("%csidistance%", format.format(distance));
		}
		if(name!=null&&msg.contains("%csientityname%")) {
			msg = msg.replaceAll("%csientityname%", name);
		}
		return applyLocalPlaceholders(msg, entity);
	}
	
	protected static String getIndicatorText(Player player,LivingEntity entity,double amount,boolean sign) {
		if(entity.getType()==EntityType.PLAYER) {
			String msg = sign?Options.indicatorhealplayer.text:Options.indicatordamageplayer.text;
			if(hasplaceholdaeapi) {
				msg = PlaceholderAPI.setPlaceholders((Player)entity,msg);
			}
			if(msg.contains("%csientityamount%")) {
				msg = msg.replaceAll("%csientityamount%", format.format(amount));
			}
			return applyLocalPlaceholders(msg, entity);
		}
		String name = entity.getCustomName();
		String msg = sign?name==null?Options.indicatorheal.text:Options.indicatorhealnamed.text:name==null?Options.indicatordamage.text:Options.indicatordamagenamed.text;
		if(msg.contains("%csientityamount%")) {
			msg = msg.replaceAll("%csientityamount%", format.format(amount));
		}
		if(name!=null&&msg.contains("%csientityname%")) {
			msg = msg.replaceAll("%csientityname%", name);
		}
		return applyLocalPlaceholders(msg, entity);
	}
	
	private static String applyLocalPlaceholders(String msg,LivingEntity entity) {
		if(msg.contains("%csientitytype%")) msg = msg.replaceAll("%csientitytype%", queryEntityType(entity));
		if(msg.contains("%csientityhealth%")) msg = msg.replaceAll("%csientityhealth%", format.format(entity.getHealth()));
		if(msg.contains("%csientitymaxhealth%")) msg = msg.replaceAll("%csientitymaxhealth%", format.format(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
		return msg;
	}
	
	private static final String queryEntityType(Entity entity) {
        EntityType entityType = entity.getType();
        if(entityType.equals(EntityType.SNOWMAN)) {
        	return "entity.minecraft.snow_golem";
        }
        if (entityType == EntityType.PIG_ZOMBIE) {
        	return "entity.minecraft.zombie_pigman";
        }
        if (entityType == EntityType.VILLAGER) {
        	Profession proffession = ((Villager)entity).getProfession();
        	return proffession==null?"entity.minecraft.villager":"entity.minecraft.villager.".concat(((Villager)entity).getProfession().toString().toLowerCase());
        }
        if (entityType == EntityType.RABBIT&&((Rabbit)entity).getRabbitType()==Rabbit.Type.THE_KILLER_BUNNY) {
        	return "entity.minecraft.killer_bunny";
        }
        if (entityType == EntityType.TROPICAL_FISH) {
        	TropicalFish fish = (TropicalFish)entity;
        	Pattern pattern = fish.getPattern();
        	if(pattern==null) {
        		return "entity.minecraft.tropical_fish";
        	}
        	byte prefefinedtype = -1;
        	switch (fish.getPatternColor().ordinal() << 24 | fish.getBodyColor().ordinal() << 16 | (pattern.ordinal() % 6) << 8 | (pattern.ordinal() > 5 ? 1 : 0)) {
        		case 65536:
        			prefefinedtype = 5;
        		break;
        		case 459008:
        			prefefinedtype = 19;
            	break;
        		case 917504:
        			prefefinedtype = 18;
            	break;
        		case 918273:
        			prefefinedtype = 16;
            	break;
        		case 918529:
        			prefefinedtype = 14;
            	break;
        		case 16778497:
        			prefefinedtype = 11;
            	break;
        		case 50660352:
        			prefefinedtype = 13;
            	break;
        		case 50726144:
        			prefefinedtype = 6;
            	break;
        		case 67108865:
        			prefefinedtype = 17;
            	break;
        		case 67110144:
        			prefefinedtype = 9;
            	break;
        		case 67371009:
        			prefefinedtype = 21;
            	break;
        		case 67699456:
        			prefefinedtype = 20;
            	break;
        		case 67764993:
        			prefefinedtype = 7;
            	break;
        		case 101253888:
        			prefefinedtype = 12;
            	break;
        		case 117441025:
        			prefefinedtype = 10;
            	break;
        		case 117441793:
        			prefefinedtype = 3;
            	break;
        		case 117506305:
        			prefefinedtype = 0;
            	break;
        		case 117899265:
        			prefefinedtype = 1;
            	break;
        		case 118161664:
        			prefefinedtype = 4;
            	break;
        		case 185008129:
        			prefefinedtype = 2;
            	break;
        		case 234882305:
        			prefefinedtype = 8;
            	break;
        		case 235340288:
        			prefefinedtype = 15;
            	break;
        	}
        	if(prefefinedtype==-1) {
        		return "entity.minecraft.tropical_fish.type.".concat(pattern.toString().toLowerCase());
        	}
        	return "entity.minecraft.tropical_fish.predefined.".concat(Byte.toString(prefefinedtype));
        	
        }
        return "entity.minecraft." + entityType.toString().toLowerCase();
    }
	
	private static enum Options {
		bossbardistanceplayer,bossbarplayer,bossbarnamed,bossbardistancenamed,bossbardistance,bossbar,indicatorhealplayer,indicatorhealnamed,indicatorheal,indicatordamageplayer,indicatordamagenamed,indicatordamage;
		static {
			JavaPlugin plugin = JavaPlugin.getPlugin(CSI.class);
			YamlConfiguration alang = null; 
			File langfile = new File(plugin.getDataFolder() + File.separator + "lang.yml");
			if(langfile.isFile()) {
				alang = YamlConfiguration.loadConfiguration(langfile);
			}
			if(alang==null) {
				try {
					Reader langstream = new InputStreamReader(plugin.getResource(Bukkit.getServer().getClass().getPackage().getName().substring(23).equals("v1_16_R3") ? "lang_rgb.yml" : "lang_old.yml"), "UTF8");
					if (langstream != null) {
						alang = YamlConfiguration.loadConfiguration(langstream);
					}
				} catch (UnsupportedEncodingException e) {
				}
				if(!langfile.exists()) {
					try {
						alang.save(langfile);
					} catch (IOException e) {
					}
				}
			}
			for(Options lang : values()) {
				lang.text = alang.getString(lang.toString(),"[{\"text\":\"ERROR LANG OPTION \",\"bold\":true,\"color\":\"dark_red\"},{\"text\":\"".concat(lang.toString()).concat("\",\"bold\":true,\"color\":\"red\"},{\"text\":\" NOT EXSIST\",\"bold\":true,\"color\":\"dark_red\"}]"));
			}
		}
		private String text = "{\"text\":\"ERROR LANG FILE NOT LOADED\",\"bold\":true,\"color\":\"dark_red\"}";
	}
	
}
