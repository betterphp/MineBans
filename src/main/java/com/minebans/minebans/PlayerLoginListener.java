package com.minebans.minebans;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.minebans.minebans.api.ConnectionAllowedReason;
import com.minebans.minebans.api.ConnectionDeniedReason;
import com.minebans.minebans.api.data.PlayerJoinInfoData;
import com.minebans.minebans.api.request.PlayerJoinInfoRequest;
import com.minebans.minebans.events.PlayerConnectionAllowedEvent;
import com.minebans.minebans.events.PlayerConnectionDeniedEvent;
import com.minebans.minebans.events.PlayerLoginDataEvent;

import uk.co.jacekk.bukkit.baseplugin.v9.event.BaseListener;

public class PlayerLoginListener extends BaseListener<MineBans> {
	
	public PlayerLoginListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event){
		try{
			if (!AsyncPlayerPreLoginEvent.class.getDeclaredMethod("getResult").invoke(event).toString().equals("ALLOWED")){
				return;
			}
		}catch (Exception e){
			if (event.getLoginResult() != Result.ALLOWED){
				return;
			}
		}
		
		final String playerAddress = event.getAddress().getHostAddress();
		final String playerName = event.getName();
		
		if (plugin.banManager.isExempt(playerName)){
			plugin.log.info(playerName + " (" + playerAddress + ") was found on the local ban exempt list, no further checks will be made.");
			plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName, ConnectionAllowedReason.PLAYER_EXEMPT));
			return;
		}
		
		PlayerJoinInfoData joinData = (new PlayerJoinInfoRequest(plugin, "CONSOLE", playerName)).process();
		
		if (joinData != null){
			PlayerLoginDataEvent loginDataEvent = new PlayerLoginDataEvent(playerName, playerAddress, joinData);
			
			plugin.pluginManager.callEvent(loginDataEvent);
			
			if (loginDataEvent.isConnectionPrevented()){
				event.disallow(Result.KICK_OTHER, loginDataEvent.getKickMessage());
				plugin.log.info(playerName + " (" + playerAddress + ") " + loginDataEvent.getLogMessage());
				plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, loginDataEvent.getReason()));
				return;
			}
		}else{
			plugin.log.warn("The API failed to respond, reverting to local only checks.");
			
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
		
		if (plugin.config.getBoolean(Config.USE_COMPACT_JOIN_INFO)){
			NotificationManager.sendCompactJoinNotification(playerName, joinData);
		}else{
			NotificationManager.sendJoinNotification(playerName, playerAddress, joinData);
		}
		
		plugin.log.info(playerName + " (" + playerAddress + ") was allowed to join the server.");
		plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName, ConnectionAllowedReason.PASSED_CHECKS));
	}
	
}