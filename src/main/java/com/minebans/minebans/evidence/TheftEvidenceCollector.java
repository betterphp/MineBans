package com.minebans.minebans.evidence;

import java.util.HashMap;

import com.minebans.minebans.MineBans;

public class TheftEvidenceCollector extends EvidenceCollector {
	
	private MineBans plugin;
	
	public TheftEvidenceCollector(MineBans plugin){
		this.plugin = plugin;
	}
	
	@Override
	public HashMap<Integer, Integer> collect(String playerName){
		return plugin.loggingPlugin.getChestAccess(playerName);
	}
	
}
