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
	
	THEFT(0,		"Stealing from another player",			Arrays.asList("thief", "theft", "stealing", "steal"),		Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED, BanSeverity.LOW, BanSeverity.MEDIUM, BanSeverity.HIGH), 	TheftEvidenceCollector.class),
	GRIEF(1,		"Destroying another players building",	Arrays.asList("griefing", "grief"),							Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED, BanSeverity.LOW, BanSeverity.MEDIUM, BanSeverity.HIGH),	GriefEvidenceCollector.class),
	ABUSE(2,		"Being excessively abusive",			Arrays.asList("abuse", "abusive", "swearing"),				Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															AbuseEvidenceCollector.class),
	ADVERTISING(3,	"Advertising a product or service",		Arrays.asList("advertising", "ads", "advert"),				Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															AdvertisingEvidenceCollector.class),
	XRAY(4,			"Using an x-ray mod",					Arrays.asList("x-ray", "xray", "x-raying", "xraying"),		Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED, BanSeverity.LOW, BanSeverity.MEDIUM, BanSeverity.HIGH),	XrayEvidenceCollector.class),
	FLY(5,			"Using a fly mod",						Arrays.asList("fly", "flymod", "flying"),					Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															FlyingEvidenceCollector.class),
	SPEED(6,		"Using a movement speed",				Arrays.asList("speed", "sprint", "fakesneak"),				Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															SpeedEvidenceCollector.class),
	BLOCK_REACH(7,	"Using a block reach",					Arrays.asList("reach", "block-reach"),						Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															BlockReachEvidenceCollector.class),
	NOFALL(8,		"Avoiding fall damage",					Arrays.asList("nofall", "no-fall"),							Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															NoFallEvidenceCollector.class),
	NOSWING(9,		"Unfair a no-swing mod",				Arrays.asList("noswing", "no-swing"),						Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															NoSwingEvidenceCollector.class),
	PVP_CHEAT(10,	"Using PVP mods to gain an advantage",	Arrays.asList("pvpcheat", "pvp", "kill"),					Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															PvpCheatEvidenceCollector.class),
	SPAM(11,		"Chat or command spam",					Arrays.asList("spam", "spaming"),							Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															SpamEvidenceCollector.class),
	ITEM_DROP(12,	"Item drop",							Arrays.asList("drop", "dropping", "items"),					Arrays.asList(BanSeverity.TOTAL, BanSeverity.CONFIRMED, BanSeverity.UNCONFIRMED),															DropEvidenceCollector.class);
	
	private int id;
	private String description;
	private List<String> keywords;
	private List<BanSeverity> severities;
	private Class<?> evidenceCollector;
	
	private MineBans plugin;
	
	private static BanReason[] idLookupTable;
	private static HashMap<String, BanReason> keywordLookupTable;
	
	private BanReason(int id, String description, List<String> keywords, List<BanSeverity> severities, Class<?> evidenceCollector){
		this.id = id;
		this.description = description;
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
