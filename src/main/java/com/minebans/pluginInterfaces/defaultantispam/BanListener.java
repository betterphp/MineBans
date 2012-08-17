package com.minebans.pluginInterfaces.defaultantispam;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.minebans.events.PlayerBanEvent;

public class BanListener implements Listener {
	
	private DefaultAntiSpamPluginInterface collector;
	
	public BanListener(DefaultAntiSpamPluginInterface collector){
		this.collector = collector;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBan(PlayerBanEvent event){
		String playerName = event.getPlayerName();
		
		if (collector.messageCounter.containsKey(playerName)){
			collector.messageCounter.remove(playerName);
		}
		
		if (collector.maxViolationLevel.containsKey(playerName)){
			collector.maxViolationLevel.remove(playerName);
		}
	}
	
}
