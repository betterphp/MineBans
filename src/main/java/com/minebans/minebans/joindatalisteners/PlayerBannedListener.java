package com.minebans.minebans.joindatalisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import uk.co.jacekk.bukkit.baseplugin.v9_1.event.BaseListener;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.ConnectionDeniedReason;
import com.minebans.minebans.events.PlayerLoginDataEvent;

public class PlayerBannedListener extends BaseListener<MineBans> {
	
	public PlayerBannedListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLoginData(PlayerLoginDataEvent event){
		String playerName = event.getPlayerName();
		
		if (plugin.banManager.isLocallyBanned(playerName)){
			event.setPreventConnection(true);
			event.setReason(ConnectionDeniedReason.LOCALLY_BANNED);
			event.setKickMessage(ConnectionDeniedReason.LOCALLY_BANNED.getKickMessage());
			event.setLogMessage(ConnectionDeniedReason.LOCALLY_BANNED.getLogMessage());
		}else if (plugin.banManager.isGloballyBanned(playerName)){
			if (event.getJoinData().getInfoData().shouldUnban()){
				plugin.banManager.unGlobalBan(playerName, "CONSOLE");
			}else{
				event.setPreventConnection(true);
				event.setReason(ConnectionDeniedReason.GLOBALLY_BANNED);
				event.setKickMessage(ConnectionDeniedReason.GLOBALLY_BANNED.getKickMessage());
				event.setLogMessage(ConnectionDeniedReason.GLOBALLY_BANNED.getLogMessage());
			}
		}else if (plugin.banManager.isTempBanned(playerName)){
			event.setPreventConnection(true);
			event.setReason(ConnectionDeniedReason.TEMP_BANNED);
			event.setKickMessage(ConnectionDeniedReason.TEMP_BANNED.getKickMessage());
			event.setLogMessage(ConnectionDeniedReason.TEMP_BANNED.getLogMessage());
		}
	}
	
}
