package com.minebans.bans;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.minebans.MineBans;
import com.minebans.events.PlayerBanEvent;
import com.minebans.events.PlayerGlobalBanEvent;
import com.minebans.events.PlayerLocalBanEvent;
import com.minebans.events.PlayerTempBanEvent;
import com.minebans.events.PlayerUnbanEvent;
import com.minebans.events.PlayerUnwhitelistEvent;
import com.minebans.events.PlayerWhitelistEvent;
import com.minebans.util.PlayerDataStore;
import com.minebans.util.PlayerListStore;

public class BanManager {
	
	private MineBans plugin;
	private NotificationManager notification;
	
	private PlayerListStore globallyBannedPlayers;
	private PlayerListStore locallyBannedPlayers;
	private PlayerDataStore tempBannedPlayers;
	private PlayerListStore localWhitelist;
	
	public BanManager(MineBans plugin){
		this.plugin = plugin;
		this.notification = new NotificationManager(plugin);
		
		String pluginFolder = plugin.getDataFolder().getAbsolutePath();
		
		this.globallyBannedPlayers = new PlayerListStore(new File(pluginFolder + File.separator + "globally-banned-players.txt"));
		this.locallyBannedPlayers = new PlayerListStore(new File(pluginFolder + File.separator + "locally-banned-players.txt"));
		this.tempBannedPlayers = new PlayerDataStore(new File(pluginFolder + File.separator + "temp-banned-players.txt"));
		this.localWhitelist = new PlayerListStore(new File(pluginFolder + File.separator + "ban-exceptions.txt"));
		
		this.globallyBannedPlayers.load();
		this.locallyBannedPlayers.load();
		this.tempBannedPlayers.load();
		this.localWhitelist.load();
	}
	
	public void locallyBanPlayer(String playerName, boolean log, boolean notify){
		Player player = plugin.getServer().getPlayer(playerName);
		
		if (player != null){
			player.kickPlayer("You have been banned from this server.");
		}
		
		this.locallyBannedPlayers.add(playerName);
		this.locallyBannedPlayers.save();
		
		plugin.pluginManager.callEvent(new PlayerLocalBanEvent(playerName));
		plugin.pluginManager.callEvent(new PlayerBanEvent(playerName, BanType.LOCAL));
		
		if (notify){
			this.notification.sendBanNotification(playerName, log);
		}
	}
	
	public void locallyBanPlayer(String playerName, boolean log){
		this.locallyBanPlayer(playerName, log, true);
	}
	
	public void locallyBanPlayer(String playerName){
		this.locallyBanPlayer(playerName, true, true);
	}
	
	public void globallyBanPlayer(String playerName, String issuedBy, BanReason reason, boolean log, boolean notify){
		Player player = plugin.getServer().getPlayer(playerName);
		
		if (player != null){
			player.kickPlayer("You have been banned from this server.");
		}
		
		this.globallyBannedPlayers.add(playerName);
		this.globallyBannedPlayers.save();
		
		plugin.api.banPlayer(playerName, issuedBy, reason, reason.getEvidenceCollector().collect(playerName));
		
		plugin.pluginManager.callEvent(new PlayerGlobalBanEvent(playerName, reason));
		plugin.pluginManager.callEvent(new PlayerBanEvent(playerName, BanType.GLOBAL));
		
		if (notify){
			this.notification.sendBanNotification(playerName, reason, log);
		}
	}
	
	public void globallyBanPlayer(String playerName, String issuedBy, BanReason reason, boolean log){
		this.globallyBanPlayer(playerName, issuedBy, reason, log, true);
	}
	
	public void globallyBanPlayer(String playerName, String issuedBy, BanReason reason){
		this.globallyBanPlayer(playerName, issuedBy, reason, true, true);
	}
	
	public void tempBanPlayer(String playerName, int banDuration, boolean log, boolean notify){
		Player player = plugin.getServer().getPlayer(playerName);
		
		if (player != null){
			double days = Math.floor(banDuration / 86400);
			double hours = Math.round((banDuration - (days * 86400)) / 3600);
			
			player.kickPlayer("You have been banned from this server for " + days + " " + ((days == 1D) ? "day" : "days") + " and " + hours + " " + ((hours == 1D) ? "hour" : "hours") + ".");
		}
		
		this.tempBannedPlayers.add(playerName, new Long((System.currentTimeMillis() / 1000) + banDuration).toString());
		this.tempBannedPlayers.save();
		
		plugin.pluginManager.callEvent(new PlayerTempBanEvent(playerName, banDuration));
		plugin.pluginManager.callEvent(new PlayerBanEvent(playerName, BanType.TEMP));
		
		if (notify){
			this.notification.sendBanNotification(playerName, banDuration, log);
		}
	}
	
	public void tempBanPlayer(String playerName, int banDuration, boolean log){
		this.tempBanPlayer(playerName, banDuration, log, true);
	}
	
	public void tempBanPlayer(String playerName, int banDuration){
		this.tempBanPlayer(playerName, banDuration, true, true);
	}
	
	// NOTE: Called on line 153 of APIInterface.java to remove the player only if the request completed correctly.
	public void unbanPlayerAPICallback(String playerName){
		if (this.globallyBannedPlayers.contains(playerName)){
			this.globallyBannedPlayers.remove(playerName);
			this.globallyBannedPlayers.save();
			
			plugin.pluginManager.callEvent(new PlayerUnbanEvent(playerName, BanType.GLOBAL));
			this.notification.sendUnbanNotification(playerName, true);
		}
	}
	
	public void unbanPlayer(String playerName, String issuedBy, boolean log){
		if (this.isGloballyBanned(playerName)){
			plugin.api.unbanPlayer(playerName, issuedBy);
		}else if (this.isLocallyBanned(playerName)){
			this.locallyBannedPlayers.remove(playerName);
			this.locallyBannedPlayers.save();
			
			plugin.pluginManager.callEvent(new PlayerUnbanEvent(playerName, BanType.LOCAL));
			this.notification.sendUnbanNotification(playerName, log);
		}else if (this.isTempBanned(playerName)){
			this.tempBannedPlayers.remove(playerName);
			this.tempBannedPlayers.save();
			
			plugin.pluginManager.callEvent(new PlayerUnbanEvent(playerName, BanType.TEMP));
			this.notification.sendUnbanNotification(playerName, log);
		}
	}
	
	public void unbanPlayer(String playerName, String issuedBy){
		this.unbanPlayer(playerName, issuedBy, true);
	}
	
	public void whiteListPlayer(String playerName, boolean log){
		this.localWhitelist.add(playerName);
		
		plugin.pluginManager.callEvent(new PlayerWhitelistEvent(playerName));
	}
	
	public void whiteListPlayer(String playerName){
		this.whiteListPlayer(playerName, true);
	}
	
	public void unWhiteListPlayer(String playerName, boolean log){
		Player player = plugin.getServer().getPlayer(playerName);
		
		if (player != null){
			player.kickPlayer("You have been removed from the whitelist.");
		}
		
		this.localWhitelist.remove(playerName);
		
		plugin.pluginManager.callEvent(new PlayerUnwhitelistEvent(playerName));
	}
	
	public void unWhiteListPlayer(String playerName){
		this.unWhiteListPlayer(playerName, true);
	}
	
	public boolean isLocallyBanned(String playerName){
		return this.locallyBannedPlayers.contains(playerName);
	}
	
	public boolean isTempBanned(String playerName){
		if (this.tempBannedPlayers.contains(playerName)){
			System.out.println(Long.parseLong(this.tempBannedPlayers.getData(playerName)));
			
			if (Long.parseLong(this.tempBannedPlayers.getData(playerName)) <= (System.currentTimeMillis() / 1000)){
				this.unbanPlayer(playerName, "CONSOLE");
				return false;
			}else{
				return true;
			}
		}
		
		return false;
	}
	
	public Integer getTempBanRemaining(String playerName){
		if (this.isBanned(playerName) == false){
			return null;
		}
		
		return Math.max((int) (Long.parseLong(this.tempBannedPlayers.getData(playerName)) - (System.currentTimeMillis() / 1000)), 0);
	}
	
	public boolean isGloballyBanned(String playerName){
		return this.globallyBannedPlayers.contains(playerName);
	}
	
	public boolean isBanned(String playerName){
		return (this.isGloballyBanned(playerName) || this.isLocallyBanned(playerName) || this.isTempBanned(playerName));
	}
	
	public boolean isWhitelisted(String playerName){
		return this.localWhitelist.contains(playerName);
	}
	
	public ArrayList<String> getLocallyBannedPlayers(){
		return this.locallyBannedPlayers.getPlayerNames();
	}
	
}
