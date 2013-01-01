package com.minebans.minebans;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import uk.co.jacekk.bukkit.baseplugin.v7.event.BaseListener;

public class PlayerJoinListener extends BaseListener<MineBans> {
	
	public PlayerJoinListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		
		if (Permission.ALERT_ON_UPDATE.has(player)){
			plugin.server.dispatchCommand(player, "minebans update");
		}
	}
	
}
