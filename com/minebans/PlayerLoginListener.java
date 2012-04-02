package com.minebans;

import java.net.SocketTimeoutException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerPreLoginEvent;

import com.minebans.api.APIResponseCallback;
import com.minebans.api.ConnectionAllowedReason;
import com.minebans.api.ConnectionDeniedReason;
import com.minebans.api.PlayerJoinData;
import com.minebans.events.PlayerConnectionAllowedEvent;
import com.minebans.events.PlayerConnectionDeniedEvent;
import com.minebans.events.PlayerLoginDataEvent;

public class PlayerLoginListener implements Listener {
	
	private MineBans plugin;
	
	public PlayerLoginListener(MineBans plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(PlayerPreLoginEvent event){
		if (event.getResult() != Result.ALLOWED) return;
		
		final String playerAddress = event.getAddress().getHostAddress();
		final String playerName = event.getName();
		
		if (plugin.banManager.isExempt(playerName)){
			plugin.log.info(playerName + " (" + playerAddress + ") was found on the local ban exempt list, no further checks will be made.");
			plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName, ConnectionAllowedReason.PLAYER_EXEMPT));
			return;
		}
		
		Boolean apiDelay = false;
		
		try{
			PlayerJoinData joinData = plugin.api.getPlayerJoinInfo(playerName, "CONSOLE");
			
			if (joinData != null){
				PlayerLoginDataEvent loginDataEvent = new PlayerLoginDataEvent(playerName, playerAddress, joinData);
				
				plugin.pluginManager.callEvent(loginDataEvent);
				
				if (loginDataEvent.isConnectionPrevented()){
					event.disallow(Result.KICK_OTHER, loginDataEvent.getKickMessage());
					plugin.log.info(playerName + " (" + playerAddress + ") " + loginDataEvent.getLogMessage());
					plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, loginDataEvent.getReason()));
					return;
				}
			}
		}catch (SocketTimeoutException e){
			apiDelay = true;
			
			plugin.api.lookupPlayerJoinInfo(playerName, "CONSOLE", new APIResponseCallback(){
				
				public void onSuccess(String response){
					try{
						PlayerJoinData joinData = plugin.api.getPlayerJoinInfo(playerName, "CONSOLE");
						
						if (joinData != null){
							PlayerLoginDataEvent loginDataEvent = new PlayerLoginDataEvent(playerName, playerAddress, joinData);
							
							plugin.pluginManager.callEvent(loginDataEvent);
							
							if (loginDataEvent.isConnectionPrevented()){
								Player player = plugin.server.getPlayer(playerName);
								
								if (player != null){
									player.kickPlayer(loginDataEvent.getKickMessage());
								}
								
								plugin.log.info(playerName + " (" + playerAddress + ") " + loginDataEvent.getLogMessage());
								plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, loginDataEvent.getReason()));
							}
						}
					}catch (SocketTimeoutException e){
						this.onFailure(e);
					}
				}
				
				public void onFailure(Exception e){
					plugin.log.warn("The API failed to respond even with a longer timeout, it might be down for some reason.");
				}
				
			});
		}
		
		// REMINDER: Leave this here, if the API does not respond we don't want to allow locally banned player to connect.
		if (apiDelay){
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
		plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName, (apiDelay) ? ConnectionAllowedReason.CHECKS_DELAYED : ConnectionAllowedReason.PASSED_CHECKS));
	}
	
}
