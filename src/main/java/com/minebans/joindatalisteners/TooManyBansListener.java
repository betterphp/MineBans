package com.minebans.joindatalisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import uk.co.jacekk.bukkit.baseplugin.BaseListener;

import com.minebans.MineBans;
import com.minebans.Config;
import com.minebans.api.ConnectionDeniedReason;
import com.minebans.api.PlayerBanData;
import com.minebans.bans.BanReason;
import com.minebans.bans.BanSeverity;
import com.minebans.events.PlayerLoginDataEvent;

public class TooManyBansListener extends BaseListener<MineBans> {
	
	public TooManyBansListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLoginData(PlayerLoginDataEvent event){
		Long limit;
		
		PlayerBanData banData = event.getJoinData().getBanData();
		
		for (BanReason banReason : banData.getBanReasons()){
			if (plugin.config.getBoolean(Config.getReasonEnabled(banReason))){
				for (BanSeverity severity : banReason.getSeverties()){
					limit = plugin.config.getLong(Config.getReasonLimit(banReason, severity));
					
					if (limit != -1L && banData.get(banReason, severity) > limit){
						event.setPreventConnection(true);
						event.setReason(ConnectionDeniedReason.GLOBALLY_BANNED);
						event.setKickMessage(ConnectionDeniedReason.GLOBALLY_BANNED.getKickMessage());
						event.setLogMessage(ConnectionDeniedReason.GLOBALLY_BANNED.getLogMessage());
					}
				}
			}
		}
		
		for (BanSeverity severity : BanSeverity.getAll()){
			limit = plugin.config.getLong(Config.getTotalLimit(severity));
			
			if (limit != -1L && banData.getTotal(severity) > limit){
				event.setPreventConnection(true);
				event.setReason(ConnectionDeniedReason.GLOBALLY_BANNED);
				event.setKickMessage(ConnectionDeniedReason.GLOBALLY_BANNED.getKickMessage());
				event.setLogMessage(ConnectionDeniedReason.GLOBALLY_BANNED.getLogMessage());
			}
		}
	}
	
}
