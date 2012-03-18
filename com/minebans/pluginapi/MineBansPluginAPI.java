package com.minebans.pluginapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.json.simple.parser.ParseException;

import com.minebans.MineBans;
import com.minebans.api.APIResponceCallback;
import com.minebans.api.PlayerBanData;
import com.minebans.bans.BanReason;
import com.minebans.bans.BanType;

public class MineBansPluginAPI {
	
	private MineBans plugin;
	private PluginDescriptionFile pdf;
	
	public MineBansPluginAPI(MineBans minebans, Plugin plugin){
		this.plugin = minebans;
		this.pdf = plugin.getDescription();
	}
	
	public String getVersion(){
		return plugin.getVersion();
	}
	
	public boolean kickPlayer(Player player, String message){
		if (player == null){
			return false;
		}
		
		player.kickPlayer(message);
		plugin.log.info(player.getName() + " was kicked from the server by the plugin '" + this.pdf.getName() + "'");
		
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
		plugin.log.info(playerName + " was locally banned by the plugin '" + this.pdf.getName() + "'");
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
		if (plugin.server.getOfflinePlayer(playerName).hasPlayedBefore() == false){
			return false;
		}
		
		if (plugin.config.getBoolean("max-bans." + reason.getConfigKey() + ".enabled") == false){
			return false;
		}
		
		if (issuedBy.equalsIgnoreCase("console")){
			return false;
		}
		
		plugin.banManager.globallyBanPlayer(playerName, issuedBy, reason, false, false);
		plugin.log.info(playerName + " was globally banned by the plugin '" + this.pdf.getName() + "'");
		
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
		plugin.log.info(playerName + " was temporarily banned by the plugin '" + this.pdf.getName() + "'");
		
		return true;
	}
	
	public boolean tempBanPlayer(Player player, int banDuration){
		return this.tempBanPlayer(player.getName(), banDuration);
	}
	
	public boolean unLocalBanPlayer(String playerName){
		if (plugin.banManager.isLocallyBanned(playerName) == false){
			return false;
		}
		
		plugin.banManager.unLocalBan(playerName, false);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pdf.getName() + "'");
		
		return true;
	}
	
	public boolean unLocalBanPlayer(Player player){
		return this.unLocalBanPlayer(player.getName());
	}
	
	public boolean unGlobalBanPlayer(String playerName, String issuedBy){
		if (plugin.banManager.isGloballyBanned(playerName) == false){
			return false;
		}
		
		plugin.banManager.unGlobalBan(playerName, issuedBy);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pdf.getName() + "'");
		
		return true;
	}
	
	public boolean unGlobalBanPlayer(Player player, String issuedBy){
		return this.unGlobalBanPlayer(player.getName(), issuedBy);
	}
	
	public boolean unTempBanPlayer(String playerName){
		if (plugin.banManager.isTempBanned(playerName) == false){
			return false;
		}
		
		plugin.banManager.unTempBan(playerName, false);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pdf.getName() + "'");
		
		return true;
	}
	
	public boolean unTempBanPlayer(Player player){
		return this.unTempBanPlayer(player.getName());
	}
	
	public boolean unBanPlayer(String playerName, String issuedBy){
		if (this.isBanned(playerName) == false){
			return false;
		}
		
		plugin.banManager.unbanPlayer(playerName, issuedBy, false);
		plugin.log.info(playerName + " was unbanned by the plugin '" + this.pdf.getName() + "'");
		
		return true;
	}
	
	public boolean unBanPlayer(Player player, String issuedBy){
		return this.unBanPlayer(player.getName(), issuedBy);
	}
	
	public boolean exemptPlayer(String playerName){
		plugin.banManager.exemptPlayer(playerName, false);
		plugin.log.info(playerName + " was added to the exempt list by the plugin '" + this.pdf.getName() + "'");
		
		return true;
	}
	
	public boolean exemptPlayer(Player player){
		return this.exemptPlayer(player.getName());
	}
	
	public boolean unExemptPlayer(String playerName){
		if (this.isExempt(playerName) == false){
			return false;
		}
		
		plugin.banManager.unExemptPlayer(playerName, false);
		plugin.log.info(playerName + " was removed from thes exempt list by the plugin '" + this.pdf.getName() + "'");
		
		return true;
	}
	
	public boolean unExemptPlayer(Player player){
		return this.unExemptPlayer(player.getName());
	}
	
	public void lookupPlayer(String playerName, String issuedBy, final PluginAPIResponceCallback callback){
		plugin.api.lookupPlayerBans(playerName, issuedBy, new APIResponceCallback(){
			
			public void onSuccess(String responce){
				try{
					callback.onSuccess(new PlayerBanData(responce));
				}catch (ParseException e){
					callback.onFailure(e);
				}
			}
			
			public void onFailure(Exception e){
				callback.onFailure(e);
			}
			
		});
	}
	
	public void lookupPlayer(Player player, String issuedBy, PluginAPIResponceCallback callback){
		this.lookupPlayer(player.getName(), issuedBy, callback);
	}
	
}
