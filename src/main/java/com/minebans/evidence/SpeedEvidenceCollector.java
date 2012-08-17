package com.minebans.evidence;

import com.minebans.MineBans;
import com.minebans.bans.BanReason;

public class SpeedEvidenceCollector extends EvidenceCollector {
	
	private MineBans plugin;
	
	public SpeedEvidenceCollector(MineBans plugin){
		this.plugin = plugin;
	}
	
	public Long collect(String playerName){
		return plugin.exploitPlugin.getMaxViolationLevel(playerName, BanReason.SPEED);
	}
	
}
