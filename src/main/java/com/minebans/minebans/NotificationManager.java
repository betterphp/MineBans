package com.minebans.minebans;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.jacekk.bukkit.baseplugin.v9.util.ChatUtils;

import com.minebans.minebans.api.data.PlayerBansData;
import com.minebans.minebans.api.data.PlayerInfoData;
import com.minebans.minebans.api.data.PlayerJoinInfoData;
import com.minebans.minebans.bans.BanReason;

public class NotificationManager {
	
	public static String parseNotification(String notification, String playerName, String adminName, BanReason reason, int duration){
		if (playerName != null){
			notification = notification.replaceAll("%player_name%", playerName);
		}
		
		if (adminName != null){
			notification = notification.replaceAll("%admin_name%", adminName);
		}
		
		if (reason != null){
			notification = notification.replaceAll("%reason%", reason.getDescription());
			notification = notification.replaceAll("%short_reason%", reason.getShortDescription());
		}
		
		if (duration > 0){
			int days = (int) Math.floor(duration / 86400.0d);
			int hours = (int) Math.round((duration - (days * 86400.0d)) / 3600.0d);
			
			notification = notification.replaceAll("%duration%", (days > 0) ? Integer.toString(days) + " days" : Integer.toString(hours) + " hours");
		}
		
		return ChatUtils.parseFormattingCodes(notification);
	}
	
	public static void sendBanNotification(String playerName, String issuedBy, boolean log){
		String message = parseNotification(MineBans.INSTANCE.config.getString(Config.MESSAGE_LOCAL_BAN_SERVER), playerName, issuedBy, null, 0);
		
		for (Player player : Permission.ALERT_ON_BAN.getPlayersWith()){
			player.sendMessage(MineBans.INSTANCE.formatMessage(message));
		}
		
		if (log){
			MineBans.INSTANCE.log.info(message);
		}
	}
	
	public static void sendBanNotification(String playerName, String issuedBy, BanReason reason, boolean log){
		String message = parseNotification(MineBans.INSTANCE.config.getString(Config.MESSAGE_GLOBAL_BAN_SERVER), playerName, issuedBy, reason, 0);
		
		for (Player player : Permission.ALERT_ON_BAN.getPlayersWith()){
			player.sendMessage(MineBans.INSTANCE.formatMessage(message));
		}
		
		if (log){
			MineBans.INSTANCE.log.info(message);
		}
	}
	
	public static void sendBanNotification(String playerName, String issuedBy, int duration, boolean log){
		String message = parseNotification(MineBans.INSTANCE.config.getString(Config.MESSAGE_TEMP_BAN_SERVER), playerName, issuedBy, null, duration);
		
		for (Player player : Permission.ALERT_ON_BAN.getPlayersWith()){
			player.sendMessage(MineBans.INSTANCE.formatMessage(message));
		}
		
		if (log){
			MineBans.INSTANCE.log.info(message);
		}
	}
	
	public static void sendUnbanNotification(String playerName, String issuedBy, boolean log){
		String message = parseNotification(MineBans.INSTANCE.config.getString(Config.MESSAGE_UNBAN_SERVER), playerName, issuedBy, null, 0);
		
		for (Player player : Permission.ALERT_ON_UNBAN.getPlayersWith()){
			player.sendMessage(message);
		}
		
		if (log){
			MineBans.INSTANCE.log.info(message);
		}
	}
	
	public static void sendExemptListNotification(String playerName, String issuedBy, boolean log){
		String message = parseNotification(MineBans.INSTANCE.config.getString(Config.MESSAGE_EXEMPT_SERVER), playerName, issuedBy, null, 0);
		
		for (Player player : Permission.ALERT_ON_EXEMPT.getPlayersWith()){
			player.sendMessage(MineBans.INSTANCE.formatMessage(message));
		}
		
		if (log){
			MineBans.INSTANCE.log.info(message);
		}
	}
	
	public static void sendUnExemptListNotification(String playerName, String issuedBy, boolean log){
		String message = parseNotification(MineBans.INSTANCE.config.getString(Config.MESSAGE_UNEXEMPT_SERVER), playerName, issuedBy, null, 0);
		
		for (Player player : Permission.ALERT_ON_UNEXEMPT.getPlayersWith()){
			player.sendMessage(MineBans.INSTANCE.formatMessage(message));
		}
		
		if (log){
			MineBans.INSTANCE.log.info(message);
		}
	}
	
	public static void sendKickNotification(String playerName, String issuedBy, boolean log){
		String message = parseNotification(MineBans.INSTANCE.config.getString(Config.MESSAGE_KICK_SERVER), playerName, issuedBy, null, 0);
		
		for (Player player : Permission.ALERT_ON_KICK.getPlayersWith()){
			player.sendMessage(MineBans.INSTANCE.formatMessage(message));
		}
		
		if (log){
			MineBans.INSTANCE.log.info(message);
		}
	}
	
	public static void sendCompactJoinNotification(String playerName, PlayerJoinInfoData playerData){
		PlayerBansData bansData = playerData.getBansData();
		
		Long totalBans = bansData.getTotal();
		Long last24 = bansData.getLast24();
		
		if (totalBans > 0L || last24 > 0L){
			for (Player player : Permission.ALERT_ON_JOIN.getPlayersWith()){
				player.sendMessage(MineBans.INSTANCE.formatMessage(ChatColor.GREEN + "Summary for " + playerName + " Total: " + ((totalBans <= 5L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + totalBans) + " Last 24 Hours: " + ((last24 == 0L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + last24);
			}
		}
	}
	
	public static void sendJoinNotification(String playerName, String ipAddress, PlayerJoinInfoData playerData){
		if (playerData == null){
			return;
		}
		
		PlayerBansData bansData = playerData.getBansData();
		PlayerInfoData infoData = playerData.getInfoData();
		
		Long totalBans = bansData.getTotal();
		Long last24 = bansData.getLast24();
		Long removed = bansData.getRemoved();
		boolean compromised = infoData.isKnownCompromised();
		
		ArrayList<String> bannedAlts = MineBans.INSTANCE.bannedIPs.get(ipAddress);
		
		if (totalBans > 0L || last24 > 0L || (removed > 0L && !MineBans.INSTANCE.config.getBoolean(Config.IGNORE_REMOVED_BANS)) || compromised || (bannedAlts != null && !bannedAlts.isEmpty())){
			for (Player player : Permission.ALERT_ON_JOIN.getPlayersWith()){
				player.sendMessage(MineBans.INSTANCE.formatMessage(ChatColor.GREEN + "Summary for " + playerName));
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
