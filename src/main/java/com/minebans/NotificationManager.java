package com.minebans;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.minebans.api.data.PlayerBansData;
import com.minebans.api.data.PlayerInfoData;
import com.minebans.api.data.PlayerJoinInfoData;
import com.minebans.bans.BanReason;

public class NotificationManager {
	
	private MineBans plugin;
	
	public NotificationManager(MineBans plugin){
		this.plugin = plugin;
	}
	
	public void sendBanNotification(String playerName, boolean log){
		for (Player player : Permission.ALERT_ON_BAN.getPlayersWith()){
			player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been banned from the server."));
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been banned from the server.", false));
		}
	}
	
	public void sendBanNotification(String playerName, BanReason reason, boolean log){
		for (Player player : Permission.ALERT_ON_BAN.getPlayersWith()){
			player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been banned from the server."));
			player.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Reason: " + reason.getDescription()));
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been banned from the server.", false));
			plugin.log.info(plugin.formatMessage("Reason: " + reason.getDescription(), false));
		}
	}
	
	public void sendBanNotification(String playerName, int banDuration, boolean log){
		double days = Math.floor(banDuration / 86400);
		double hours = Math.round((banDuration - (days * 86400)) / 3600);
		
		for (Player player : Permission.ALERT_ON_BAN.getPlayersWith()){
			player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been banned from the server for " + days + " " + ((days == 1D) ? "day" : "days") + " and " + hours + " " + ((hours == 1D) ? "hour" : "hours") + "."));
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been banned from the server for " + days + " " + ((days == 1D) ? "day" : "days") + " and " + hours + " " + ((hours == 1D) ? "hour" : "hours") + ".", false));
		}
	}
	
	public void sendUnbanNotification(String playerName, boolean log){
		for (Player player : Permission.ALERT_ON_UNBAN.getPlayersWith()){
			player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been unbanned from the server."));
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been unbanned from the server.", false));
		}
	}
	
	public void sendExemptListNotification(String playerName, boolean log){
		for (Player player : Permission.ALERT_ON_EXEMPT.getPlayersWith()){
			player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been added to the exempt list."));
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been added to the exempt list.", false));
		}
	}
	
	public void sendUnExemptListNotification(String playerName, boolean log){
		for (Player player : Permission.ALERT_ON_UNEXEMPT.getPlayersWith()){
			player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been removed from the exempt list."));
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been removed from the exempt list.", false));
		}
	}
	
	public void sendKickNotification(String playerName, boolean log){
		for (Player player : Permission.ALERT_ON_KICK.getPlayersWith()){
			player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been kicked from the server."));
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been kicked from the server.", false));
		}
	}
	
	public void sendJoinNotification(String playerName, String ipAddress, PlayerJoinInfoData playerData){
		if (playerData == null){
			return;
		}
		
		PlayerBansData bansData = playerData.getBansData();
		PlayerInfoData infoData = playerData.getInfoData();
		
		Long totalBans = bansData.getTotal();
		Long last24 = bansData.getLast24();
		Long removed = bansData.getRemoved();
		boolean compromised = infoData.isKnownCompromised();
		
		ArrayList<String> bannedAlts = plugin.bannedIPs.get(ipAddress);
		
		if (plugin.config.getBoolean(Config.USE_COMPACT_JOIN_INFO)){
			if (totalBans > 0L || last24 > 0L){
				for (Player player : Permission.ALERT_ON_JOIN.getPlayersWith()){
					player.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Summary for " + playerName + " Total: " + ((totalBans <= 5L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + totalBans) + " Last 24 Hours: " + ((last24 == 0L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + last24);
				}
			}
		}else{
			if (totalBans > 0L || last24 > 0L || removed > 0L || compromised || (bannedAlts != null && !bannedAlts.isEmpty())){
				for (Player player : Permission.ALERT_ON_JOIN.getPlayersWith()){
					player.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Summary for " + playerName));
					player.sendMessage(ChatColor.GREEN + "Total bans on record: " + ((totalBans <= 5L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + totalBans);
					player.sendMessage(ChatColor.GREEN + "Bans in the last 24 hours: " + ((last24 == 0L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + last24);
					player.sendMessage(ChatColor.GREEN + "Bans that have been removed: " + ((removed <= 10L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + removed);
					player.sendMessage(ChatColor.GREEN + "Known compromised account: " + ((compromised) ? ChatColor.DARK_RED + "Yes" : ChatColor.GREEN + "No"));
					
					if (bannedAlts != null && !bannedAlts.isEmpty()){
						player.sendMessage(ChatColor.GREEN + "Recently banned players with the same IP:");
						
						for (String banned : bannedAlts){
							player.sendMessage(ChatColor.GREEN + "  - " + banned);
						}
					}
				}
			}
		}
	}
	
}
