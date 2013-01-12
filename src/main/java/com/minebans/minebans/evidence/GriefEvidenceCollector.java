package com.minebans.minebans.evidence;

import java.util.HashMap;

import com.minebans.minebans.MineBans;

public class GriefEvidenceCollector extends EvidenceCollector {
	
	private MineBans plugin;
	
	public GriefEvidenceCollector(MineBans plugin){
		this.plugin = plugin;
	}
	
	@Override
	public HashMap<String, HashMap<Integer, Integer>> collect(String playerName){
		return plugin.loggingPlugin.getBlockChanges(playerName);
	}
	
}
