package com.minebans.evidence;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.minebans.MineBans;
import com.minebans.events.PlayerBanEvent;

public class SpamEvidenceCollector extends EvidenceCollector implements Listener {
	
	private HashMap<String, Integer> maxViolationLevel;
	private HashMap<String, Integer> messageCounter;
	
	public SpamEvidenceCollector(MineBans plugin){
		this.maxViolationLevel = new HashMap<String, Integer>();
		this.messageCounter = new HashMap<String, Integer>();
		
		plugin.pluginManager.registerEvents(this, plugin);
		
		plugin.scheduler.scheduleSyncRepeatingTask(plugin, new SpamEvidenceCollectorResetTask(this), 100, 100);
	}
	
	// NOTE: Called by the task scheduled above,
	public void resetCounter(){
		Integer current, max;
		
		for (String playerName : this.messageCounter.keySet()){
			max = 0;
			
			current = this.messageCounter.get(playerName);
			max = this.maxViolationLevel.containsKey(playerName) ? this.maxViolationLevel.get(playerName) : 0;
			
			if (current > max){
				max = current;
			}
			
			this.maxViolationLevel.put(playerName, max);
		}
		
		this.messageCounter.clear();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChat(PlayerChatEvent event){
		String playerName = event.getPlayer().getName();
		
		this.messageCounter.put(playerName, (this.messageCounter.containsKey(playerName)) ? this.messageCounter.get(playerName) + 1 : 1);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event){
		String playerName = event.getPlayer().getName();
		
		this.messageCounter.put(playerName, (this.messageCounter.containsKey(playerName)) ? this.messageCounter.get(playerName) + 1 : 1);
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
			return this.messageCounter.get(playerName);
		}
		
		return 0;
	}
	
}
