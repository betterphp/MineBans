package com.minebans.pluginapi;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.json.simple.parser.ParseException;

import com.minebans.MineBans;
import com.minebans.api.APIResponceCallback;
import com.minebans.api.PlayerBanData;
import com.minebans.bans.BanReason;

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
	
	public boolean locallyBanPlayer(String playerName){
		plugin.banManager.locallyBanPlayer(playerName, false, false);
		plugin.log.info(playerName + " was locally banned by the plugin '" + this.pdf.getName() + "'");
		return true;
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
	
}
