package com.minebans.evidence;

import com.minebans.MineBans;

public class SpamEvidenceCollector extends EvidenceCollector {
	
	private MineBans plugin;
	
	public SpamEvidenceCollector(MineBans plugin){
		this.plugin = plugin;
	}
	
	public Integer collect(String playerName){
		return plugin.antiSpamPlugin.getMaxViolationLevel(playerName);
	}
	
}
