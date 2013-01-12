package com.minebans.minebans.evidence;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.bans.BanReason;

public class BlockReachEvidenceCollector extends EvidenceCollector {
	
	private MineBans plugin;
	
	public BlockReachEvidenceCollector(MineBans plugin){
		this.plugin = plugin;
	}
	
	@Override
	public Long collect(String playerName){
		return plugin.exploitPlugin.getMaxViolationLevel(playerName, BanReason.BLOCK_REACH);
	}
	
}
