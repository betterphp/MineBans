package com.minebans.bans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.minebans.MineBans;
import com.minebans.events.PlayerBanEvent;
import com.minebans.events.PlayerExemptEvent;
import com.minebans.events.PlayerGlobalBanEvent;
import com.minebans.events.PlayerLocalBanEvent;
import com.minebans.events.PlayerTempBanEvent;
import com.minebans.events.PlayerUnbanEvent;
import com.minebans.events.PlayerUnExemptEvent;
import com.minebans.util.PlayerDataStore;
import com.minebans.util.PlayerListStore;

public class BanManager {
	
	private MineBans plugin;
	
	private PlayerListStore globallyBannedPlayers;
	private PlayerListStore locallyBannedPlayers;
	private PlayerDataStore tempBannedPlayers;
	private PlayerListStore localExemptList;
	
	public BanManager(MineBans plugin){
		this.plugin = plugin;
		
		String pluginFolder = plugin.getDataFolder().getAbsolutePath();
		
		this.globallyBannedPlayers = new PlayerListStore(new File(pluginFolder + File.separator + "globally-banned-players.txt"));
		this.locallyBannedPlayers = new PlayerListStore(new File(pluginFolder + File.separator + "locally-banned-players.txt"));
		this.tempBannedPlayers = new PlayerDataStore(new File(pluginFolder + File.separator + "temp-banned-players.txt"));
		this.localExemptList = new PlayerListStore(new File(pluginFolder + File.separator + "ban-exceptions.txt"));
		
		this.globallyBannedPlayers.load();
		this.locallyBannedPlayers.load();
		this.tempBannedPlayers.load();
		this.localExemptList.load();
	}
	
	public void kickPlayer(String playerName, boolean log){
		Player player = plugin.server.getPlayer(playerName);
		
		if (player != null){
			player.kickPlayer("You have been kicked from the server.");
			
			plugin.notificationManager.sendKickNotification(playerName, log);
		}
	}
	
	public void locallyBanPlayer(String playerName, boolean log, boolean notify){
		Player player = plugin.getServer().getPlayer(playerName);
		
		if (player != null){
			player.kickPlayer("You have been banned from this server");
		}
		
		this.locallyBannedPlayers.add(playerName);
		this.locallyBannedPlayers.save();
		
		plugin.pluginManager.callEvent(new PlayerLocalBanEvent(playerName));
		plugin.pluginManager.callEvent(new PlayerBanEvent(playerName, BanType.LOCAL));
		
		if (notify){
			plugin.notificationManager.sendBanNotification(playerName, log);
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
			player.kickPlayer("You have been banned from this server (appeal at minebans.com)");
		}
		
		this.globallyBannedPlayers.add(playerName);
		this.globallyBannedPlayers.save();
		
		plugin.api.banPlayer(playerName, issuedBy, reason, plugin.evidenceManager.collectFor(reason, playerName));
		
		plugin.pluginManager.callEvent(new PlayerGlobalBanEvent(playerName, reason));
		plugin.pluginManager.callEvent(new PlayerBanEvent(playerName, BanType.GLOBAL));
		
		if (notify){
			plugin.notificationManager.sendBanNotification(playerName, reason, log);
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
			
			player.kickPlayer("You have been banned from this server for " + days + " " + ((days == 1D) ? "day" : "days") + " and " + hours + " " + ((hours == 1D) ? "hour" : "hours"));
		}
		
		this.tempBannedPlayers.add(playerName, new Long((System.currentTimeMillis() / 1000) + banDuration).toString());
		this.tempBannedPlayers.save();
		
		plugin.pluginManager.callEvent(new PlayerTempBanEvent(playerName, banDuration));
		plugin.pluginManager.callEvent(new PlayerBanEvent(playerName, BanType.TEMP));
		
		if (notify){
			plugin.notificationManager.sendBanNotification(playerName, banDuration, log);
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
			this.globallyBannedPlayers.remove(playerName);
			this.globallyBannedPlayers.save();
			
			plugin.pluginManager.callEvent(new PlayerUnbanEvent(playerName, BanType.GLOBAL));
			plugin.notificationManager.sendUnbanNotification(playerName, true);
		}
	}
	
	public void unGlobalBan(String playerName, String issuedBy){
		plugin.api.unbanPlayer(playerName, issuedBy);
	}
	
	public void unLocalBan(String playerName, boolean log){
		this.locallyBannedPlayers.remove(playerName);
		this.locallyBannedPlayers.save();
		
		plugin.pluginManager.callEvent(new PlayerUnbanEvent(playerName, BanType.LOCAL));
		plugin.notificationManager.sendUnbanNotification(playerName, log);
	}
	
	public void unTempBan(String playerName, boolean log){
		this.tempBannedPlayers.remove(playerName);
		this.tempBannedPlayers.save();
		
		plugin.pluginManager.callEvent(new PlayerUnbanEvent(playerName, BanType.TEMP));
		plugin.notificationManager.sendUnbanNotification(playerName, log);
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
		this.localExemptList.add(playerName);
		
		plugin.pluginManager.callEvent(new PlayerExemptEvent(playerName));
		plugin.notificationManager.sendExemptListNotification(playerName, log);
	}
	
	public void exemptPlayer(String playerName){
		this.exemptPlayer(playerName, true);
	}
	
	public void unExemptPlayer(String playerName, boolean log){
		Player player = plugin.getServer().getPlayer(playerName);
		
		if (player != null){
			player.kickPlayer("You have been removed from the exempt list.");
		}
		
		this.localExemptList.remove(playerName);
		
		plugin.pluginManager.callEvent(new PlayerUnExemptEvent(playerName));
		plugin.notificationManager.sendUnExemptListNotification(playerName, log);
	}
	
	public void unExemptPlayer(String playerName){
		this.unExemptPlayer(playerName, true);
	}
	
	public List<String> getLocallyBannedPlayers(){
		return new ArrayList<String>(this.locallyBannedPlayers.getPlayerNames());
	}
	
	public List<String> getGloballyBannedPlayers(){
		return new ArrayList<String>(this.globallyBannedPlayers.getPlayerNames());
	}
	
	public List<String> getTempBannedPlayers(){
		return new ArrayList<String>(this.tempBannedPlayers.getPlayerNames());
	}
	
	public boolean isLocallyBanned(String playerName){
		return this.locallyBannedPlayers.contains(playerName);
	}
	
	public boolean isTempBanned(String playerName){
		if (this.tempBannedPlayers.contains(playerName)){
			if (Long.parseLong(this.tempBannedPlayers.getData(playerName)) <= (System.currentTimeMillis() / 1000)){
				this.unTempBan(playerName, true);
				return false;
			}else{
				return true;
			}
		}
		
		return false;
	}
	
	public Integer getTempBanRemaining(String playerName){
		if (this.isTempBanned(playerName) == false){
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
	
	public boolean isExempt(String playerName){
		return this.localExemptList.contains(playerName);
	}
	
}
