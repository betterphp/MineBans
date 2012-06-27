package com.minebans.pluginInterfaces.nocheatplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.minebans.MineBans;
import com.minebans.events.PlayerBanEvent;

import fr.neatmonster.nocheatplus.players.NCPPlayer;

public class NoCheatPlusDataCache implements Runnable, Listener {
	
	private MineBans plugin;
	
	private HashMap<String, HashMap<String, ArrayList<Long>>> data;
	
	public NoCheatPlusDataCache(MineBans plugin){
		this.plugin = plugin;
		
		this.data = new HashMap<String, HashMap<String, ArrayList<Long>>>();
		
		plugin.pluginManager.registerEvents(this, plugin);
	}
	
	public void run(){
		HashMap<String, ArrayList<Long>> storedData;
		ArrayList<Long> storedValues;
		
		for (Player player : plugin.server.getOnlinePlayers()){
			String playerName = player.getName();
			NCPPlayer ncpPlayer = NCPPlayer.getPlayer(player);
			
			Map<String, Object> playerData = ncpPlayer.getStatistics().get();
			
			if (!this.data.containsKey(playerName)){
				storedData = new HashMap<String, ArrayList<Long>>();
			}else{
				storedData = this.data.get(playerName);
			}
			
			for (String key : playerData.keySet()){
				if (!key.startsWith("nocheat")){
					if (!storedData.containsKey(key)){
						storedValues = new ArrayList<Long>();
					}else{
						storedValues = storedData.get(key);
					}
					
					// Some of the values are ints and some longs, weird !
					storedValues.add(new Long(playerData.get(key).toString()));
					
					if (storedValues.size() > 15){
						storedValues.remove(0);
					}
					
					storedData.put(key, storedValues);
				}
			}
			
			this.data.put(playerName, storedData);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBan(PlayerBanEvent event){
		String playerName = event.getPlayerName();
		
		if (this.data.containsKey(playerName)){
			this.data.remove(playerName);
		}
	}
	
	public Long getMaxViolationLevel(String playerName, String key){
		if (this.data.containsKey(playerName)){
			HashMap<String, ArrayList<Long>> storedData = this.data.get(playerName);
			
			if (storedData.containsKey(key)){
				return Collections.max(storedData.get(key));
			}
		}
		
		return 0L;
	}
	
}
