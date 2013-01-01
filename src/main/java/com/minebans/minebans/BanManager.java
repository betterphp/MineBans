package com.minebans.minebans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minebans.minebans.api.callback.PlayerBanCallback;
import com.minebans.minebans.api.callback.PlayerUnbanCallback;
import com.minebans.minebans.api.request.PlayerBanRequest;
import com.minebans.minebans.api.request.PlayerUnbanRequest;
import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.bans.BanType;
import com.minebans.minebans.events.PlayerBanEvent;
import com.minebans.minebans.events.PlayerExemptEvent;
import com.minebans.minebans.events.PlayerGlobalBanEvent;
import com.minebans.minebans.events.PlayerLocalBanEvent;
import com.minebans.minebans.events.PlayerTempBanEvent;
import com.minebans.minebans.events.PlayerUnExemptEvent;
import com.minebans.minebans.events.PlayerUnbanEvent;
import com.minebans.minebans.util.PlayerDataStore;
import com.minebans.minebans.util.PlayerListStore;

public class BanManager {
	
	private MineBans plugin;
	
	private PlayerListStore globallyBannedPlayers;
	private PlayerListStore locallyBannedPlayers;
	private PlayerDataStore tempBannedPlayers;
	private PlayerListStore localExemptList;
	
	public BanManager(MineBans plugin){
		this.plugin = plugin;
		
		this.globallyBannedPlayers = new PlayerListStore(new File(plugin.getBaseDirPath() + File.separator + "globally-banned-players.txt"));
		this.locallyBannedPlayers = new PlayerListStore(new File(plugin.getBaseDirPath() + File.separator + "locally-banned-players.txt"));
		this.tempBannedPlayers = new PlayerDataStore(new File(plugin.getBaseDirPath() + File.separator + "temp-banned-players.txt"));
		this.localExemptList = new PlayerListStore(new File(plugin.getBaseDirPath() + File.separator + "ban-exceptions.txt"));
		
		this.globallyBannedPlayers.load();
		this.locallyBannedPlayers.load();
		this.tempBannedPlayers.load();
		this.localExemptList.load();
	}
	
	public void kickPlayer(String playerName, boolean log, String message){
		Player player = plugin.server.getPlayer(playerName);
		
		if (player != null){
			player.kickPlayer(message);
			
			plugin.notificationManager.sendKickNotification(playerName, log);
		}
	}
	
	public void kickPlayer(String playerName, boolean log){
		this.kickPlayer(playerName, log, plugin.config.getString(Config.MESSAGE_KICK));
	}
	
	public void locallyBanPlayer(String playerName, boolean log, boolean notify){
		PlayerLocalBanEvent localBanEvent = new PlayerLocalBanEvent(playerName);
		PlayerBanEvent banEvent = new PlayerBanEvent(playerName, BanType.LOCAL);
		
		plugin.pluginManager.callEvent(localBanEvent);
		banEvent.setCancelled(localBanEvent.isCancelled());
		plugin.pluginManager.callEvent(banEvent);
		
		if (!banEvent.isCancelled()){
			Player player = plugin.getServer().getPlayer(playerName);
			
			if (player != null){
				player.kickPlayer(plugin.config.getString(Config.MESSAGE_BAN));
			}
			
			this.locallyBannedPlayers.add(playerName);
			this.locallyBannedPlayers.save();
			
			if (notify){
				plugin.notificationManager.sendBanNotification(playerName, log);
			}
		}
	}
	
	public void locallyBanPlayer(String playerName, boolean log){
		this.locallyBanPlayer(playerName, log, true);
	}
	
	public void locallyBanPlayer(String playerName){
		this.locallyBanPlayer(playerName, true, true);
	}
	
	public void globallyBanPlayer(String playerName, final String issuedBy, BanReason reason, boolean log, boolean notify){
		PlayerGlobalBanEvent globalBanEvent = new PlayerGlobalBanEvent(playerName, reason);
		PlayerBanEvent banEvent = new PlayerBanEvent(playerName, BanType.GLOBAL);
		
		plugin.pluginManager.callEvent(globalBanEvent);
		banEvent.setCancelled(globalBanEvent.isCancelled());
		plugin.pluginManager.callEvent(banEvent);
		
		if (!banEvent.isCancelled()){
			Player player = plugin.getServer().getPlayer(playerName);
			
			if (player != null){
				player.kickPlayer(plugin.config.getString(Config.MESSAGE_BAN) + "\n" + ChatColor.RESET + "(appeal at minebans.com)");
			}
			
			this.globallyBannedPlayers.add(playerName);
			this.globallyBannedPlayers.save();
			
			(new PlayerBanRequest(plugin, playerName, issuedBy, reason, plugin.evidenceManager.collectFor(reason, playerName))).process(new PlayerBanCallback(plugin){
				
				public void onFailure(Exception exception){
					CommandSender sender = (issuedBy.equalsIgnoreCase("console")) ? Bukkit.getConsoleSender() : Bukkit.getServer().getPlayer(issuedBy);
					
					plugin.api.handleException(exception, sender);
				}
				
			});
			
			if (notify){
				plugin.notificationManager.sendBanNotification(playerName, reason, log);
			}
		}
	}
	
	public void globallyBanPlayer(String playerName, String issuedBy, BanReason reason, boolean log){
		this.globallyBanPlayer(playerName, issuedBy, reason, log, true);
	}
	
	public void globallyBanPlayer(String playerName, String issuedBy, BanReason reason){
		this.globallyBanPlayer(playerName, issuedBy, reason, true, true);
	}
	
	public void tempBanPlayer(String playerName, int banDuration, boolean log, boolean notify){
		PlayerTempBanEvent tempBanEvent = new PlayerTempBanEvent(playerName, banDuration);
		PlayerBanEvent banEvent = new PlayerBanEvent(playerName, BanType.TEMP);
		
		plugin.pluginManager.callEvent(tempBanEvent);
		banEvent.setCancelled(tempBanEvent.isCancelled());
		plugin.pluginManager.callEvent(banEvent);
		
		if (!banEvent.isCancelled()){
			Player player = plugin.getServer().getPlayer(playerName);
			
			if (player != null){
				double days = Math.floor(banDuration / 86400.0d);
				double hours = Math.round((banDuration - (days * 86400.0d)) / 3600.0d);
				
				player.kickPlayer(plugin.config.getString(Config.MESSAGE_TEMPBAN) + days + " " + ((days == 1D) ? "day" : "days") + " and " + hours + " " + ((hours == 1D) ? "hour" : "hours"));
			}
			
			this.tempBannedPlayers.add(playerName, new Long((System.currentTimeMillis() / 1000) + banDuration).toString());
			this.tempBannedPlayers.save();
			
			if (notify){
				plugin.notificationManager.sendBanNotification(playerName, banDuration, log);
			}
		}
	}
	
	public void tempBanPlayer(String playerName, int banDuration, boolean log){
		this.tempBanPlayer(playerName, banDuration, log, true);
	}
	
	public void tempBanPlayer(String playerName, int banDuration){
		this.tempBanPlayer(playerName, banDuration, true, true);
	}
	
	// NOTE: Called by APIInterface.java to remove the player only if the request completed correctly.
	public void unbanPlayerAPICallback(String playerName){
		if (this.globallyBannedPlayers.contains(playerName)){
			PlayerUnbanEvent unbanEvent = new PlayerUnbanEvent(playerName, BanType.GLOBAL);
			plugin.pluginManager.callEvent(unbanEvent);
			
			if (!unbanEvent.isCancelled()){
				this.globallyBannedPlayers.remove(playerName);
				this.globallyBannedPlayers.save();
				
				plugin.notificationManager.sendUnbanNotification(playerName, true);
			}
		}
	}
	
	public void unGlobalBan(String playerName, final String issuedBy){
		(new PlayerUnbanRequest(plugin, playerName, issuedBy)).process(new PlayerUnbanCallback(plugin, playerName){
			
			public void onFailure(Exception exception){
				CommandSender sender = (issuedBy.equalsIgnoreCase("console")) ? Bukkit.getConsoleSender() : Bukkit.getServer().getPlayer(issuedBy);
				
				plugin.api.handleException(exception, sender);
			}
			
		});
	}
	
	public void unLocalBan(String playerName, boolean log){
		PlayerUnbanEvent unbanEvent = new PlayerUnbanEvent(playerName, BanType.LOCAL);
		plugin.pluginManager.callEvent(unbanEvent);
		
		if (!unbanEvent.isCancelled()){
			this.locallyBannedPlayers.remove(playerName);
			this.locallyBannedPlayers.save();
			
			plugin.notificationManager.sendUnbanNotification(playerName, log);
		}
	}
	
	public void unTempBan(String playerName, boolean log){
		PlayerUnbanEvent unbanEvent = new PlayerUnbanEvent(playerName, BanType.TEMP);
		plugin.pluginManager.callEvent(unbanEvent);
		
		if (!unbanEvent.isCancelled()){
			this.tempBannedPlayers.remove(playerName);
			this.tempBannedPlayers.save();
			
			plugin.notificationManager.sendUnbanNotification(playerName, log);
		}
	}
	
	public void unbanPlayer(String playerName, String issuedBy, boolean log){
		if (this.isGloballyBanned(playerName)){
			this.unGlobalBan(playerName, issuedBy);
		}else if (this.isLocallyBanned(playerName)){
			this.unLocalBan(playerName, log);
		}else if (this.isTempBanned(playerName)){
			this.unTempBan(playerName, log);
		}
	}
	
	public void unbanPlayer(String playerName, String issuedBy){
		this.unbanPlayer(playerName, issuedBy, true);
	}
	
	public void exemptPlayer(String playerName, boolean log){
		PlayerExemptEvent exemptEvent = new PlayerExemptEvent(playerName);
		plugin.pluginManager.callEvent(exemptEvent);
		
		if (!exemptEvent.isCancelled()){
			this.localExemptList.add(playerName);
			this.localExemptList.save();
			
			plugin.notificationManager.sendExemptListNotification(playerName, log);
		}
	}
	
	public void exemptPlayer(String playerName){
		this.exemptPlayer(playerName, true);
	}
	
	public void unExemptPlayer(String playerName, boolean log){
		PlayerUnExemptEvent unExemptEvent = new PlayerUnExemptEvent(playerName); 
		plugin.pluginManager.callEvent(unExemptEvent);
		
		if (!unExemptEvent.isCancelled()){
			Player player = plugin.getServer().getPlayer(playerName);
			
			if (player != null){
				player.kickPlayer(plugin.config.getString(Config.MESSAGE_UNEXEMPT));
			}
			
			this.localExemptList.remove(playerName);
			this.localExemptList.save();
			
			plugin.notificationManager.sendUnExemptListNotification(playerName, log);
		}
	}
	
	public void unExemptPlayer(String playerName){
		this.unExemptPlayer(playerName, true);
	}
	
	public void checkExpiredTempBans(){
		for (String playerName : new ArrayList<String>(this.tempBannedPlayers.getPlayerNames())){
			this.isTempBanned(playerName);
		}
	}
	
	public List<String> getLocallyBannedPlayers(){
		return new ArrayList<String>(this.locallyBannedPlayers.getPlayerNames());
	}
	
	public List<String> getGloballyBannedPlayers(){
		return new ArrayList<String>(this.globallyBannedPlayers.getPlayerNames());
	}
	
	public List<String> getTempBannedPlayers(){
		this.checkExpiredTempBans();
		
		return new ArrayList<String>(this.tempBannedPlayers.getPlayerNames());
	}
	
	public boolean isLocallyBanned(String playerName){
		return this.locallyBannedPlayers.contains(playerName);
	}
	
	public boolean isTempBanned(String playerName){
		if (this.tempBannedPlayers.contains(playerName)){
			if (this.getTempBanRemaining(playerName) == 0){
				this.unTempBan(playerName, true);
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	public Integer getTempBanRemaining(String playerName){
		return Math.max((int) (Long.parseLong(this.tempBannedPlayers.getData(playerName)) - (System.currentTimeMillis() / 1000)), 0);
	}
	
	public boolean isGloballyBanned(String playerName){
		return this.globallyBannedPlayers.contains(playerName);
	}
	
	public boolean isBanned(String playerName){
		return (this.isGloballyBanned(playerName) || this.isLocallyBanned(playerName) || this.isTempBanned(playerName));
	}
	
	public boolean isExempt(String playerName){
		return this.localExemptList.contains(playerName);
	}
	
}
