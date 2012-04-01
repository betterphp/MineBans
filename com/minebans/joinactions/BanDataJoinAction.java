package com.minebans.joinactions;

import com.minebans.api.PlayerBanData;

public abstract class BanDataJoinAction {
	
	public abstract void onPlayerBanData(String playerName, String playerAddress, PlayerBanData banData);
	
}
