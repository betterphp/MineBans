package com.minebans.pluginInterfaces.defaultantispam;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {
	
	private DefaultAntiSpamPluginInterface collector;
	
	public ChatListener(DefaultAntiSpamPluginInterface collector){
		this.collector = collector;
	}
	
	private void onPlayerMessage(String playerName, String message){
		HashMap<String, Integer> playerMessages;
		
		if (!collector.messageCounter.containsKey(playerName)){
			playerMessages = new HashMap<String, Integer>();
		}else{
			playerMessages = collector.messageCounter.get(playerName);
		}
		
		playerMessages.put(message, (playerMessages.containsKey(message)) ? playerMessages.get(message) + 1 : 1);
		
		collector.messageCounter.put(playerName, playerMessages);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		this.onPlayerMessage(event.getPlayer().getName(), event.getMessage());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event){
		this.onPlayerMessage(event.getPlayer().getName(), event.getMessage());
	}
	
}
