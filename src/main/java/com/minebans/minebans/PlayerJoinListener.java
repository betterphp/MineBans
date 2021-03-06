package com.minebans.minebans;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import uk.co.jacekk.bukkit.baseplugin.event.BaseListener;

public class PlayerJoinListener extends BaseListener<MineBans> {
	
	public PlayerJoinListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		
		if (Permission.ALERT_ON_UPDATE.has(player)){
			plugin.getServer().dispatchCommand(player, "minebans update -q");
		}
		
		if (Permission.ADMIN_APPEALS.has(player)){
			plugin.getServer().dispatchCommand(player, "minebans appeals -q");
		}
	}
	
}
