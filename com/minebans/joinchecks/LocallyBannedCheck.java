package com.minebans.joinchecks;

import com.minebans.MineBans;

public class LocallyBannedCheck extends LocalJoinCheck {
	
	private MineBans plugin;
	
	public LocallyBannedCheck(MineBans plugin){
		this.plugin = plugin;
	}
	
	public boolean shouldPreventConnection(String playerName, String playerAddress){
		return plugin.banManager.isLocallyBanned(playerName);
	}
	
}
