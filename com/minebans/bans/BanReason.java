package com.minebans.bans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;
import com.minebans.evidence.AdvertisingEvidenceCollector;
import com.minebans.evidence.BlockReachEvidenceCollector;
import com.minebans.evidence.DropEvidenceCollector;
import com.minebans.evidence.EvidenceCollector;
import com.minebans.evidence.FlyingEvidenceCollector;
import com.minebans.evidence.GriefEvidenceCollector;
import com.minebans.evidence.NoFallEvidenceCollector;
import com.minebans.evidence.NoSwingEvidenceCollector;
import com.minebans.evidence.PvpCheatEvidenceCollector;
import com.minebans.evidence.SpamEvidenceCollector;
import com.minebans.evidence.SpeedEvidenceCollector;
import com.minebans.evidence.TheftEvidenceCollector;
import com.minebans.evidence.AbuseEvidenceCollector;
import com.minebans.evidence.XrayEvidenceCollector;

public enum BanReason {
	
	THEFT(0,		"Stealing from another player",			MineBansConfig.MAX_BANS_THEFT_ENABLED,			Arrays.asList("thief", "theft", "stealing", "steal"),		Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED, BanSeverity.LOW, BanSeverity.MEDIUM, BanSeverity.HIGH), 	TheftEvidenceCollector.class),
	GRIEF(1,		"Destroying another players building",	MineBansConfig.MAX_BANS_GRIEF_ENABLED,			Arrays.asList("grief", "griefing"),							Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED, BanSeverity.LOW, BanSeverity.MEDIUM, BanSeverity.HIGH),	GriefEvidenceCollector.class),
	ABUSE(2,		"Being excessively abusive",			MineBansConfig.MAX_BANS_ABUSE_ENABLED,			Arrays.asList("abuse", "abusive", "swearing"),				Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															AbuseEvidenceCollector.class),
	ADVERTISING(3,	"Advertising a product or service",		MineBansConfig.MAX_BANS_ADVERTISING_ENABLED,	Arrays.asList("advertising", "ads", "advert"),				Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															AdvertisingEvidenceCollector.class),
	XRAY(4,			"Unfair mod, x-ray",					MineBansConfig.MAX_BANS_XRAY_ENABLED,			Arrays.asList("x-ray", "xray", "x-raying", "xraying"),		Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED, BanSeverity.LOW, BanSeverity.MEDIUM, BanSeverity.HIGH),	XrayEvidenceCollector.class),
	FLY(5,			"Unfair mod, fly",						MineBansConfig.MAX_BANS_FLY_ENABLED,			Arrays.asList("fly", "flymod", "flying"),					Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															FlyingEvidenceCollector.class),
	SPEED(6,		"Unfair mod, movement speed",			MineBansConfig.MAX_BANS_MOVE_SPEED_ENABLED,		Arrays.asList("speed", "sprint", "fakesneak"),				Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															SpeedEvidenceCollector.class),
	BLOCK_REACH(7,	"Unfair mod, block reach",				MineBansConfig.MAX_BANS_BLOCK_REACH_ENABLED,	Arrays.asList("reach", "block-reach"),						Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															BlockReachEvidenceCollector.class),
	NOFALL(8,		"Unfair mod, no-fall",					MineBansConfig.MAX_BANS_NOFALL_ENABLED,			Arrays.asList("nofall", "no-fall"),							Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															NoFallEvidenceCollector.class),
	NOSWING(9,		"Unfair mod, no-swing",					MineBansConfig.MAX_BANS_NOSWING_ENABLED,		Arrays.asList("noswing", "no-swing"),						Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															NoSwingEvidenceCollector.class),
	PVP_CHEAT(10,	"Unfair mod, PVP cheats",				MineBansConfig.MAX_BANS_PVP_CHEATS_ENABLED,		Arrays.asList("pvpcheat", "pvp", "kill"),					Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															PvpCheatEvidenceCollector.class),
	SPAM(11,		"Malicious mod, chat or command spam",	MineBansConfig.MAX_BANS_CHAT_SPAM_ENABLED,		Arrays.asList("spam", "spaming"),							Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															SpamEvidenceCollector.class),
	ITEM_DROP(12,	"Malicious mod, item drop",				MineBansConfig.MAX_BANS_ITEM_DROP_ENABLED,		Arrays.asList("drop", "dropping", "items"),					Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															DropEvidenceCollector.class);
	
	private int id;
	private String description;
	private MineBansConfig enabledKey;
	private List<String> keywords;
	private List<BanSeverity> severities;
	private Class<?> evidenceCollector;
	
	private MineBans plugin;
	
	private static BanReason[] idLookupTable;
	private static HashMap<String, BanReason> keywordLookupTable;
	
	BanReason(int id, String description, MineBansConfig enabledKey, List<String> keywords, List<BanSeverity> severities, Class<?> evidenceCollector){
		this.id = id;
		this.description = description;
		this.enabledKey = enabledKey;
		this.keywords = keywords;
		this.severities = severities;
		this.evidenceCollector = evidenceCollector;
		
		this.plugin = (MineBans) Bukkit.getServer().getPluginManager().getPlugin("MineBans");
	}
	
	static {
		idLookupTable = new BanReason[values().length];
		keywordLookupTable = new HashMap<String, BanReason>();
		
		for (BanReason reason : values()){
			idLookupTable[reason.id] = reason;
			
			for (String keyword : reason.keywords){
				keywordLookupTable.put(keyword, reason);
			}
		}
	}
	
	public static BanReason[] getAll(){
		return idLookupTable;
	}
	
	public static BanReason getFromID(int id){
		return idLookupTable[id];
	}
	
	public static BanReason getFromKeyword(String keyword){
		return keywordLookupTable.get(keyword.toLowerCase());
	}
	
	public int getID(){
		return this.id;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public MineBansConfig getEnabledKey(){
		return this.enabledKey;
	}
	
	public List<String> getKeywords(){
		return this.keywords;
	}
	
	public List<BanSeverity> getSeverties(){
		return this.severities;
	}
	
	public EvidenceCollector getEvidenceCollector(){
		Class<?>[] params = new Class[1];
		params[0] = MineBans.class;
		
		try{
			return (EvidenceCollector) this.evidenceCollector.getDeclaredConstructor(params).newInstance(plugin);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
}
