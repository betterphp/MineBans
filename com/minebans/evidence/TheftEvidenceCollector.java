package com.minebans.evidence;

import java.util.HashMap;

import com.minebans.MineBans;

public class TheftEvidenceCollector extends EvidenceCollector {
	
	private MineBans plugin;
	
	public TheftEvidenceCollector(MineBans plugin){
		this.plugin = plugin;
	}
	
	public HashMap<Short, Integer> collect(String playerName){
		return plugin.loggingPlugin.getChestAccess(playerName);
	}
	
}
