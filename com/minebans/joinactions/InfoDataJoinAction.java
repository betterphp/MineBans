package com.minebans.joinactions;

import com.minebans.api.PlayerInfoData;

public abstract class InfoDataJoinAction {
	
	public abstract void onPlayerInfoData(String playerName, String playerAddress, PlayerInfoData infoData);
	
}
