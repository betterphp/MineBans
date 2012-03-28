package com.minebans.joinchecks;

import com.minebans.api.PlayerBanData;

public abstract class BanDataJoinCheck {
	
	public abstract boolean shouldPreventConnection(String playerName, String playerAddress, PlayerBanData banData);
	
}
