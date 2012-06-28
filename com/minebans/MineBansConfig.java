package com.minebans;

import java.util.HashMap;
import java.util.LinkedHashMap;

import uk.co.jacekk.bukkit.baseplugin.config.PluginConfigKey;

import com.minebans.bans.BanReason;
import com.minebans.bans.BanSeverity;

public enum MineBansConfig implements PluginConfigKey {
	
	API_KEY(							"api-key", 								"change this to the one from your control panel"),
	
	USE_COMPACT_JOIN_INFO(				"use-compact-join-info",				false),
	USE_GROUP_BANS(						"use-group-bans",						true),
	
	BLOCK_PROXIES(						"block-public-proxies",					true),
	BLOCK_COMPROMISED_ACCOUNTS(			"block-known-compromised-accounts",		true),
	
	MAX_BANS_TOTAL_TOTAL(				"max-bans.total.total",					15L),
	MAX_BANS_TOTAL_CONFIRMED(			"max-bans.total.confirmed",				-1L),
	MAX_BANS_TOTAL_UNCONFIRMED(			"max-bans.total.unconfirmed",			-1L),
	MAX_BANS_TOTAL_LOW(					"max-bans.total.low",					-1L),
	MAX_BANS_TOTAL_MEDIUM(				"max-bans.total.medium",				5L),
	MAX_BANS_TOTAL_HIGH(				"max-bans.total.high",					5L),
	
	MAX_BANS_THEFT_ENABLED(				"max-bans.theft.enabled",				true),
	MAX_BANS_THEFT_TOTAL(				"max-bans.theft.total",					15L),
	MAX_BANS_THEFT_CONFIRMED(			"max-bans.theft.confirmed",				-1L),
	MAX_BANS_THEFT_UNCONFIRMED(			"max-bans.theft.unconfirmed",			-1L),
	MAX_BANS_THEFT_LOW(					"max-bans.theft.low",					-1L),
	MAX_BANS_THEFT_MEDIUM(				"max-bans.theft.medium",				5L),
	MAX_BANS_THEFT_HIGH(				"max-bans.theft.high",					5L),
	
	MAX_BANS_GRIEF_ENABLED(				"max-bans.grief.enabled",				true),
	MAX_BANS_GRIEF_TOTAL(				"max-bans.grief.total",					15L),
	MAX_BANS_GRIEF_CONFIRMED(			"max-bans.grief.confirmed",				-1L),
	MAX_BANS_GRIEF_UNCONFIRMED(			"max-bans.grief.unconfirmed",			-1L),
	MAX_BANS_GRIEF_LOW(					"max-bans.grief.low",					-1L),
	MAX_BANS_GRIEF_MEDIUM(				"max-bans.grief.medium",				5L),
	MAX_BANS_GRIEF_HIGH(				"max-bans.grief.high",					5L),
	
	MAX_BANS_XRAY_ENABLED(				"max-bans.x-ray.enabled",				true),
	MAX_BANS_XRAY_TOTAL(				"max-bans.x-ray.total",					15L),
	MAX_BANS_XRAY_CONFIRMED(			"max-bans.x-ray.confirmed",				-1L),
	MAX_BANS_XRAY_UNCONFIRMED(			"max-bans.x-ray.unconfirmed",			-1L),
	MAX_BANS_XRAY_LOW(					"max-bans.x-ray.low",					-1L),
	MAX_BANS_XRAY_MEDIUM(				"max-bans.x-ray.medium",				5L),
	MAX_BANS_XRAY_HIGH(					"max-bans.x-ray.high",					5L),
	
	MAX_BANS_ABUSE_ENABLED(				"max-bans.abuse.enabled",				false),
	MAX_BANS_ABUSE_TOTAL(				"max-bans.abuse.total",					-1L),
	MAX_BANS_ABUSE_CONFIRMED(			"max-bans.abuse.confirmed",				10L),
	MAX_BANS_ABUSE_UNCONFIRMED(			"max-bans.abuse.unconfirmed",			-1L),
	
	MAX_BANS_ADVERTISING_ENABLED(		"max-bans.advertising.enabled",			false),
	MAX_BANS_ADVERTISING_TOTAL(			"max-bans.advertising.total",			-1L),
	MAX_BANS_ADVERTISING_CONFIRMED(		"max-bans.advertising.confirmed",		10L),
	MAX_BANS_ADVERTISING_UNCONFIRMED(	"max-bans.advertising.unconfirmed",		-1L),
	
	MAX_BANS_FLY_ENABLED(				"max-bans.fly.enabled",					true),
	MAX_BANS_FLY_TOTAL(					"max-bans.fly.total",					-1L),
	MAX_BANS_FLY_CONFIRMED(				"max-bans.fly.confirmed",				10L),
	MAX_BANS_FLY_UNCONFIRMED(			"max-bans.fly.unconfirmed",				-1L),
	
	MAX_BANS_MOVE_SPEED_ENABLED(		"max-bans.movement-speed.enabled",		true),
	MAX_BANS_MOVE_SPEED_TOTAL(			"max-bans.movement-speed.total",		-1L),
	MAX_BANS_MOVE_SPEED_CONFIRMED(		"max-bans.movement-speed.confirmed",	10L),
	MAX_BANS_MOVE_SPEED_UNCONFIRMED(	"max-bans.movement-speed.unconfirmed",	-1L),
	
	MAX_BANS_BLOCK_REACH_ENABLED(		"max-bans.block-reach.enabled",			true),
	MAX_BANS_BLOCK_REACH_TOTAL(			"max-bans.block-reach.total",			-1L),
	MAX_BANS_BLOCK_REACH_CONFIRMED(		"max-bans.block-reach.confirmed",		10L),
	MAX_BANS_BLOCK_REACH_UNCONFIRMED(	"max-bans.block-reach.unconfirmed",		-1L),
	
	MAX_BANS_NOFALL_ENABLED(			"max-bans.nofall.enabled",				true),
	MAX_BANS_NOFALL_TOTAL(				"max-bans.nofall.total",				-1L),
	MAX_BANS_NOFALL_CONFIRMED(			"max-bans.nofall.confirmed",			10L),
	MAX_BANS_NOFALL_UNCONFIRMED(		"max-bans.nofall.unconfirmed",			-1L),
	
	MAX_BANS_NOSWING_ENABLED(			"max-bans.noswing.enabled",				false),
	MAX_BANS_NOSWING_TOTAL(				"max-bans.noswing.total",				-1L),
	MAX_BANS_NOSWING_CONFIRMED(			"max-bans.noswing.confirmed",			10L),
	MAX_BANS_NOSWING_UNCONFIRMED(		"max-bans.noswing.unconfirmed",			-1L),
	
	MAX_BANS_PVP_CHEATS_ENABLED(		"max-bans.pvp-cheats.enabled",			false),
	MAX_BANS_PVP_CHEATS_TOTAL(			"max-bans.pvp-cheats.total",			-1L),
	MAX_BANS_PVP_CHEATS_CONFIRMED(		"max-bans.pvp-cheats.confirmed",		10L),
	MAX_BANS_PVP_CHEATS_UNCONFIRMED(	"max-bans.pvp-cheats.unconfirmed",		-1L),
	
	MAX_BANS_CHAT_SPAM_ENABLED(			"max-bans.chat-spam.enabled",			true),
	MAX_BANS_CHAT_SPAM_TOTAL(			"max-bans.chat-spam.total",				-1L),
	MAX_BANS_CHAT_SPAM_CONFIRMED(		"max-bans.chat-spam.confirmed",			10L),
	MAX_BANS_CHAT_SPAM_UNCONFIRMED(		"max-bans.chat-spam.unconfirmed",		-1L),
	
	MAX_BANS_ITEM_DROP_ENABLED(			"max-bans.item-drop.enabled",			true),
	MAX_BANS_ITEM_DROP_TOTAL(			"max-bans.item-drop.total",				-1L),
	MAX_BANS_ITEM_DROP_CONFIRMED(		"max-bans.item-drop.confirmed",			10L),
	MAX_BANS_ITEM_DROP_UNCONFIRMED(		"max-bans.item-drop.unconfirmed",		-1L);
	
	private String key;
	private Object defaultValue;
	
	private static LinkedHashMap<String, Object> defaultValues;
	private static HashMap<String, MineBansConfig> keyLookupTable;
	
	private static HashMap<BanReason, String> banReasonKeys;
	private static HashMap<BanSeverity, String> banSeverityKeys;
	
	private MineBansConfig(String key, Object defaultValue){
		this.key = key;
		this.defaultValue = defaultValue;
	}
	
	static{
		defaultValues = new LinkedHashMap<String, Object>();
		keyLookupTable = new HashMap<String, MineBansConfig>();
		
		banReasonKeys = new HashMap<BanReason, String>();
		banSeverityKeys = new HashMap<BanSeverity, String>();
		
		for (MineBansConfig entry : MineBansConfig.values()){
			defaultValues.put(entry.getKey(), entry.getDefault());
			keyLookupTable.put(entry.getKey(), entry);
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
	
	public static LinkedHashMap<String, Object> getAll(){
		return defaultValues;
	}
	
	public static MineBansConfig getReasonEnabled(BanReason reason){
		return keyLookupTable.get("max-bans." + banReasonKeys.get(reason) + ".enabled");
	}
	
	public static MineBansConfig getReasonLimit(BanReason reason, BanSeverity severity){
		return keyLookupTable.get("max-bans." + banReasonKeys.get(reason) + "." + banSeverityKeys.get(severity));
	}
	
	public static MineBansConfig getTotalLimit(BanSeverity severity){
		return keyLookupTable.get("max-bans.total." + banSeverityKeys.get(severity));
	}
	
	public String getKey(){
		return this.key;
	}
	
	public Object getDefault(){
		return this.defaultValue;
	}
	
}
