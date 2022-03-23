package me.bomb.indicators;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

class Lang {
	private static YamlConfiguration langconfiguration = null;
	protected Lang(YamlConfiguration langconfiguration) {
		Lang.langconfiguration = langconfiguration;
	}
	protected static String getLocalizedText(Player player,String entityname,int health,int maxhealth, LangMsg langmsg) {
		if(langconfiguration == null) return "{\"text\":\"ERROR LANG FILE NOT LOADED\",\"bold\":true,\"color\":\"red\"}";
		String lang = getLocale(player);
		String msg = langconfiguration.getString(lang + "." + langmsg.getKey(), langconfiguration.getString("default." + langmsg.getKey(), ""));
		if(msg.isEmpty()) msg = langconfiguration.getString(lang + "." + LangMsg.getMessage(EntityType.UNKNOWN, langmsg.getType()).getKey(), langconfiguration.getString("default." + LangMsg.getMessage(EntityType.UNKNOWN, langmsg.getType()).getKey(),"{\"text\":\"ERROR ON GET MESSAGE FROM LANG FILE\",\"bold\":true,\"color\":\"red\"}"));
		msg = msg.replaceAll("%csientityname%", entityname);
		msg = msg.replaceAll("%csientityhealth%", Integer.toString(health));
		msg = msg.replaceAll("%csientitymaxhealth%", Integer.toString(maxhealth));
		return msg;
	}
	protected static String getLocale(Player player) {
		switch (CSI.version) {
		case v1_16_R3:
			return ((org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case v1_15_R1:
			return ((org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case v1_14_R1:
			return ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		default:
			return "default";
		}
	}
}
