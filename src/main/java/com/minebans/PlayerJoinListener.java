package com.minebans;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import uk.co.jacekk.bukkit.baseplugin.v1.event.BaseListener;

public class PlayerJoinListener extends BaseListener<MineBans> {
	
	public PlayerJoinListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		final String playerName = player.getName();
		
		if (Permission.ALERT_ON_UPDATE.has(player)){
			plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable(){
				
				public void run(){
					if (plugin.updateChecker.updateNeeded()){
						plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
							
							public void run(){
								Player player = plugin.server.getPlayer(playerName);
								
								if (player != null){
									player.sendMessage(plugin.formatMessage(ChatColor.RED + "A new version is available, v" + plugin.updateChecker.getVersion()));
									player.sendMessage(plugin.formatMessage(ChatColor.RED + plugin.updateChecker.getLink()));
								}
							}
							
						});
					}
				}
				
			});
		}
	}
	
}
