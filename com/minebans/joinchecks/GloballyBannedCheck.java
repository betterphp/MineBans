package com.minebans.joinchecks;

import com.minebans.MineBans;

public class GloballyBannedCheck extends LocalJoinCheck {
	
	private MineBans plugin;
	
	public GloballyBannedCheck(MineBans plugin){
		this.plugin = plugin;
	}
	
	public boolean shouldPreventConnection(String playerName, String playerAddress){
		return plugin.banManager.isGloballyBanned(playerName);
	}
	
}
