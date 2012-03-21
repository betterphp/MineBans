package com.minebans.evidence;

import java.util.HashMap;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;
import com.minebans.bans.BanReason;

public class EvidenceManager {
	
	private HashMap<BanReason, EvidenceCollector> collectorMap;
	
	public EvidenceManager(MineBans plugin){
		this.collectorMap = new HashMap<BanReason, EvidenceCollector>();
		
		for (BanReason reason : BanReason.getAll()){
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(reason))){
				this.collectorMap.put(reason, reason.getEvidenceCollector());
			}
		}
	}
	
	public Object collectFor(BanReason reason, String playerName){
		if (this.collectorMap.containsKey(reason) == false){
			return null;
		}
		
		return this.collectorMap.get(reason).collect(playerName);
	}
	
}
