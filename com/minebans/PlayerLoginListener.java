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
	
	private boolean processPlayerInfoData(PlayerInfoData playerInfo, String playerName, String playerAddress){
		// 4
		if (playerInfo.shouldUnban()){
			plugin.banManager.unGlobalBan(playerName, "CONSOLE");
		}
		
		// 3
		if (plugin.config.getBoolean("block-known-compromised-accounts") && playerInfo.isKnownCompromised()){
			plugin.log.info(playerName + " (" + playerAddress + ") is using an account that is known to be compromised");
			plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, null, null));
			return true;
		}
		
		return false;
	}
	
	private boolean processPlayerBanData(PlayerBanData playerData, String playerName, String playerAddress){
		String key;
		Long limit;
		
		for (BanReason banReason : playerData.getBanReasons()){
			key = banReason.getConfigKey();
			
			if (plugin.config.getBoolean("max-bans." + key + ".enabled")){
				for (BanSeverity severity : banReason.getSeverties()){
					limit = plugin.config.getLong("max-bans." + key + "." + severity.getConfigName());
					
					if (limit != -1L && playerData.get(banReason, severity) > limit){
						plugin.log.info(playerName + " (" + playerAddress + ") has exceeded max-bans." + key + "." + severity.getConfigName());
						plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, banReason, severity));
						return true;
					}
				}
			}
		}
		
		for (BanSeverity severity : BanSeverity.getAll()){
			limit = plugin.config.getLong("max-bans.total." + severity.getConfigName());
			
			if (limit != -1L && playerData.getTotal(severity) > limit){
				plugin.log.info(playerName + " (" + playerAddress + ") has exceeded max-bans.total.total");
				plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, null, severity));
				return true;
			}
		}
		
		Long totalBans	= playerData.getTotal();
		Long last24 	= playerData.getLast24();
		Long removed	= playerData.getRemoved();
		
		if (totalBans > 0L || last24 > 0L || removed > 0L){
			if (plugin.config.getBoolean("use-compact-join-info")){
				for (Player player : plugin.server.getOnlinePlayers()){
					if (player.getName().equalsIgnoreCase(playerName) == false && player.hasPermission("minebans.alert.onjoin")){
						player.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Summary for " + playerName + " Total: " + ((totalBans <= 5L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + totalBans) + " Last 24 Hours: " + ((last24 == 0L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + last24);
					}
				}
			}else{
				for (Player player : plugin.server.getOnlinePlayers()){
					if (player.getName().equalsIgnoreCase(playerName) == false && player.hasPermission("minebans.alert.onjoin")){
						player.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Summary for " + playerName));
						player.sendMessage(ChatColor.GREEN + "Total bans on record: " + ((totalBans <= 5L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + totalBans);
						player.sendMessage(ChatColor.GREEN + "Bans in the last 24 hours: " + ((last24 == 0L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + last24);
						player.sendMessage(ChatColor.GREEN + "Bans that have been removed: " + ((removed <= 10L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + removed);
					}
				}
			}
		}
		
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(PlayerPreLoginEvent event){
		if (event.getResult() != Result.ALLOWED) return;
		
		final String playerAddress = event.getAddress().getHostAddress();
		final String playerName = event.getName();
		
		/*
		 * 1 - Local whitelist
		 * 2 - DNSBL
		 * 3 - Known compromised
		 * 4 - Unban if necessary then local ban list
		 * 5 - Global ban list
		 * 6 - Known alts (informational only) (not yet)
		 */
		
		// 1
		if (plugin.banManager.isExempt(playerName)){
			plugin.log.info(playerName + " (" + playerAddress + ") was found on the local ban exempt list, no further checks will be made.");
			plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName));
			return;
		}
		
		// 2
		if (plugin.config.getBoolean("block-public-proxies") && this.dnsblChecker.ipFound(playerAddress)){
			event.disallow(Result.KICK_OTHER, "You cannot connect to this server via a proxy");
			plugin.log.info(playerName + " (" + playerAddress + ") was prevented from connecting for using a public proxy.");
			plugin.pluginManager.callEvent(new PlayerConnectionDeniedEvent(playerName, null, null));
			return;
		}
		
		try{
			PlayerInfoData playerInfo = plugin.api.getPlayerInfo(playerName, "CONSOLE");
			
			if (this.processPlayerInfoData(playerInfo, playerName, playerAddress)){
				event.disallow(Result.KICK_OTHER, "You are using an account that is known to be compromised, you should change your password");
				return;
			}
		}catch (SocketTimeoutException e){
			plugin.log.info("The API failed to respond quick enough, trying again with more time. The player will be allowed to join for now.");
			
			for (Player player : plugin.server.getOnlinePlayers()){
				if (player.getName().equalsIgnoreCase(playerName) == false && player.hasPermission("minebans.alert.onapifail")){
					player.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " has not yet been checked with the API due to a timeout."));
					player.sendMessage(plugin.formatMessage(ChatColor.RED + "The check has been delayed, they will be kicked if neessary."));
				}
			}
			
			plugin.api.lookupPlayerInfo(playerName, "CONSOLE", new APIResponseCallback(){
				
				public void onSuccess(String response){
					try{
						PlayerInfoData playerInfo = new PlayerInfoData(response);
						
						if (processPlayerInfoData(playerInfo, playerName, playerAddress)){
							Player player = plugin.server.getPlayer(playerName);
							
							if (player != null){
								player.kickPlayer("You are using an account that is known to be compromised, you should change your password");
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
		
		// 5
		if (plugin.config.getBooleansOr("max-bans.*.enabled")){
			try{
				PlayerBanData playerData = plugin.api.getPlayerBans(playerName, "CONSOLE");
				
				if (this.processPlayerBanData(playerData, playerName, playerAddress)){
					event.disallow(Result.KICK_BANNED, "You have too many bans to connect to this server (apply for the whitelist at minebans.com)");
					return;
				}
			}catch (SocketTimeoutException e){
				plugin.log.info("The API failed to respond quick enough, trying again with more time. The player will be allowed to join for now.");
				
				for (Player player : plugin.server.getOnlinePlayers()){
					if (player.getName().equalsIgnoreCase(playerName) == false && player.hasPermission("minebans.alert.onapifail")){
						player.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " has not yet been checked with the API due to a timeout."));
						player.sendMessage(plugin.formatMessage(ChatColor.RED + "The check has been delayed, they will be kicked if neessary."));
					}
				}
				
				plugin.api.lookupPlayerBans(playerName, "CONSOLE", new APIResponseCallback(){
					
					public void onSuccess(String response){
						try{
							PlayerBanData playerData = new PlayerBanData(response);
							
							if (processPlayerBanData(playerData, playerName, playerAddress)){
								Player player = plugin.server.getPlayer(playerName);
								
								if (player != null){
									player.kickPlayer("You have too many bans to connect to this server (apply for the whitelist at minebans.com)");
								}
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
		}
		
		plugin.log.info(playerName + " (" + playerAddress + ") was allowed to join the server.");
		plugin.pluginManager.callEvent(new PlayerConnectionAllowedEvent(playerName));
	}
	
}
