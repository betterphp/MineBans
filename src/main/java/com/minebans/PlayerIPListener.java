package com.minebans;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import com.minebans.events.PlayerBanEvent;
import com.minebans.events.PlayerUnbanEvent;

import uk.co.jacekk.bukkit.baseplugin.v2.event.BaseListener;

public class PlayerIPListener extends BaseListener<MineBans> {
	
	public PlayerIPListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerBan(PlayerBanEvent event){
		String playerName = event.getPlayerName();
		String ipAddress = null;
		
		for (Entry<String, ArrayList<String>> entry : plugin.playerIPs.entrySet()){
			if (entry.getValue().contains(playerName)){
				ipAddress = entry.getKey();
				break;
			}
		}
		
		if (ipAddress != null){
			ArrayList<String> playerNames = plugin.bannedIPs.get(ipAddress);
			
			if (playerNames == null){
				playerNames = new ArrayList<String>();
				plugin.bannedIPs.put(ipAddress, playerNames);
			}
			
			playerNames.add(playerName);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerUnban(PlayerUnbanEvent event){
		String playerName = event.getPlayerName();
		ArrayList<String> remove = new ArrayList<String>();
		
		for (Entry<String, ArrayList<String>> entry : plugin.bannedIPs.entrySet()){
			if (entry.getValue().contains(playerName)){
				remove.add(entry.getKey());
			}
		}
		
		for (String name : remove){
			plugin.bannedIPs.remove(name);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		String playerName = player.getName();
		String ipAddress = player.getAddress().getAddress().getHostAddress();
		
		ArrayList<String> playerNames = plugin.playerIPs.get(ipAddress);
		
		if (playerNames == null){
			playerNames = new ArrayList<String>();
			plugin.playerIPs.put(ipAddress, playerNames);
		}
		
		playerNames.add(playerName);
	}
	
}
