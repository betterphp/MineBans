package com.minebans.evidence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import uk.co.jacekk.bukkit.baseplugin.v2.util.ListUtils;

import com.minebans.MineBans;
import com.minebans.events.PlayerBanEvent;

public class AdvertisingEvidenceCollector extends EvidenceCollector implements Listener {
	
	private HashMap<String, HashMap<String, Integer>> wordListLog;
	
	private List<String> wordList;
	
	public AdvertisingEvidenceCollector(MineBans plugin){
		this.wordListLog = new HashMap<String, HashMap<String, Integer>>();
		
		this.wordList = Arrays.asList("supplies", "pay", "more", "winner", "consolidate", "debt", "promotion", "paid", "guarantee", "guaranteed", "opportunity", "compare", "offer", "selected", "giveaway", "giving", "join", "singles", "online", "pharmacy", "marketing", "limited", "vacation", "viagra", "amazing", "cash", "bonus", "promise", "credit", "loans", "satisfaction", "avoid", "bankruptcy", "casino", "subscribe", "discount", "eliminate", "weight", "marketing", "affordable", "bargain", "claims", "congratulations", "membership", "offer", "refund", "sales", "traffic", "insurance", "investment", "decision", "legal", "solutions", "money", "trial", "obligation", "shipped", "priority", "performance", "potential", "earnings", "win", "server", "map", "world");
		
		plugin.pluginManager.registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		String playerName = event.getPlayer().getName();
		HashMap<String, Integer> playerData;
		
		if (!this.wordListLog.containsKey(playerName)){
			playerData = new HashMap<String, Integer>();
		}else{
			playerData = this.wordListLog.get(playerName);
		}
		
		for (String word : ChatColor.stripColor(event.getMessage().toLowerCase()).split(" ")){
			if (this.wordList.contains(word)){
				playerData.put(word, (playerData.containsKey(word)) ? playerData.get(word) + 1 : 1);
			}
		}
		
		this.wordListLog.put(playerName, playerData);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBan(PlayerBanEvent event){
		String playerName = event.getPlayerName();
		
		if (this.wordListLog.containsKey(playerName)){
			this.wordListLog.remove(playerName);
		}
	}
	
	public Integer collect(String playerName){
		if (this.wordListLog.containsKey(playerName) == false){
			return 0;
		}
		
		return ListUtils.sumIntegers(this.wordListLog.get(playerName).values());
	}
	
}
