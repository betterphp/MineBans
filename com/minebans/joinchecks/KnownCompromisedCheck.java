package com.minebans.joinchecks;

import com.minebans.api.PlayerInfoData;

public class KnownCompromisedCheck extends InfoDataJoinCheck {
	
	public boolean shouldPreventConnection(String playerName, String playerAddress, PlayerInfoData infoData){
		return infoData.isKnownCompromised();
	}
	
}
