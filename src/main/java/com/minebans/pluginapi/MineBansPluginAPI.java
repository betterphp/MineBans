package com.minebans.pluginapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.parser.ParseException;

import com.minebans.MineBans;
import com.minebans.Config;
import com.minebans.api.APIResponseCallback;
import com.minebans.api.PlayerBanData;
import com.minebans.bans.BanReason;
import com.minebans.bans.BanType;

public class MineBansPluginAPI {
	
	private MineBans plugin;
	private String pluginName;
	
	private static HashMap<Plugin, MineBansPluginAPI> handles = new HashMap<Plugin, MineBansPluginAPI>();
	
	private MineBansPluginAPI(MineBans mineBans, Plugin plugin){
		this.plugin = mineBans;
		this.pluginName = plugin.getDescription().getName();
	}
	
	public static MineBansPluginAPI getHandle(MineBans mineBans, Plugin plugin){
		if (plugin == null){
			return null;
		}
		
		MineBansPluginAPI api = handles.get(plugin);			
		
		if (api == null){
			api = new MineBansPluginAPI(mineBans, plugin);
			handles.put(plugin, api);
		}
		
		return api;
	}
	
	public String getVersion(){
		return plugin.getVersion();
	}
	
	public boolean kickPlayer(Player player, String message){
		if (player == null){
			return false;
		}
		
		player.kickPlayer(message);
		plugin.log.info(player.getName() + " was kicked from the server by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	public boolean kickPlayer(String playerName, String message){
		Player player = plugin.server.getPlayer(playerName);
		return this.kickPlayer(player, message);
	}
	
	public boolean kickPlayer(String playerName){
		return this.kickPlayer(playerName, "You have been kicked from the server.");
	}
	
	public boolean kickPlayer(Player player){
		return this.kickPlayer(player, "You have been kicked from the server.");
	}
	
	public boolean isBanned(String playerName){
		return plugin.banManager.isBanned(playerName);
	}
	
	public boolean isBanned(Player player){
		return plugin.banManager.isBanned(player.getName());
	}
	
	public boolean isExempt(String playerName){
		return plugin.banManager.isExempt(playerName);
	}
	
	public boolean isExempt(Player player){
		return this.isExempt(player.getName());
	}
	
	public boolean locallyBanPlayer(String playerName){
		plugin.banManager.locallyBanPlayer(playerName, false, false);
		plugin.log.info(playerName + " was locally banned by the plugin '" + this.pluginName + "'");
		return true;
	}
	
	public List<String> getLocallyBannedPlayers(){
		return plugin.banManager.getLocallyBannedPlayers();
	}
	
	public List<String> getGloballyBannedPlayers(){
		return plugin.banManager.getGloballyBannedPlayers();
	}
	
	public List<String> getTempBannedPlayers(){
		return plugin.banManager.getTempBannedPlayers();
	}
	
	public Map<String, BanType> getBannedPlayers(){
		HashMap<String, BanType> bans = new HashMap<String, BanType>();
		
		for (String playerName : this.getLocallyBannedPlayers()){
			bans.put(playerName, BanType.LOCAL);
		}
		
		for (String playerName : this.getGloballyBannedPlayers()){
			bans.put(playerName, BanType.GLOBAL);
		}
		
		for (String playerName : this.getTempBannedPlayers()){
			bans.put(playerName, BanType.TEMP);
		}
		
		return bans;
	}
	
	public boolean locallyBanPlayer(Player player){
		return this.locallyBanPlayer(player.getName());
	}
	
	public boolean globallyBanPlayer(String playerName, String issuedBy, BanReason reason){
		if (!plugin.seenPlayers.contains(playerName.toLowerCase()) && !plugin.server.getOfflinePlayer(playerName).hasPlayedBefore()){
			return false;
		}
		
		if (plugin.config.getBoolean(Config.getReasonEnabled(reason)) == false){
			return false;
		}
		
		if (issuedBy.equalsIgnoreCase("console")){
			return false;
		}
		
		plugin.banManager.globallyBanPlayer(playerName, issuedBy, reason, false, false);
		plugin.log.info(playerName + " was globally banned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	public boolean globallyBanPlayer(Player player, String issuedBy, BanReason reason){
		return this.globallyBanPlayer(player.getName(), issuedBy, reason);
	}
	
	public boolean tempBanPlayer(String playerName, int banDuration){
		if (banDuration <= 0 || banDuration > 604800){
			return false;
		}
		
		plugin.banManager.tempBanPlayer(playerName, banDuration, false, false);
		plugin.log.info(playerName + " was temporarily banned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	public boolean tempBanPlayer(Player player, int banDuration){
		return this.tempBanPlayer(player.getName(), banDuration);
	}
	
	public boolean unLocalBanPlayer(String playerName){
		if (!plugin.banManager.isLocallyBanned(playerName)){
			return false;
		}
		
		plugin.banManager.unLocalBan(playerName, false);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	public boolean unLocalBanPlayer(Player player){
		return this.unLocalBanPlayer(player.getName());
	}
	
	public boolean unGlobalBanPlayer(String playerName, String issuedBy){
		if (!plugin.banManager.isGloballyBanned(playerName)){
			return false;
		}
		
		plugin.banManager.unGlobalBan(playerName, issuedBy);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	public boolean unGlobalBanPlayer(Player player, String issuedBy){
		return this.unGlobalBanPlayer(player.getName(), issuedBy);
	}
	
	public boolean unTempBanPlayer(String playerName){
		if (!plugin.banManager.isTempBanned(playerName)){
			return false;
		}
		
		plugin.banManager.unTempBan(playerName, false);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	public boolean unTempBanPlayer(Player player){
		return this.unTempBanPlayer(player.getName());
	}
	
	public boolean unBanPlayer(String playerName, String issuedBy){
		if (!this.isBanned(playerName)){
			return false;
		}
		
		plugin.banManager.unbanPlayer(playerName, issuedBy, false);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	public boolean unBanPlayer(Player player, String issuedBy){
		return this.unBanPlayer(player.getName(), issuedBy);
	}
	
	public boolean exemptPlayer(String playerName){
		plugin.banManager.exemptPlayer(playerName, false);
		plugin.log.info(playerName + " was added to the exempt list by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	public boolean exemptPlayer(Player player){
		return this.exemptPlayer(player.getName());
	}
	
	public boolean unExemptPlayer(String playerName){
		if (!this.isExempt(playerName)){
			return false;
		}
		
		plugin.banManager.unExemptPlayer(playerName, false);
		plugin.log.info(playerName + " was removed from thes exempt list by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	public boolean unExemptPlayer(Player player){
		return this.unExemptPlayer(player.getName());
	}
	
	public void lookupPlayer(String playerName, String issuedBy, final PluginAPIResponseCallback callback){
		plugin.api.lookupPlayerBans(playerName, issuedBy, new APIResponseCallback(){
			
			public void onSuccess(String response){
				try{
					callback.onSuccess(new PlayerBanData(response));
				}catch (ParseException e){
					callback.onFailure(e);
				}
			}
			
			public void onFailure(Exception e){
				callback.onFailure(e);
			}
			
		});
	}
	
	public void lookupPlayer(Player player, String issuedBy, PluginAPIResponseCallback callback){
		this.lookupPlayer(player.getName(), issuedBy, callback);
	}
	
}
