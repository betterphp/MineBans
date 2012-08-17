package com.minebans;

import java.util.HashMap;

import com.minebans.bans.BanReason;
import com.minebans.evidence.EvidenceCollector;

public class EvidenceManager {
	
	private HashMap<BanReason, EvidenceCollector> collectorMap;
	
	public EvidenceManager(MineBans plugin){
		this.collectorMap = new HashMap<BanReason, EvidenceCollector>();
		
		for (BanReason reason : BanReason.getAll()){
			if (plugin.config.getBoolean(Config.getReasonEnabled(reason))){
				this.collectorMap.put(reason, reason.getEvidenceCollector());
			}
		}
	}
	
	public Object collectFor(BanReason reason, String playerName){
		if (!this.collectorMap.containsKey(reason)){
			return null;
		}
		
		return this.collectorMap.get(reason).collect(playerName);
	}
	
}
