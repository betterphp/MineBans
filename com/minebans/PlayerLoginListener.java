package com.minebans;

import java.net.SocketTimeoutException;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.json.simple.parser.ParseException;

import com.minebans.api.APIResponseCallback;
import com.minebans.api.PlayerBanData;
import com.minebans.api.PlayerInfoData;
import com.minebans.events.PlayerConnectionAllowedEvent;
import com.minebans.events.PlayerConnectionDeniedEvent;
import com.minebans.joinchecks.BanDataJoinCheck;
import com.minebans.joinchecks.ConnectionAllowedReason;
import com.minebans.joinchecks.ConnectionDeniedReason;
import com.minebans.joinchecks.InfoDataJoinCheck;
import com.minebans.joinchecks.LocalJoinCheck;

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
		
		ConnectionDeniedReason reason;
		LocalJoinCheck localCheck;
		
		for (Entry<ConnectionDeniedReason, LocalJoinCheck> enabledCheck : plugin.joinCheckManager.getLocalChecks()){
			reason = enabledCheck.getKey();
			localCheck = enabledCheck.getValue();
			
			if (localCheck.shouldPreventConnection(playerName, playerAddress)){
				event.disallow(Result.KICK_OTHER, reason.getKickMessage());
				plugin.log.info(playerName + " (" + playerAddress + ") " + reason.getLogMessage());
				plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, reason));
				return;
			}
		}
		
		InfoDataJoinCheck infoDataCheck;
		
		try{
			PlayerInfoData playerInfo = plugin.api.getPlayerInfo(playerName, "CONSOLE");
			
			for (Entry<ConnectionDeniedReason, InfoDataJoinCheck> enabledCheck : plugin.joinCheckManager.getInfoDataChecks()){
				reason = enabledCheck.getKey();
				infoDataCheck = enabledCheck.getValue();
				
				if (infoDataCheck.shouldPreventConnection(playerName, playerAddress, playerInfo)){
					event.disallow(Result.KICK_OTHER, reason.getKickMessage());
					plugin.log.info(playerName + " (" + playerAddress + ") " + reason.getLogMessage());
					plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, reason));
					return;
				}
			}
		}catch (SocketTimeoutException e){
			plugin.api.lookupPlayerInfo(playerName, "CONSOLE", new APIResponseCallback(){
				
				public void onSuccess(String response){
					try{
						ConnectionDeniedReason reason;
						InfoDataJoinCheck infoDataCheck;
						
						PlayerInfoData playerInfo = new PlayerInfoData(response);
						
						for (Entry<ConnectionDeniedReason, InfoDataJoinCheck> enabledCheck : plugin.joinCheckManager.getInfoDataChecks()){
							reason = enabledCheck.getKey();
							infoDataCheck = enabledCheck.getValue();
							
							if (infoDataCheck.shouldPreventConnection(playerName, playerAddress, playerInfo)){
								Player player = plugin.server.getPlayer(playerName); 
								
								player.kickPlayer(reason.getKickMessage());
								
								plugin.log.info(playerName + " (" + playerAddress + ") " + reason.getLogMessage());
								plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, reason));
								
								return;
							}
						}
					}catch (ParseException e){
						this.onFailure(e);
					}
				}
				
				public void onFailure(Exception e){
					plugin.log.warn("The API failed to respond even with a longer timeout, it might be down for some reason.");
				}
				
			});
		}
		
		BanDataJoinCheck banDataCheck;
		
		try{
			PlayerBanData banData = plugin.api.getPlayerBans(playerName, "CONSOLE");
			
			for (Entry<ConnectionDeniedReason, BanDataJoinCheck> enabledCheck : plugin.joinCheckManager.getBanDataChecks()){
				reason = enabledCheck.getKey();
				banDataCheck = enabledCheck.getValue();
				
				if (banDataCheck.shouldPreventConnection(playerName, playerAddress, banData)){
					event.disallow(Result.KICK_OTHER, reason.getKickMessage());
					plugin.log.info(playerName + " (" + playerAddress + ") " + reason.getLogMessage());
					plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, reason));
					return;
				}
			}
		}catch (SocketTimeoutException e){
			plugin.api.lookupPlayerBans(playerName, "CONSOLE", new APIResponseCallback(){
				
				public void onSuccess(String response){
					try{
						ConnectionDeniedReason reason;
						BanDataJoinCheck banDataCheck;
						
						PlayerBanData banData = new PlayerBanData(response);
						
						for (Entry<ConnectionDeniedReason, BanDataJoinCheck> enabledCheck : plugin.joinCheckManager.getBanDataChecks()){
							reason = enabledCheck.getKey();
							banDataCheck = enabledCheck.getValue();
							
							if (banDataCheck.shouldPreventConnection(playerName, playerAddress, banData)){
								Player player = plugin.server.getPlayer(playerName); 
								
								player.kickPlayer(reason.getKickMessage());
								
								plugin.log.info(playerName + " (" + playerAddress + ") " + reason.getLogMessage());
								plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, reason));
								
								return;
							}
						}
					}catch (ParseException e){
						this.onFailure(e);
					}
				}
				
				public void onFailure(Exception e){
					plugin.log.info("The API failed to respond even with a longer timeout, it might be down for some reason.");
				}
				
			});
		}
		
		plugin.seenPlayers.add(playerName.toLowerCase());
		
		plugin.log.info(playerName + " (" + playerAddress + ") was allowed to join the server.");
		plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName, ConnectionAllowedReason.PASSED_CHECKS));
	}
	
}
