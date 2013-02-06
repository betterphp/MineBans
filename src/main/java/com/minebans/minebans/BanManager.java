package com.minebans.minebans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.jacekk.bukkit.baseplugin.v9.storage.DataStore;
import uk.co.jacekk.bukkit.baseplugin.v9.storage.ListStore;

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

public class BanManager {
	
	private MineBans plugin;
	
	private ListStore globallyBannedPlayers;
	private ListStore locallyBannedPlayers;
	private DataStore tempBannedPlayers;
	private ListStore localExemptList;
	
	public BanManager(MineBans plugin){
		this.plugin = plugin;
		
		this.globallyBannedPlayers = new ListStore(new File(plugin.getBaseDirPath() + File.separator + "globally-banned-players.txt"), false);
		this.locallyBannedPlayers = new ListStore(new File(plugin.getBaseDirPath() + File.separator + "locally-banned-players.txt"), false);
		this.tempBannedPlayers = new DataStore(new File(plugin.getBaseDirPath() + File.separator + "temp-banned-players.txt"), false);
		this.localExemptList = new ListStore(new File(plugin.getBaseDirPath() + File.separator + "ban-exceptions.txt"), false);
		
		this.globallyBannedPlayers.load();
		this.locallyBannedPlayers.load();
		this.tempBannedPlayers.load();
		this.localExemptList.load();
	}
	
	public void kickPlayer(String playerName, String issuedBy, boolean log, String message){
		Player player = plugin.server.getPlayer(playerName);
		
		if (player != null){
			player.kickPlayer(message);
			
			NotificationManager.sendKickNotification(playerName, issuedBy, log);
		}
	}
	
	public void kickPlayer(String playerName, String issuedBy, boolean log){
		this.kickPlayer(playerName, issuedBy, log, plugin.config.getString(Config.MESSAGE_KICK_PLAYER));
	}
	
	public void locallyBanPlayer(String playerName, String issuedBy, boolean log, boolean notify){
		PlayerLocalBanEvent localBanEvent = new PlayerLocalBanEvent(playerName);
		PlayerBanEvent banEvent = new PlayerBanEvent(playerName, BanType.LOCAL);
		
		plugin.pluginManager.callEvent(localBanEvent);
		banEvent.setCancelled(localBanEvent.isCancelled());
		plugin.pluginManager.callEvent(banEvent);
		
		if (!banEvent.isCancelled()){
			Player player = plugin.getServer().getPlayer(playerName);
			
			if (player != null){
				player.kickPlayer(NotificationManager.parseNotification(plugin.config.getString(Config.MESSAGE_LOCAL_BAN_PLAYER), playerName, issuedBy, null, 0));
			}
			
			this.locallyBannedPlayers.add(playerName);
			this.locallyBannedPlayers.save();
			
			if (notify){
				NotificationManager.sendBanNotification(playerName, issuedBy, log);
			}
		}
	}
	
	public void locallyBanPlayer(String playerName, String issuedBy, boolean log){
		this.locallyBanPlayer(playerName, issuedBy, log, true);
	}
	
	public void locallyBanPlayer(String playerName, String issuedBy){
		this.locallyBanPlayer(playerName, issuedBy, true, true);
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
				player.kickPlayer(NotificationManager.parseNotification(plugin.config.getString(Config.MESSAGE_GLOBAL_BAN_PLAYER), playerName, issuedBy, reason, 0) + "\n" + ChatColor.RESET + "(appeal at minebans.com)");
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
				NotificationManager.sendBanNotification(playerName, issuedBy, reason, log);
			}
		}
	}
	
	public void globallyBanPlayer(String playerName, String issuedBy, BanReason reason, boolean log){
		this.globallyBanPlayer(playerName, issuedBy, reason, log, true);
	}
	
	public void globallyBanPlayer(String playerName, String issuedBy, BanReason reason){
		this.globallyBanPlayer(playerName, issuedBy, reason, true, true);
	}
	
	public void tempBanPlayer(String playerName, String issuedBy, int duration, boolean log, boolean notify){
		PlayerTempBanEvent tempBanEvent = new PlayerTempBanEvent(playerName, duration);
		PlayerBanEvent banEvent = new PlayerBanEvent(playerName, BanType.TEMP);
		
		plugin.pluginManager.callEvent(tempBanEvent);
		banEvent.setCancelled(tempBanEvent.isCancelled());
		plugin.pluginManager.callEvent(banEvent);
		
		if (!banEvent.isCancelled()){
			Player player = plugin.getServer().getPlayer(playerName);
			
			if (player != null){
				player.kickPlayer(NotificationManager.parseNotification(plugin.config.getString(Config.MESSAGE_TEMP_BAN_PLAYER), playerName, issuedBy, null, duration));
			}
			
			this.tempBannedPlayers.add(playerName, new Long((System.currentTimeMillis() / 1000) + duration).toString());
			this.tempBannedPlayers.save();
			
			if (notify){
				NotificationManager.sendBanNotification(playerName, issuedBy, duration, log);
			}
		}
	}
	
	public void tempBanPlayer(String playerName, String issuedBy, int banDuration, boolean log){
		this.tempBanPlayer(playerName, issuedBy, banDuration, log, true);
	}
	
	public void tempBanPlayer(String playerName, String issuedBy, int banDuration){
		this.tempBanPlayer(playerName, issuedBy, banDuration, true, true);
	}
	
	// NOTE: Called by APIInterface.java to remove the player only if the request completed correctly.
	public void unbanPlayerAPICallback(String playerName, String issuedBy){
		if (this.globallyBannedPlayers.contains(playerName)){
			PlayerUnbanEvent unbanEvent = new PlayerUnbanEvent(playerName, BanType.GLOBAL);
			plugin.pluginManager.callEvent(unbanEvent);
			
			if (!unbanEvent.isCancelled()){
				this.globallyBannedPlayers.remove(playerName);
				this.globallyBannedPlayers.save();
				
				NotificationManager.sendUnbanNotification(playerName, issuedBy, true);
			}
		}
	}
	
	public void unGlobalBan(String playerName, final String issuedBy){
		(new PlayerUnbanRequest(plugin, playerName, issuedBy)).process(new PlayerUnbanCallback(plugin, playerName, issuedBy){
			
			public void onFailure(Exception exception){
				CommandSender sender = (issuedBy.equalsIgnoreCase("console")) ? Bukkit.getConsoleSender() : Bukkit.getServer().getPlayer(issuedBy);
				
				plugin.api.handleException(exception, sender);
			}
			
		});
	}
	
	public void unLocalBan(String playerName, String issuedBy, boolean log){
		PlayerUnbanEvent unbanEvent = new PlayerUnbanEvent(playerName, BanType.LOCAL);
		plugin.pluginManager.callEvent(unbanEvent);
		
		if (!unbanEvent.isCancelled()){
			this.locallyBannedPlayers.remove(playerName);
			this.locallyBannedPlayers.save();
			
			NotificationManager.sendUnbanNotification(playerName, issuedBy, log);
		}
	}
	
	public void unTempBan(String playerName, String issuedBy, boolean log){
		PlayerUnbanEvent unbanEvent = new PlayerUnbanEvent(playerName, BanType.TEMP);
		plugin.pluginManager.callEvent(unbanEvent);
		
		if (!unbanEvent.isCancelled()){
			this.tempBannedPlayers.remove(playerName);
			this.tempBannedPlayers.save();
			
			NotificationManager.sendUnbanNotification(playerName, issuedBy, log);
		}
	}
	
	public void unbanPlayer(String playerName, String issuedBy, boolean log){
		if (this.isGloballyBanned(playerName)){
			this.unGlobalBan(playerName, issuedBy);
		}else if (this.isLocallyBanned(playerName)){
			this.unLocalBan(playerName, issuedBy, log);
		}else if (this.isTempBanned(playerName)){
			this.unTempBan(playerName, issuedBy, log);
		}
	}
	
	public void unbanPlayer(String playerName, String issuedBy){
		this.unbanPlayer(playerName, issuedBy, true);
	}
	
	public void exemptPlayer(String playerName, String issuedBy, boolean log){
		PlayerExemptEvent exemptEvent = new PlayerExemptEvent(playerName);
		plugin.pluginManager.callEvent(exemptEvent);
		
		if (!exemptEvent.isCancelled()){
			this.localExemptList.add(playerName);
			this.localExemptList.save();
			
			NotificationManager.sendExemptListNotification(playerName, issuedBy, log);
		}
	}
	
	public void exemptPlayer(String playerName, String issuedBy){
		this.exemptPlayer(playerName, issuedBy, true);
	}
	
	public void unExemptPlayer(String playerName, String issuedBy, boolean log){
		PlayerUnExemptEvent unExemptEvent = new PlayerUnExemptEvent(playerName); 
		plugin.pluginManager.callEvent(unExemptEvent);
		
		if (!unExemptEvent.isCancelled()){
			Player player = plugin.getServer().getPlayer(playerName);
			
			if (player != null){
				player.kickPlayer(plugin.config.getString(Config.MESSAGE_UNEXEMPT_PLAYER));
			}
			
			this.localExemptList.remove(playerName);
			this.localExemptList.save();
			
			NotificationManager.sendUnExemptListNotification(playerName, issuedBy, log);
		}
	}
	
	public void unExemptPlayer(String playerName, String issuedBy){
		this.unExemptPlayer(playerName, issuedBy, true);
	}
	
	public void checkExpiredTempBans(){
		for (String playerName : this.tempBannedPlayers.getKeys()){
			this.isTempBanned(playerName);
		}
	}
	
	public List<String> getLocallyBannedPlayers(){
		return new ArrayList<String>(this.locallyBannedPlayers.getAll());
	}
	
	public List<String> getGloballyBannedPlayers(){
		return new ArrayList<String>(this.globallyBannedPlayers.getAll());
	}
	
	public List<String> getTempBannedPlayers(){
		this.checkExpiredTempBans();
		
		return new ArrayList<String>(this.tempBannedPlayers.getKeys());
	}
	
	public boolean isLocallyBanned(String playerName){
		return this.locallyBannedPlayers.contains(playerName);
	}
	
	public boolean isTempBanned(String playerName){
		if (this.tempBannedPlayers.contains(playerName)){
			if (this.getTempBanRemaining(playerName) == 0){
				this.unTempBan(playerName, "expired", true);
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
