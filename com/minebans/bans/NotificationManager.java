package com.minebans.bans;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.minebans.MineBans;

public class NotificationManager {
	
	private MineBans plugin;
	
	public NotificationManager(MineBans plugin){
		this.plugin = plugin;
	}
	
	public void sendBanNotification(String playerName, boolean log){
		for (Player player : plugin.server.getOnlinePlayers()){
			if (player.hasPermission("minebans.alert.onban")){
				player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been banned from the server."));
			}
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been banned from the server.", false));
		}
	}
	
	public void sendBanNotification(String playerName, BanReason reason, boolean log){
		for (Player player : plugin.server.getOnlinePlayers()){
			if (player.hasPermission("minebans.alert.onban")){
				player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been banned from the server."));
				player.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Reason: " + reason.getDescription()));
			}
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been banned from the server.", false));
			plugin.log.info(plugin.formatMessage("Reason: " + reason.getDescription(), false));
		}
	}
	
	public void sendBanNotification(String playerName, int banDuration, boolean log){
		double days = Math.floor(banDuration / 86400);
		double hours = Math.round((banDuration - (days * 86400)) / 3600);
		
		for (Player player : plugin.server.getOnlinePlayers()){
			if (player.hasPermission("minebans.alert.onban")){
				player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been banned from the server for " + days + " " + ((days == 1D) ? "day" : "days") + " and " + hours + " " + ((hours == 1D) ? "hour" : "hours") + "."));
			}
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been banned from the server for " + days + " " + ((days == 1D) ? "day" : "days") + " and " + hours + " " + ((hours == 1D) ? "hour" : "hours") + ".", false));
		}
	}
	
	public void sendUnbanNotification(String playerName, boolean log){
		for (Player player : plugin.server.getOnlinePlayers()){
			if (player.hasPermission("minebans.alert.onunban")){
				player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been unbanned from the server."));
			}
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been unbanned from the server.", false));
		}
	}
	
	public void sendWhiteListNotification(String playerName, boolean log){
		for (Player player : plugin.server.getOnlinePlayers()){
			if (player.hasPermission("minebans.alert.onunban")){
				player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been added to the whitelsit."));
			}
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been added to the whitelist.", false));
		}
	}
	
	public void sendUnWhiteListNotification(String playerName, boolean log){
		for (Player player : plugin.server.getOnlinePlayers()){
			if (player.hasPermission("minebans.alert.onunban")){
				player.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been removed from the whitelsit."));
			}
		}
		
		if (log){
			plugin.log.info(plugin.formatMessage(playerName + " has been removed from the whitelist.", false));
		}
	}
	
}
