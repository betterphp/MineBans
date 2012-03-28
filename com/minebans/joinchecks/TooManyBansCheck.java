package com.minebans.joinchecks;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;
import com.minebans.api.PlayerBanData;
import com.minebans.bans.BanReason;
import com.minebans.bans.BanSeverity;

public class TooManyBansCheck extends BanDataJoinCheck {
	
	private MineBans plugin;
	
	public TooManyBansCheck(MineBans plugin){
		this.plugin = plugin;
	}
	
	public boolean shouldPreventConnection(String playerName, String playerAddress, PlayerBanData banData){
		Long limit;
		
		for (BanReason banReason : banData.getBanReasons()){
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(banReason))){
				for (BanSeverity severity : banReason.getSeverties()){
					limit = plugin.config.getLong(MineBansConfig.getReasonLimit(banReason, severity));
					
					if (limit != -1L && banData.get(banReason, severity) > limit){
						return true;
					}
				}
			}
		}
		
		for (BanSeverity severity : BanSeverity.getAll()){
			limit = plugin.config.getLong(MineBansConfig.getTotalLimit(severity));
			
			if (limit != -1L && banData.getTotal(severity) > limit){
				return true;
			}
		}
		
		return false;
	}
	
}
