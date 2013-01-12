package com.minebans.minebans.evidence;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.bans.BanReason;

public class PvpCheatEvidenceCollector extends EvidenceCollector {
	
	private MineBans plugin;
	
	public PvpCheatEvidenceCollector(MineBans plugin){
		this.plugin = plugin;
	}
	
	@Override
	public Long collect(String playerName){
		return plugin.exploitPlugin.getMaxViolationLevel(playerName, BanReason.PVP_CHEAT);
	}
	
}
