package com.minebans.minebans.bans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.evidence.AbuseEvidenceCollector;
import com.minebans.minebans.evidence.AdvertisingEvidenceCollector;
import com.minebans.minebans.evidence.BlockReachEvidenceCollector;
import com.minebans.minebans.evidence.DropEvidenceCollector;
import com.minebans.minebans.evidence.EvidenceCollector;
import com.minebans.minebans.evidence.FlyingEvidenceCollector;
import com.minebans.minebans.evidence.GriefEvidenceCollector;
import com.minebans.minebans.evidence.NoFallEvidenceCollector;
import com.minebans.minebans.evidence.NoSwingEvidenceCollector;
import com.minebans.minebans.evidence.PvpCheatEvidenceCollector;
import com.minebans.minebans.evidence.SpamEvidenceCollector;
import com.minebans.minebans.evidence.SpeedEvidenceCollector;
import com.minebans.minebans.evidence.TheftEvidenceCollector;
import com.minebans.minebans.evidence.XrayEvidenceCollector;

public enum BanReason {
	
	THEFT(0,		"stealing",				"stealing from another player",			Arrays.asList("thief", "theft", "stealing", "steal"),		Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED, BanSeverity.LOW, BanSeverity.MEDIUM, BanSeverity.HIGH), 	TheftEvidenceCollector.class),
	GRIEF(1,		"griefing",				"destroying another players building",	Arrays.asList("griefing", "grief"),							Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED, BanSeverity.LOW, BanSeverity.MEDIUM, BanSeverity.HIGH),	GriefEvidenceCollector.class),
	ABUSE(2,		"being abusive",		"being excessively abusive",			Arrays.asList("abuse", "abusive", "swearing"),				Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															AbuseEvidenceCollector.class),
	ADVERTISING(3,	"advertising",			"advertising a product or service",		Arrays.asList("advertising", "ads", "advert"),				Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															AdvertisingEvidenceCollector.class),
	XRAY(4,			"using x-ray",			"using an x-ray mod",					Arrays.asList("x-ray", "xray", "x-raying", "xraying"),		Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED, BanSeverity.LOW, BanSeverity.MEDIUM, BanSeverity.HIGH),	XrayEvidenceCollector.class),
	FLY(5,			"flying",				"using a fly mod",						Arrays.asList("fly", "flymod", "flying"),					Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															FlyingEvidenceCollector.class),
	SPEED(6,		"using a speed mod",	"using a movement speed mod",			Arrays.asList("speed", "sprint", "fakesneak"),				Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															SpeedEvidenceCollector.class),
	BLOCK_REACH(7,	"reach mod",			"using a block reach",					Arrays.asList("reach", "block-reach"),						Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															BlockReachEvidenceCollector.class),
	NOFALL(8,		"no-fall",				"avoiding fall damage",					Arrays.asList("nofall", "no-fall"),							Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															NoFallEvidenceCollector.class),
	NOSWING(9,		"no-swing",				"unfair a no-swing mod",				Arrays.asList("noswing", "no-swing"),						Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															NoSwingEvidenceCollector.class),
	PVP_CHEAT(10,	"PVP cheats",			"using PVP mods to gain an advantage",	Arrays.asList("pvpcheat", "pvp", "kill"),					Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															PvpCheatEvidenceCollector.class),
	SPAM(11,		"spamming",				"chat or command spam",					Arrays.asList("spam", "spaming"),							Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															SpamEvidenceCollector.class),
	ITEM_DROP(12,	"item drop",			"dropping a large number of items",		Arrays.asList("drop", "dropping", "items"),					Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															DropEvidenceCollector.class);
	
	private int id;
	private String shortDescription;
	private String description;
	private List<String> keywords;
	private List<BanSeverity> severities;
	private Class<?> evidenceCollector;
	
	private MineBans plugin;
	
	private static BanReason[] idLookupTable;
	private static HashMap<String, BanReason> keywordLookupTable;
	
	private BanReason(int id, String shortDescription, String description, List<String> keywords, List<BanSeverity> severities, Class<?> evidenceCollector){
		this.id = id;
		this.description = description;
		this.shortDescription = shortDescription;
		this.keywords = keywords;
		this.severities = severities;
		this.evidenceCollector = evidenceCollector;
		
		if (Bukkit.getServer() != null){
			this.plugin = (MineBans) Bukkit.getServer().getPluginManager().getPlugin("MineBans");
		}
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
	
	public String getShortDescription(){
		return this.shortDescription;
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
