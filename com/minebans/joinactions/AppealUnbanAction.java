package com.minebans.joinactions;

import com.minebans.MineBans;
import com.minebans.api.PlayerInfoData;

public class AppealUnbanAction extends InfoDataJoinAction {
	
	private MineBans plugin;
	
	public AppealUnbanAction(MineBans plugin){
		this.plugin = plugin;
	}
	
	public void onPlayerInfoData(String playerName, String playerAddress, PlayerInfoData infoData){
		if (infoData.shouldUnban()){
			plugin.banManager.unGlobalBan(playerName, "CONSOLE");
		}
	}
	
}
