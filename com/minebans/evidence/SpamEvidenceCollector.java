package com.minebans.evidence;

import java.util.Collections;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.minebans.MineBans;
import com.minebans.events.PlayerBanEvent;

public class SpamEvidenceCollector extends EvidenceCollector implements Listener {
	
	private HashMap<String, Integer> maxViolationLevel;
	private HashMap<String, HashMap<String, Integer>> messageCounter;
	
	public SpamEvidenceCollector(MineBans plugin){
		this.maxViolationLevel = new HashMap<String, Integer>();
		this.messageCounter = new HashMap<String, HashMap<String, Integer>>();
		
		plugin.pluginManager.registerEvents(this, plugin);
		
		plugin.scheduler.scheduleSyncRepeatingTask(plugin, new SpamEvidenceCollectorResetTask(this), 200, 200); // 200 ticks = 10 seconds
	}
	
	// NOTE: Called by the task scheduled above,
	public void resetCounter(){
		Integer current, max;
		
		for (String playerName : this.messageCounter.keySet()){
			max = 0;
			
			current = Collections.max(this.messageCounter.get(playerName).values());
			max = this.maxViolationLevel.containsKey(playerName) ? this.maxViolationLevel.get(playerName) : 0;
			
			if (current > max){
				max = current;
			}
			
			this.maxViolationLevel.put(playerName, max);
		}
		
		this.messageCounter.clear();
	}
	
	private void processPlayerSendMessage(Player player, String message){
		String playerName = player.getName();
		HashMap<String, Integer> playerMessages;
		
		if (this.messageCounter.containsKey(playerName) == false){
			playerMessages = new HashMap<String, Integer>();
		}else{
			playerMessages = this.messageCounter.get(playerName);
		}
		
		playerMessages.put(message, (playerMessages.containsKey(message)) ? playerMessages.get(message) + 1 : 1);
		
		this.messageCounter.put(playerName, playerMessages);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChat(PlayerChatEvent event){
		this.processPlayerSendMessage(event.getPlayer(), event.getMessage());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event){
		this.processPlayerSendMessage(event.getPlayer(), event.getMessage());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBan(PlayerBanEvent event){
		String playerName = event.getPlayerName();
		
		if (this.messageCounter.containsKey(playerName)){
			this.messageCounter.remove(playerName);
		}
		
		if (this.maxViolationLevel.containsKey(playerName)){
			this.maxViolationLevel.remove(playerName);
		}
	}
	
	public Integer collect(String playerName){
		if (this.maxViolationLevel.containsKey(playerName)){
			return this.maxViolationLevel.get(playerName);
		}
		
		if (this.messageCounter.containsKey(playerName)){
			return Collections.max(this.messageCounter.get(playerName).values());
		}
		
		return 0;
	}
	
}
