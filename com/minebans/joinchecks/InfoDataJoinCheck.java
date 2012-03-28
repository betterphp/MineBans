package com.minebans.joinchecks;

import com.minebans.api.PlayerInfoData;

public abstract class InfoDataJoinCheck {
	
	public abstract boolean shouldPreventConnection(String playerName, String playerAddress, PlayerInfoData infoData);
	
}
