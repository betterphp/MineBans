package com.minebans;

import java.net.SocketTimeoutException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;

import com.minebans.api.ConnectionAllowedReason;
import com.minebans.api.ConnectionDeniedReason;
import com.minebans.api.PlayerJoinData;
import com.minebans.events.PlayerConnectionAllowedEvent;
import com.minebans.events.PlayerConnectionDeniedEvent;
import com.minebans.events.PlayerLoginDataEvent;

import uk.co.jacekk.bukkit.baseplugin.BaseListener;

public class PlayerLoginListenerASync extends BaseListener<MineBans> {
	
	public PlayerLoginListenerASync(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event){
		if (event.getResult() != Result.ALLOWED) return;
		
		final String playerAddress = event.getAddress().getHostAddress();
		final String playerName = event.getName();
		
		if (plugin.banManager.isExempt(playerName)){
			plugin.log.info(playerName + " (" + playerAddress + ") was found on the local ban exempt list, no further checks will be made.");
			plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName, ConnectionAllowedReason.PLAYER_EXEMPT));
			return;
		}
		
		try{
			PlayerJoinData joinData = plugin.api.getPlayerJoinInfo(playerName, "CONSOLE", 8000);
			
			PlayerLoginDataEvent loginDataEvent = new PlayerLoginDataEvent(playerName, playerAddress, joinData);
			
			plugin.pluginManager.callEvent(loginDataEvent);
			
			if (loginDataEvent.isConnectionPrevented()){
				event.disallow(Result.KICK_OTHER, loginDataEvent.getKickMessage());
				plugin.log.info(playerName + " (" + playerAddress + ") " + loginDataEvent.getLogMessage());
				plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, loginDataEvent.getReason()));
				return;
			}
		}catch (SocketTimeoutException ste){
			plugin.log.warn("The API failed to respond even with a longer timeout, it might be down for some reason.");
			
			if (plugin.banManager.isLocallyBanned(playerName)){
				event.disallow(Result.KICK_BANNED, ConnectionDeniedReason.LOCALLY_BANNED.getKickMessage());
				plugin.log.info(playerName + " (" + playerAddress + ") " + ConnectionDeniedReason.LOCALLY_BANNED.getLogMessage());
				plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, ConnectionDeniedReason.LOCALLY_BANNED));
				return;
			}else if (plugin.banManager.isGloballyBanned(playerName)){
				event.disallow(Result.KICK_BANNED, ConnectionDeniedReason.GLOBALLY_BANNED.getKickMessage());
				plugin.log.info(playerName + " (" + playerAddress + ") " + ConnectionDeniedReason.GLOBALLY_BANNED.getLogMessage());
				plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, ConnectionDeniedReason.GLOBALLY_BANNED));
				return;
			}else if (plugin.banManager.isTempBanned(playerName)){
				event.disallow(Result.KICK_BANNED, ConnectionDeniedReason.TEMP_BANNED.getKickMessage());
				plugin.log.info(playerName + " (" + playerAddress + ") " + ConnectionDeniedReason.TEMP_BANNED.getLogMessage());
				plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, ConnectionDeniedReason.TEMP_BANNED));
				return;
			}
		}
		
		plugin.seenPlayers.add(playerName.toLowerCase());
		
		plugin.log.info(playerName + " (" + playerAddress + ") was allowed to join the server.");
		plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName, ConnectionAllowedReason.PASSED_CHECKS));
	}
	
}