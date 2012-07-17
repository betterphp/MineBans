package com.minebans;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import com.minebans.api.APIResponseCallback;

import uk.co.jacekk.bukkit.baseplugin.BaseListener;

public class PlayerJoinListener extends BaseListener<MineBans> {
	
	public PlayerJoinListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		
		if (MineBansPermission.ALERT_ON_UPDATE.playerHasPermission(player)){
			plugin.api.lookupLatestVersion(new APIResponseCallback(){
				
				public void onSuccess(String response){
					if (!plugin.getVersion().equals(response)){
						player.sendMessage(plugin.formatMessage(ChatColor.RED + "A new version is available, " + response));
						player.sendMessage(plugin.formatMessage(ChatColor.RED + "Get it from dev.bukkit.org/server-mods/minebans/files/"));
					}
				}
				
				public void onFailure(Exception e){
					plugin.log.warn("Failed to fetch latest version: " + e.getMessage());
				}
				
			});
		}
	}
	
}
