package com.minebans.joindatalisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.minebans.api.ConnectionDeniedReason;
import com.minebans.events.PlayerLoginDataEvent;

public class KnownCompromisedListener implements Listener {
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLoginData(PlayerLoginDataEvent event){
		if (event.getJoinData().getInfoData().isKnownCompromised()){
			event.setPreventConnection(true);
			event.setReason(ConnectionDeniedReason.KNOWN_COMPROMISED);
			event.setKickMessage(ConnectionDeniedReason.KNOWN_COMPROMISED.getKickMessage());
			event.setLogMessage(ConnectionDeniedReason.KNOWN_COMPROMISED.getLogMessage());
		}
	}
	
}
