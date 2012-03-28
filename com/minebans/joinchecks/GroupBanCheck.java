package com.minebans.joinchecks;

import com.minebans.api.PlayerBanData;

public class GroupBanCheck extends BanDataJoinCheck {
	
	public boolean shouldPreventConnection(String playerName, String playerAddress, PlayerBanData banData){
		return (banData.getGroup() > 0);
	}
	
}
