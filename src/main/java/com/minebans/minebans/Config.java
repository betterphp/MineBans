package com.minebans.minebans;

import java.lang.reflect.Field;
import java.util.HashMap;

import uk.co.jacekk.bukkit.baseplugin.v7.config.PluginConfigKey;

import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.bans.BanSeverity;

public class Config {
	
	public static final PluginConfigKey API_KEY									= new PluginConfigKey("api-key", "change this to the one from your control panel");
	
	public static final PluginConfigKey USE_COMPACT_JOIN_INFO					= new PluginConfigKey("use-compact-join-info", false);
	public static final PluginConfigKey USE_GROUP_BANS							= new PluginConfigKey("use-group-bans", true);
	
	public static final PluginConfigKey BLOCK_PROXIES							= new PluginConfigKey("block-public-proxies", true);
	public static final PluginConfigKey BLOCK_COMPROMISED_ACCOUNTS				= new PluginConfigKey("block-known-compromised-accounts", true);
	
	public static final PluginConfigKey MESSAGE_KICK							= new PluginConfigKey("messages.kick", "You have been kicked from the server.");
	public static final PluginConfigKey MESSAGE_BAN								= new PluginConfigKey("messages.ban", "You have been banned from the server.");
	public static final PluginConfigKey MESSAGE_TEMPBAN							= new PluginConfigKey("messages.tempban", "You have been banned from the server for ");
	public static final PluginConfigKey MESSAGE_UNEXEMPT						= new PluginConfigKey("messages.unexempt", "You have been removed from the exempt list.");
	
	public static final PluginConfigKey BAN_COMMANDS_AUTO						= new PluginConfigKey("ban.commands.autoexec", false);
	public static final PluginConfigKey MAX_TEMP_BAN_DURATION					= new PluginConfigKey("ban.max-temp-duration", "7d");
	public static final PluginConfigKey GLOBAL_BAN_COMMANDS						= new PluginConfigKey("ban.commands.global", new String[]{"lb rb player %player_name% world world since 10.10.1010", "lb rb player %player_name% world world_nether since 10.10.1010"});
	public static final PluginConfigKey LOCAL_BAN_COMMANDS						= new PluginConfigKey("ban.commands.local", new String[]{"lb rb player %player_name% world world since 10.10.1010", "lb rb player %player_name% world world_nether since 10.10.1010"});
	public static final PluginConfigKey TEMP_BAN_COMMANDS						= new PluginConfigKey("ban.commands.temp", new String[]{"lb rb player %player_name% world world since 3600", "lb rb player %player_name% world world_nether since 3600"});
	
	public static final PluginConfigKey MAX_BANS_TOTAL_IGNORE_DISABLED_RULES	= new PluginConfigKey("max-bans.total.ignore-disabled-rules", true);
	public static final PluginConfigKey MAX_BANS_TOTAL_TOTAL					= new PluginConfigKey("max-bans.total.total", 15L);
	public static final PluginConfigKey MAX_BANS_TOTAL_CONFIRMED				= new PluginConfigKey("max-bans.total.confirmed", -1L);
	public static final PluginConfigKey MAX_BANS_TOTAL_UNCONFIRMED				= new PluginConfigKey("max-bans.total.unconfirmed", -1L);
	public static final PluginConfigKey MAX_BANS_TOTAL_LOW						= new PluginConfigKey("max-bans.total.low", -1L);
	public static final PluginConfigKey MAX_BANS_TOTAL_MEDIUM					= new PluginConfigKey("max-bans.total.medium", 5L);
	public static final PluginConfigKey MAX_BANS_TOTAL_HIGH						= new PluginConfigKey("max-bans.total.high", 5L);
		
	public static final PluginConfigKey MAX_BANS_THEFT_ENABLED					= new PluginConfigKey("max-bans.theft.enabled", true);
	public static final PluginConfigKey MAX_BANS_THEFT_TOTAL					= new PluginConfigKey("max-bans.theft.total", 15L);
	public static final PluginConfigKey MAX_BANS_THEFT_CONFIRMED				= new PluginConfigKey("max-bans.theft.confirmed", -1L);
	public static final PluginConfigKey MAX_BANS_THEFT_UNCONFIRMED				= new PluginConfigKey("max-bans.theft.unconfirmed",	-1L);
	public static final PluginConfigKey MAX_BANS_THEFT_LOW						= new PluginConfigKey("max-bans.theft.low", -1L);
	public static final PluginConfigKey MAX_BANS_THEFT_MEDIUM					= new PluginConfigKey("max-bans.theft.medium", 5L);
	public static final PluginConfigKey MAX_BANS_THEFT_HIGH						= new PluginConfigKey("max-bans.theft.high", 5L);
	
	public static final PluginConfigKey MAX_BANS_GRIEF_ENABLED					= new PluginConfigKey("max-bans.grief.enabled", true);
	public static final PluginConfigKey MAX_BANS_GRIEF_TOTAL					= new PluginConfigKey("max-bans.grief.total", 15L);
	public static final PluginConfigKey MAX_BANS_GRIEF_CONFIRMED				= new PluginConfigKey("max-bans.grief.confirmed", -1L);
	public static final PluginConfigKey MAX_BANS_GRIEF_UNCONFIRMED				= new PluginConfigKey("max-bans.grief.unconfirmed", -1L);
	public static final PluginConfigKey MAX_BANS_GRIEF_LOW						= new PluginConfigKey("max-bans.grief.low", -1L);
	public static final PluginConfigKey MAX_BANS_GRIEF_MEDIUM					= new PluginConfigKey("max-bans.grief.medium", 5L);
	public static final PluginConfigKey MAX_BANS_GRIEF_HIGH						= new PluginConfigKey("max-bans.grief.high", 5L);
	
	public static final PluginConfigKey MAX_BANS_XRAY_ENABLED					= new PluginConfigKey("max-bans.x-ray.enabled", true);
	public static final PluginConfigKey MAX_BANS_XRAY_TOTAL						= new PluginConfigKey("max-bans.x-ray.total", 15L);
	public static final PluginConfigKey MAX_BANS_XRAY_CONFIRMED					= new PluginConfigKey("max-bans.x-ray.confirmed", -1L);
	public static final PluginConfigKey MAX_BANS_XRAY_UNCONFIRMED				= new PluginConfigKey("max-bans.x-ray.unconfirmed", -1L);
	public static final PluginConfigKey MAX_BANS_XRAY_LOW						= new PluginConfigKey("max-bans.x-ray.low", -1L);
	public static final PluginConfigKey MAX_BANS_XRAY_MEDIUM					= new PluginConfigKey("max-bans.x-ray.medium", 5L);
	public static final PluginConfigKey MAX_BANS_XRAY_HIGH						= new PluginConfigKey("max-bans.x-ray.high", 5L);
	
	public static final PluginConfigKey MAX_BANS_ABUSE_ENABLED					= new PluginConfigKey("max-bans.abuse.enabled", false);
	public static final PluginConfigKey MAX_BANS_ABUSE_TOTAL					= new PluginConfigKey("max-bans.abuse.total", -1L);
	public static final PluginConfigKey MAX_BANS_ABUSE_CONFIRMED				= new PluginConfigKey("max-bans.abuse.confirmed", 10L);
	public static final PluginConfigKey MAX_BANS_ABUSE_UNCONFIRMED				= new PluginConfigKey("max-bans.abuse.unconfirmed", -1L);
	
	public static final PluginConfigKey MAX_BANS_ADVERTISING_ENABLED			= new PluginConfigKey("max-bans.advertising.enabled", false);
	public static final PluginConfigKey MAX_BANS_ADVERTISING_TOTAL				= new PluginConfigKey("max-bans.advertising.total", -1L);
	public static final PluginConfigKey MAX_BANS_ADVERTISING_CONFIRMED			= new PluginConfigKey("max-bans.advertising.confirmed", 10L);
	public static final PluginConfigKey MAX_BANS_ADVERTISING_UNCONFIRMED		= new PluginConfigKey("max-bans.advertising.unconfirmed", -1L);
	
	public static final PluginConfigKey MAX_BANS_FLY_ENABLED					= new PluginConfigKey("max-bans.fly.enabled", true);
	public static final PluginConfigKey MAX_BANS_FLY_TOTAL						= new PluginConfigKey("max-bans.fly.total", -1L);
	public static final PluginConfigKey MAX_BANS_FLY_CONFIRMED					= new PluginConfigKey("max-bans.fly.confirmed", 10L);
	public static final PluginConfigKey MAX_BANS_FLY_UNCONFIRMED				= new PluginConfigKey("max-bans.fly.unconfirmed", -1L);
	
	public static final PluginConfigKey MAX_BANS_MOVE_SPEED_ENABLED				= new PluginConfigKey("max-bans.movement-speed.enabled", true);
	public static final PluginConfigKey MAX_BANS_MOVE_SPEED_TOTAL				= new PluginConfigKey("max-bans.movement-speed.total", -1L);
	public static final PluginConfigKey MAX_BANS_MOVE_SPEED_CONFIRMED			= new PluginConfigKey("max-bans.movement-speed.confirmed", 10L);
	public static final PluginConfigKey MAX_BANS_MOVE_SPEED_UNCONFIRMED			= new PluginConfigKey("max-bans.movement-speed.unconfirmed", -1L);
	
	public static final PluginConfigKey MAX_BANS_BLOCK_REACH_ENABLED			= new PluginConfigKey("max-bans.block-reach.enabled", true);
	public static final PluginConfigKey MAX_BANS_BLOCK_REACH_TOTAL				= new PluginConfigKey("max-bans.block-reach.total", -1L);
	public static final PluginConfigKey MAX_BANS_BLOCK_REACH_CONFIRMED			= new PluginConfigKey("max-bans.block-reach.confirmed", 10L);
	public static final PluginConfigKey MAX_BANS_BLOCK_REACH_UNCONFIRMED		= new PluginConfigKey("max-bans.block-reach.unconfirmed", -1L);
	
	public static final PluginConfigKey MAX_BANS_NOFALL_ENABLED					= new PluginConfigKey("max-bans.nofall.enabled", true);
	public static final PluginConfigKey MAX_BANS_NOFALL_TOTAL					= new PluginConfigKey("max-bans.nofall.total", -1L);
	public static final PluginConfigKey MAX_BANS_NOFALL_CONFIRMED				= new PluginConfigKey("max-bans.nofall.confirmed", 10L);
	public static final PluginConfigKey MAX_BANS_NOFALL_UNCONFIRMED				= new PluginConfigKey("max-bans.nofall.unconfirmed", -1L);
	
	public static final PluginConfigKey MAX_BANS_NOSWING_ENABLED				= new PluginConfigKey("max-bans.noswing.enabled", false);
	public static final PluginConfigKey MAX_BANS_NOSWING_TOTAL					= new PluginConfigKey("max-bans.noswing.total", -1L);
	public static final PluginConfigKey MAX_BANS_NOSWING_CONFIRMED				= new PluginConfigKey("max-bans.noswing.confirmed",	 10L);
	public static final PluginConfigKey MAX_BANS_NOSWING_UNCONFIRMED			= new PluginConfigKey("max-bans.noswing.unconfirmed", -1L);
	
	public static final PluginConfigKey MAX_BANS_PVP_CHEATS_ENABLED				= new PluginConfigKey("max-bans.pvp-cheats.enabled", false);
	public static final PluginConfigKey MAX_BANS_PVP_CHEATS_TOTAL				= new PluginConfigKey("max-bans.pvp-cheats.total", -1L);
	public static final PluginConfigKey MAX_BANS_PVP_CHEATS_CONFIRMED			= new PluginConfigKey("max-bans.pvp-cheats.confirmed", 10L);
	public static final PluginConfigKey MAX_BANS_PVP_CHEATS_UNCONFIRMED			= new PluginConfigKey("max-bans.pvp-cheats.unconfirmed", -1L);
	
	public static final PluginConfigKey MAX_BANS_CHAT_SPAM_ENABLED				= new PluginConfigKey("max-bans.chat-spam.enabled",	 true);
	public static final PluginConfigKey MAX_BANS_CHAT_SPAM_TOTAL				= new PluginConfigKey("max-bans.chat-spam.total", -1L);
	public static final PluginConfigKey MAX_BANS_CHAT_SPAM_CONFIRMED			= new PluginConfigKey("max-bans.chat-spam.confirmed", 10L);
	public static final PluginConfigKey MAX_BANS_CHAT_SPAM_UNCONFIRMED			= new PluginConfigKey("max-bans.chat-spam.unconfirmed", -1L);
	
	public static final PluginConfigKey MAX_BANS_ITEM_DROP_ENABLED				= new PluginConfigKey("max-bans.item-drop.enabled", true);
	public static final PluginConfigKey MAX_BANS_ITEM_DROP_TOTAL				= new PluginConfigKey("max-bans.item-drop.total", -1L);
	public static final PluginConfigKey MAX_BANS_ITEM_DROP_CONFIRMED			= new PluginConfigKey("max-bans.item-drop.confirmed", 10L);
	public static final PluginConfigKey MAX_BANS_ITEM_DROP_UNCONFIRMED			= new PluginConfigKey("max-bans.item-drop.unconfirmed", -1L);
	
	private static HashMap<String, PluginConfigKey> keyLookupTable;
	
	private static HashMap<BanReason, String> banReasonKeys;
	private static HashMap<BanSeverity, String> banSeverityKeys;
	
	static{
		keyLookupTable = new HashMap<String, PluginConfigKey>();
		
		banReasonKeys = new HashMap<BanReason, String>();
		banSeverityKeys = new HashMap<BanSeverity, String>();
		
		for (Field field : Config.class.getDeclaredFields()){
			if (field.getType().equals(PluginConfigKey.class)){
				try{
					PluginConfigKey key = (PluginConfigKey) field.get(null);
					
					keyLookupTable.put(key.getKey(), key);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		
		banReasonKeys.put(BanReason.THEFT, "theft");
		banReasonKeys.put(BanReason.GRIEF, "grief");
		banReasonKeys.put(BanReason.ABUSE, "abuse");
		banReasonKeys.put(BanReason.ADVERTISING, "advertising");
		banReasonKeys.put(BanReason.XRAY, "x-ray");
		banReasonKeys.put(BanReason.FLY, "fly");
		banReasonKeys.put(BanReason.SPEED, "movement-speed");
		banReasonKeys.put(BanReason.BLOCK_REACH, "block-reach");
		banReasonKeys.put(BanReason.NOFALL, "nofall");
		banReasonKeys.put(BanReason.NOSWING, "noswing");
		banReasonKeys.put(BanReason.PVP_CHEAT, "pvp-cheats");
		banReasonKeys.put(BanReason.SPAM, "chat-spam");
		banReasonKeys.put(BanReason.ITEM_DROP, "item-drop");
		
		banSeverityKeys.put(BanSeverity.TOTAL, "total");
		banSeverityKeys.put(BanSeverity.CONFIRMED, "confirmed");
		banSeverityKeys.put(BanSeverity.UNCONFIRMED, "unconfirmed");
		banSeverityKeys.put(BanSeverity.LOW, "low");
		banSeverityKeys.put(BanSeverity.MEDIUM, "medium");
		banSeverityKeys.put(BanSeverity.HIGH, "high");
	}
	
	public static PluginConfigKey getReasonEnabled(BanReason reason){
		return keyLookupTable.get("max-bans." + banReasonKeys.get(reason) + ".enabled");
	}
	
	public static PluginConfigKey getReasonLimit(BanReason reason, BanSeverity severity){
		return keyLookupTable.get("max-bans." + banReasonKeys.get(reason) + "." + banSeverityKeys.get(severity));
	}
	
	public static PluginConfigKey getTotalLimit(BanSeverity severity){
		return keyLookupTable.get("max-bans.total." + banSeverityKeys.get(severity));
	}
	
}
