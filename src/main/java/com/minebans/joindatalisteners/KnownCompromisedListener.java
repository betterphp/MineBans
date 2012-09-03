package com.minebans.joindatalisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import uk.co.jacekk.bukkit.baseplugin.v1.event.BaseListener;

import com.minebans.MineBans;
import com.minebans.api.ConnectionDeniedReason;
import com.minebans.events.PlayerLoginDataEvent;

public class KnownCompromisedListener extends BaseListener<MineBans> {
	
	public KnownCompromisedListener(MineBans plugin){
		super(plugin);
	}
	
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
