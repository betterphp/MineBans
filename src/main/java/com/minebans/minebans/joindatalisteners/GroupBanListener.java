package com.minebans.minebans.joindatalisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import uk.co.jacekk.bukkit.baseplugin.event.BaseListener;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.ConnectionDeniedReason;
import com.minebans.minebans.events.PlayerLoginDataEvent;

public class GroupBanListener extends BaseListener<MineBans> {
	
	public GroupBanListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLoginData(PlayerLoginDataEvent event){
		if (event.getJoinData().getBansData().getTotalGroupBans() > 0){
			event.setPreventConnection(true);
			event.setReason(ConnectionDeniedReason.GROUP_BAN);
			event.setKickMessage(ConnectionDeniedReason.GROUP_BAN.getKickMessage());
			event.setLogMessage(ConnectionDeniedReason.GROUP_BAN.getLogMessage());
		}
	}
	
}
