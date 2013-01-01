package com.minebans.minebans.pluginInterfaces.nocheatplus;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.events.PlayerBanEvent;

public class NoCheatPlusDataCache implements Runnable, Listener {
	
	private MineBans plugin;
	
	private HashMap<String, LinkedList<NoCheatPlusCombinedData>> data;
	
	public NoCheatPlusDataCache(MineBans plugin){
		this.plugin = plugin;
		
		this.data = new HashMap<String, LinkedList<NoCheatPlusCombinedData>>();
		
		plugin.pluginManager.registerEvents(this, plugin);
	}
	
	public void run(){
		for (Player player : plugin.server.getOnlinePlayers()){
			String playerName = player.getName();
			LinkedList<NoCheatPlusCombinedData> data = this.data.get(playerName);
			
			if (data == null){
				data = new LinkedList<NoCheatPlusCombinedData>();
				this.data.put(playerName, data);
			}
			
			data.add(new NoCheatPlusCombinedData(player));
			
			if (data.size() > 20){
				data.remove(0);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBan(PlayerBanEvent event){
		this.data.remove(event.getPlayerName());
	}
	
	public Double getMaxViolationLevel(String playerName, BanReason reason){
		double maxVL = 0.0d;
		double currentVL;
		
		if (this.data.containsKey(playerName)){
			for (NoCheatPlusCombinedData data : this.data.get(playerName)){
				currentVL = data.getMaxforReason(reason);
				
				if (currentVL > maxVL){
					maxVL = currentVL;
				}
			}
		}
		
		return maxVL;
	}
	
}
