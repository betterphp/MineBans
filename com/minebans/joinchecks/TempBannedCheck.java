package com.minebans.joinchecks;

import com.minebans.MineBans;

public class TempBannedCheck extends LocalJoinCheck {
	
	private MineBans plugin;
	
	public TempBannedCheck(MineBans plugin){
		this.plugin = plugin;
	}
	
	public boolean shouldPreventConnection(String playerName, String playerAddress){
		return plugin.banManager.isTempBanned(playerName);
	}
	
}
