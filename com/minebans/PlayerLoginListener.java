package com.minebans;

import java.net.SocketTimeoutException;

import javax.naming.NamingException;

import org.bukkit.ChatColor;
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
import com.minebans.bans.BanReason;
import com.minebans.bans.BanSeverity;
import com.minebans.events.PlayerConnectionAllowedEvent;
import com.minebans.events.PlayerConnectionDeniedEvent;
import com.minebans.util.DNSBLChecker;

public class PlayerLoginListener implements Listener {
	
	private MineBans plugin;
	private DNSBLChecker dnsblChecker;
	
	public PlayerLoginListener(MineBans plugin){
		this.plugin = plugin;
		
		try{
			this.dnsblChecker = new DNSBLChecker();
			
			this.dnsblChecker.addDNSBL("dnsbl.proxybl.org");
			this.dnsblChecker.addDNSBL("http.dnsbl.sorbs.net");
			this.dnsblChecker.addDNSBL("socks.dnsbl.sorbs.net");
			this.dnsblChecker.addDNSBL("misc.dnsbl.sorbs.net");
			this.dnsblChecker.addDNSBL("tor.dnsbl.sectoor.de");
		}catch (NamingException e){
			plugin.log.fatal("Something odd happened (you should report this on BukkitDev)");
			e.printStackTrace();
		}
	}
	
	private String processPlayerInfoData(PlayerInfoData playerInfo, String playerName, String playerAddress){
		if (playerInfo.shouldUnban()){
			plugin.banManager.unGlobalBan(playerName, "CONSOLE");
		}
		
		if (playerInfo.bannedFromGroup()){
			plugin.log.info(playerName + " (" + playerAddress + ") has been banned from another server linked to your account");
			plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, null, null));
			
			return "You have been banned from all of this owners's servers";
		}
		
		if (plugin.config.getBoolean(MineBansConfig.BLOCK_COMPROMISED_ACCOUNTS) && playerInfo.isKnownCompromised()){
			plugin.log.info(playerName + " (" + playerAddress + ") is using an account that is known to be compromised");
			plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, null, null));
			
			return "You are using an account that is known to be compromised, you should change your password";
		}
		
		return null;
	}
	
	private String processPlayerBanData(PlayerBanData playerData, String playerName, String playerAddress){
		Long limit;
		
		for (BanReason banReason : playerData.getBanReasons()){
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(banReason))){
				for (BanSeverity severity : banReason.getSeverties()){
					limit = plugin.config.getLong(MineBansConfig.getReasonLimit(banReason, severity));
					
					if (limit != -1L && playerData.get(banReason, severity) > limit){
						plugin.log.info(playerName + " (" + playerAddress + ") has exceeded " + MineBansConfig.getReasonLimit(banReason, severity).getKey());
						plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, banReason, severity));
						
						return "You have too many bans for " + banReason.name().toLowerCase() + " to connect to this server";
					}
				}
			}
		}
		
		for (BanSeverity severity : BanSeverity.getAll()){
			limit = plugin.config.getLong(MineBansConfig.getTotalLimit(severity));
			
			if (limit != -1L && playerData.getTotal(severity) > limit){
				plugin.log.info(playerName + " (" + playerAddress + ") has exceeded max-bans.total.total");
				plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, null, severity));
				
				return "You have too many bans to connect to this server";
			}
		}
		
		plugin.notificationManager.sendJoinNotification(playerName, playerData);
		
		return null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(PlayerPreLoginEvent event){
		if (event.getResult() != Result.ALLOWED) return;
		
		final String playerAddress = event.getAddress().getHostAddress();
		final String playerName = event.getName();
		
		if (plugin.banManager.isExempt(playerName)){
			plugin.log.info(playerName + " (" + playerAddress + ") was found on the local ban exempt list, no further checks will be made.");
			plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName));
			return;
		}
		
		if (plugin.config.getBoolean(MineBansConfig.BLOCK_PROXIES) && this.dnsblChecker.ipFound(playerAddress)){
			event.disallow(Result.KICK_OTHER, "You cannot connect to this server via a proxy");
			plugin.log.info(playerName + " (" + playerAddress + ") was prevented from connecting for using a public proxy.");
			plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, null, null));
			return;
		}
		
		try{
			String kickMessage = this.processPlayerInfoData(plugin.api.getPlayerInfo(playerName, "CONSOLE"), playerName, playerAddress);
			
			if (kickMessage != null){
				event.disallow(Result.KICK_OTHER, kickMessage);
				return;
			}
		}catch (SocketTimeoutException e){
			plugin.log.info("The API failed to respond quick enough, trying again with more time. The player will be allowed to join for now.");
			
			for (Player player : MineBansPermission.ALERT_ON_API_FAIL.getPlayersWithPermission()){
				player.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " has not yet been checked with the API due to a timeout."));
				player.sendMessage(plugin.formatMessage(ChatColor.RED + "The check has been delayed, they will be kicked if neessary."));
			}
			
			plugin.api.lookupPlayerInfo(playerName, "CONSOLE", new APIResponseCallback(){
				
				public void onSuccess(String response){
					try{
						String kickMessage = processPlayerInfoData(new PlayerInfoData(response), playerName, playerAddress);
						Player player = plugin.server.getPlayer(playerName);
						
						if (kickMessage != null && player != null){
							player.kickPlayer(kickMessage);
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
		
		if (plugin.banManager.isBanned(playerName)){
			if (plugin.banManager.isGloballyBanned(playerName)){
				event.disallow(Result.KICK_BANNED, "You have been banned from this server (appeal at minebans.com)");				
			}else if (plugin.banManager.isLocallyBanned(playerName)){
				event.disallow(Result.KICK_BANNED, "You have been banned from this server");
			}else{
				event.disallow(Result.KICK_BANNED, "You have been temporarily banned from this server");
			}
			
			plugin.log.info(playerName + " (" + playerAddress + ") was prevented from connecting as they have been banned.");
			plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, null, null));
			return;
		}
		
		for (BanReason banReason : BanReason.getAll()){
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(banReason))){
				try{
					String kickMessage = this.processPlayerBanData(plugin.api.getPlayerBans(playerName, "CONSOLE"), playerName, playerAddress);
					
					if (kickMessage != null){
						event.disallow(Result.KICK_BANNED, kickMessage);
						return;
					}
				}catch (SocketTimeoutException e){
					plugin.log.info("The API failed to respond quick enough, trying again with more time. The player will be allowed to join for now.");
					
					for (Player player : MineBansPermission.ALERT_ON_API_FAIL.getPlayersWithPermission()){
						player.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " has not yet been checked with the API due to a timeout."));
						player.sendMessage(plugin.formatMessage(ChatColor.RED + "The check has been delayed, they will be kicked if neessary."));
					}
					
					plugin.api.lookupPlayerBans(playerName, "CONSOLE", new APIResponseCallback(){
						
						public void onSuccess(String response){
							try{
								String kickMessage = processPlayerBanData(new PlayerBanData(response), playerName, playerAddress);
								Player player = plugin.server.getPlayer(playerName);
								
								if (kickMessage != null && player != null){
									player.kickPlayer(kickMessage);
								}
							}catch (Exception e){
								this.onFailure(e);
							}
						}
						
						public void onFailure(Exception e){
							plugin.log.info("The API failed to respond even with a longer timeout, it might be down for some reason.");
						}
						
					});
				}
				
				break;
			}
		}
		
		plugin.log.info(playerName + " (" + playerAddress + ") was allowed to join the server.");
		plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName));
	}
	
}
