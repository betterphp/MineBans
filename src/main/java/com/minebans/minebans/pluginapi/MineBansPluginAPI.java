package com.minebans.minebans.pluginapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.PlayerBansCallback;
import com.minebans.minebans.api.request.PlayerBansRequest;
import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.bans.BanType;

public class MineBansPluginAPI {
	
	private MineBans plugin;
	private String pluginName;
	
	private static HashMap<Plugin, MineBansPluginAPI> handles = new HashMap<Plugin, MineBansPluginAPI>();
	
	private MineBansPluginAPI(MineBans mineBans, Plugin plugin){
		this.plugin = mineBans;
		this.pluginName = plugin.getDescription().getName();
	}
	
	/**
	 * Gets the instance of the plugin API for a specific plugin.
	 * 
	 * @param mineBans	The main MineBans plugin
	 * @param plugin	The plugin that this instance belongs too.
	 * @return			The instance of the plugin API.
	 */
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
	
	/**
	 * @return	The version of the MineBans plugin.
	 */
	public String getVersion(){
		return plugin.getVersion();
	}
	
	/**
	 * Kicks a player from the server with the specified reason.
	 * 
	 * @param player	The player to be kicked.
	 * @param message	The reason that the player is being kicked.
	 * @return			True is the player was online and was kicked, false if not.
	 */
	public boolean kickPlayer(Player player, String message){
		if (!player.isOnline() || player == null){
			return false;
		}
		
		player.kickPlayer(message);
		plugin.log.info(player.getName() + " was kicked from the server by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	/**
	 * Kicks a player (by name) from the server with the specified reason.
	 * 
	 * @param playerName	The name of the player to be kicked.
	 * @param message		The reason that the player is being kicked.
	 * @return				True is the player was online and was kicked, false if not.
	 */
	public boolean kickPlayer(String playerName, String message){
		Player player = plugin.getServer().getPlayer(playerName);
		return this.kickPlayer(player, message);
	}
	
	/**
	 * Kicks a player from the server with the default reason.
	 * 
	 * @param player	The player to be kicked.
	 * @return			True is the player was online and was kicked, false if not.
	 */
	public boolean kickPlayer(Player player){
		return this.kickPlayer(player, "You have been kicked from the server.");
	}
	
	/**
	 * Kicks a player from the server with the default reason.
	 * 
	 * @param playerName	The name of the player to be kicked.
	 * @return				True is the player was online and was kicked, false if not.
	 */
	public boolean kickPlayer(String playerName){
		return this.kickPlayer(playerName, "You have been kicked from the server.");
	}
	
	/**
	 * Checks to see if a player is banned,
	 * 
	 * @param player	The player to check.
	 * @return			True if the player is banned, false if not.
	 */
	public boolean isBanned(Player player){
		return plugin.banManager.isBanned(player.getName());
	}
	
	/**
	 * Checks to see if a player is banned,
	 * 
	 * @param playerName	The name of the player to be checked.
	 * @return				True if the player is banned, false if not.
	 */
	public boolean isBanned(String playerName){
		return plugin.banManager.isBanned(playerName);
	}
	
	/**
	 * Checks to see if the player is exempt from being banned.
	 * 
	 * @param player	The player to check.
	 * @return			True if the player is exempt, false if not.
	 */
	public boolean isExempt(Player player){
		return this.isExempt(player.getName());
	}
	
	/**
	 * Checks to see if the player is exempt from being banned.
	 * 
	 * @param playerName	The name of the player to be checked.
	 * @return				True if the player is exempt, false if not.
	 */
	public boolean isExempt(String playerName){
		return plugin.banManager.isExempt(playerName);
	}
	
	/**
	 * Gets a list of all players that have been locally banned by this sever.
	 * 
	 * @return	The list of player names.
	 */
	public List<String> getLocallyBannedPlayers(){
		return plugin.banManager.getLocallyBannedPlayers();
	}
	
	/**
	 * Gets a list of all players that have been globally banned by this sever.
	 * 
	 * @return	The list of player names.
	 */
	public List<String> getGloballyBannedPlayers(){
		return plugin.banManager.getGloballyBannedPlayers();
	}
	
	/**
	 * Gets a list of all players that have been temporarily banned by this sever.
	 * 
	 * @return	The list of player names.
	 */
	public List<String> getTempBannedPlayers(){
		return plugin.banManager.getTempBannedPlayers();
	}
	
	/**
	 * Gets a list of all players that have been banned by this sever.
	 * 
	 * @return	The list of player names.
	 */
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
	
	/**
	 * Locally bans a player from the server.
	 * 
	 * @param playerName	The name of the player to be banned.
	 * @return				True
	 */
	public boolean locallyBanPlayer(String playerName){
		plugin.banManager.locallyBanPlayer(playerName, this.pluginName, false, false);
		plugin.log.info(playerName + " was locally banned by the plugin '" + this.pluginName + "'");
		return true;
	}
	
	/**
	 * Locally bans a player from the server.
	 * 
	 * @param player	The player to be banned.
	 * @return			True
	 */
	public boolean locallyBanPlayer(Player player){
		return this.locallyBanPlayer(player.getName());
	}
	
	/**
	 * Globally bans a player from the server.
	 * 
	 * @param playerName	The name of the player to be banned.
	 * @param issuedBy		The name of the player to be made responsible for this ban, they must have an account at minebans.com and be listed as a moderator for this server.
	 * @param reason		The {@link BanReason} that this player is being banned.
	 * @return				True on success or false on failure.
	 */
	public boolean globallyBanPlayer(String playerName, String issuedBy, BanReason reason){
		if (!plugin.seenPlayers.contains(playerName.toLowerCase()) && !plugin.getServer().getOfflinePlayer(playerName).hasPlayedBefore()){
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
	
	/**
	 * Globally bans a player from this server.
	 * 
	 * @param player	The player to be banned.
	 * @param issuedBy	The name of the player to be made responsible for this ban, they must have an account at minebans.com and be listed as a moderator for this server.
	 * @param reason	The {@link BanReason} that this player is being banned.
	 * @return			True on success or false on failure.
	 */
	public boolean globallyBanPlayer(Player player, String issuedBy, BanReason reason){
		return this.globallyBanPlayer(player.getName(), issuedBy, reason);
	}
	
	/**
	 * Temporarily bans a player from the server.
	 * 
	 * @param playerName	The name of the player to be banned.
	 * @param banDuration	The length of time (in minutes) that the player should be banned for.
	 * @return				True on success or false on failure.
	 */
	public boolean tempBanPlayer(String playerName, int banDuration){
		if (banDuration <= 0 || banDuration > 604800){
			return false;
		}
		
		plugin.banManager.tempBanPlayer(playerName, this.pluginName, banDuration, false, false);
		plugin.log.info(playerName + " was temporarily banned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	/**
	 * Temporarily bans a player from the server.
	 * 
	 * @param player		The player to be banned.
	 * @param banDuration	The length of time (in minutes) that the player should be banned for.
	 * @return				True on success or false on failure.
	 */
	public boolean tempBanPlayer(Player player, int banDuration){
		return this.tempBanPlayer(player.getName(), banDuration);
	}
	
	/**
	 * Removed a local ban for a player.
	 * 
	 * @param playerName	The name of the player to be unbanned.
	 * @return				True on success or false on failure.
	 */
	public boolean unLocalBanPlayer(String playerName){
		if (!plugin.banManager.isLocallyBanned(playerName)){
			return false;
		}
		
		plugin.banManager.unLocalBan(playerName, this.pluginName, false);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	/**
	 * Removed a local ban for a player.
	 * 
	 * @param player	The player to be unbanned.
	 * @return			True on success or false on failure.
	 */
	public boolean unLocalBanPlayer(Player player){
		return this.unLocalBanPlayer(player.getName());
	}
	
	/**
	 * Removes a global ban for a player.
	 * 
	 * @param playerName	The name of the player to be unbanned.
	 * @param issuedBy		The name of the player to be made responsible for this ban, they must have an account at minebans.com and be listed as a moderator for this server.
	 * @return				True on success or false on failure.
	 */
	public boolean unGlobalBanPlayer(String playerName, String issuedBy){
		if (!plugin.banManager.isGloballyBanned(playerName)){
			return false;
		}
		
		plugin.banManager.unGlobalBan(playerName, issuedBy);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	
	/**
	 * Removes a global ban for a player.
	 * 
	 * @param player		The player to be unbanned.
	 * @param issuedBy		The name of the player to be made responsible for this ban, they must have an account at minebans.com and be listed as a moderator for this server.
	 * @return				True on success or false on failure.
	 */
	public boolean unGlobalBanPlayer(Player player, String issuedBy){
		return this.unGlobalBanPlayer(player.getName(), issuedBy);
	}
	
	/**
	 * Removes a temporary ban for a player.
	 * 
	 * @param playerName	The name of the player to be unbanned.
	 * @return				True on success or false on failure.
	 */
	public boolean unTempBanPlayer(String playerName){
		if (!plugin.banManager.isTempBanned(playerName)){
			return false;
		}
		
		plugin.banManager.unTempBan(playerName, this.pluginName, false);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	/**
	 * Removes a temporary ban for a player.
	 * 
	 * @param player	The player to be unbanned.
	 * @return			True on success or false on failure.
	 */
	public boolean unTempBanPlayer(Player player){
		return this.unTempBanPlayer(player.getName());
	}
	
	/**
	 * Removes any bans that a player has,
	 * 
	 * @param playerName	The name of the player to be unbanned.
	 * @param issuedBy		The name of the player to be made responsible for this ban, they must have an account at minebans.com and be listed as a moderator for this server.
	 * @return				True on success or false on failure.
	 */
	public boolean unBanPlayer(String playerName, String issuedBy){
		if (!this.isBanned(playerName)){
			return false;
		}
		
		plugin.banManager.unbanPlayer(playerName, issuedBy, false);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	/**
	 * Removes any bans that a player has,
	 * 
	 * @param player	The player to be unbanned.
	 * @param issuedBy	The name of the player to be made responsible for this ban, they must have an account at minebans.com and be listed as a moderator for this server.
	 * @return			True on success or false on failure.
	 */
	public boolean unBanPlayer(Player player, String issuedBy){
		return this.unBanPlayer(player.getName(), issuedBy);
	}
	
	/**
	 * Makes a player exempt from being banned and any join checks.
	 * 
	 * @param playerName	The name of the player to be made exempt.
	 * @return				True.
	 */
	public boolean exemptPlayer(String playerName){
		plugin.banManager.exemptPlayer(playerName, this.pluginName, false);
		plugin.log.info(playerName + " was added to the exempt list by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	/**
	 * Makes a player exempt from being banned and any join checks.
	 * 
	 * @param player	The player to be made exempt.
	 * @return			True.
	 */
	public boolean exemptPlayer(Player player){
		return this.exemptPlayer(player.getName());
	}
	
	/**
	 * Removes an exemption for a player.
	 * 
	 * @param playerName	The name of the player.
	 * @return				True on success or false on failure.
	 */
	public boolean unExemptPlayer(String playerName){
		if (!this.isExempt(playerName)){
			return false;
		}
		
		plugin.banManager.unExemptPlayer(playerName, this.pluginName, false);
		plugin.log.info(playerName + " was removed from thes exempt list by the plugin '" + this.pluginName + "'");
		
		return true;
	}
	
	/**
	 * Removes an exemption for a player.
	 * 
	 * @param player	The player.
	 * @return			True on success or false on failure.
	 */
	public boolean unExemptPlayer(Player player){
		return this.unExemptPlayer(player.getName());
	}
	
	/**
	 * Fetches information on a players previous bans from minebans.com.
	 * 
	 * @param playerName	The name of the player to be looked up.
	 * @param issuedBy		The name of the player to be made responsible for this lookup, they must have an account at minebans.com and be listed as a moderator for this server. This can be "CONSOLE" which makes the server admin responsible. 
	 * @param callback		A {@link PlayerBansCallback} to be used to handle the response.
	 */
	public void lookupPlayer(String playerName, String issuedBy, PlayerBansCallback callback){
		(new PlayerBansRequest(plugin, issuedBy, playerName)).process(callback);
	}
	
	/**
	 * Fetches information on a players previous bans from minebans.com.
	 * 
	 * @param player	The player to be looked up.
	 * @param issuedBy	The name of the player to be made responsible for this lookup, they must have an account at minebans.com and be listed as a moderator for this server. This can be "CONSOLE" which makes the server admin responsible.
	 * @param callback	A {@link PlayerBansCallback} to be used to handle the response.
	 */
	public void lookupPlayer(Player player, String issuedBy, PlayerBansCallback callback){
		this.lookupPlayer(player.getName(), issuedBy, callback);
	}
	
}
