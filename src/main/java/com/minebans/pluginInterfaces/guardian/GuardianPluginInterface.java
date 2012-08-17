package com.minebans.pluginInterfaces.guardian;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import org.guardian.ActionType;
import org.guardian.Guardian;
import org.guardian.config.Config;
import org.guardian.config.WorldConfig;
import org.guardian.entries.BlockEntry;
import org.guardian.entries.Entry;
import org.guardian.params.QueryParams;

import com.minebans.MineBans;
import com.minebans.pluginInterfaces.LoggingPluginInterface;

public class GuardianPluginInterface extends LoggingPluginInterface {
	
	private MineBans plugin;
	private Guardian guardian;
	
	public GuardianPluginInterface(MineBans plugin){
		this.plugin = plugin;
		this.guardian = (Guardian) plugin.pluginManager.getPlugin("Guardian");
	}
	
	public boolean pluginEnabled(){
		return (this.guardian != null);
	}
	
	public String getPluginName(){
		return "Guardian";
	}
	
	public boolean checkConfig(){
		Config config = this.guardian.getConf();
		
		String worldName;
		WorldConfig worldConfig;
		
		for (java.util.Map.Entry<String, WorldConfig> entry : config.worlds.entrySet()){
			worldName = entry.getKey();
			worldConfig = entry.getValue();
			
			plugin.log.info("Checking LogBlock config for '" + worldName + "'");
			
			if (!worldConfig.isLogging(ActionType.BLOCK_BREAK)){
				plugin.log.warn("To provide the best data Guardian should be set to log block breaks for all worlds.");
			}
			
			if (!worldConfig.isLogging(ActionType.BLOCK_PLACE)){
				plugin.log.warn("To provide the best data Guardian should be set to log blocks placed for all worlds.");
			}
			
			if (!worldConfig.isLogging(ActionType.INVENTORY_TAKE)){
				plugin.log.warn("To provide the best data Guardian should be set to log items taken from inventories for all worlds.");
			}
			
			if (!worldConfig.isLogging(ActionType.INVENTORY_ADD)){
				plugin.log.warn("To provide the best data Guardian should be set to log items added to inventories for all worlds.");
			}
		}
		
		return true;
	}
	
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		HashMap<Integer, Integer> data = new HashMap<Integer, Integer>();
		
		// Guardian chest logging is still rather broken but this should work when it's fixed.
		/*
		QueryParams params = new QueryParams();
		
		params.players = Arrays.asList(playerName);
		params.actions = Arrays.asList(ActionType.INVENTORY_ADD, ActionType.INVENTORY_TAKE);
		params.worlds = plugin.server.getWorlds();
		params.since = (System.currentTimeMillis() / 60000) - 1440;
		params.limit = -1;
		
		params.needId = true;
		
		try{
			for (Entry logEntry : this.guardian.getLog(params)){
				int multi = (logEntry.getAction() == ActionType.INVENTORY_ADD) ? 1 : -1;
				
				int typeId = ((ItemEntry) logEntry).getTypeId();
				int amount = ((ItemEntry) logEntry).getAmount() * multi;
				
				data.put(typeId, (data.containsKey(typeId)) ? data.get(typeId) + amount : amount);
			}
		}catch (SQLException e){
			plugin.log.warn("Failed to lookup inventory changes. Please make sure Guardian is correctly configured.");
			e.printStackTrace();
		}
		
		*/
		
		return data;
	}
	
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		HashMap<Integer, Integer> placed = new HashMap<Integer, Integer>();
		
		QueryParams params = new QueryParams();
		
		params.players = Arrays.asList(playerName);
		params.actions = Arrays.asList(ActionType.BLOCK_PLACE);
		params.worlds = plugin.server.getWorlds();
		params.since = (System.currentTimeMillis() / 60000) - 1440;
		params.limit = -1;
		
		params.needId = true;
		
		try{
			int typeId;
			
			for (Entry logEntry : this.guardian.getLog(params)){
				typeId = ((BlockEntry) logEntry).getTypeAfter();
				
				placed.put(typeId, (placed.containsKey(typeId)) ? placed.get(typeId) + 1 : 1);
			}
		}catch (SQLException e){
			plugin.log.warn("Failed to lookup block changes. Please make sure Guardian is correctly configured.");
			e.printStackTrace();
		}
		
		return placed;
	}
	
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		HashMap<Integer, Integer> broken = new HashMap<Integer, Integer>();
		
		QueryParams params = new QueryParams();
		
		params.players = Arrays.asList(playerName);
		params.actions = Arrays.asList(ActionType.BLOCK_BREAK);
		params.worlds = plugin.server.getWorlds();
		params.since = (System.currentTimeMillis() / 60000) - 1440;
		params.limit = -1;
		
		try{
			int typeId;
			
			for (Entry logEntry : this.guardian.getLog(params)){
				typeId = ((BlockEntry) logEntry).getTypeBefore();
				
				broken.put(typeId, (broken.containsKey(typeId)) ? broken.get(typeId) + 1 : 1);
			}
		}catch (SQLException e){
			plugin.log.warn("Failed to lookup block changes. Please make sure Guardian is correctly configured.");
			e.printStackTrace();
		}
		
		return broken;
	}
	
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		HashMap<String, HashMap<Integer, Integer>> data = new HashMap<String, HashMap<Integer, Integer>>();
		
		HashMap<Integer, Integer> broken = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> placed = new HashMap<Integer, Integer>();
		
		QueryParams params = new QueryParams();
		
		params.players = Arrays.asList(playerName);
		params.actions = Arrays.asList(ActionType.BLOCK_PLACE, ActionType.BLOCK_BREAK);
		params.worlds = plugin.server.getWorlds();
		params.since = (System.currentTimeMillis() / 60000) - 1440;
		params.limit = -1;
		
		try{
			int typeId;
			
			for (Entry logEntry : this.guardian.getLog(params)){
				if (logEntry.getAction() == ActionType.BLOCK_BREAK){
					typeId = ((BlockEntry) logEntry).getTypeBefore();
					broken.put(typeId, (broken.containsKey(typeId)) ? broken.get(typeId) + 1 : 1);
				}else{
					typeId = ((BlockEntry) logEntry).getTypeAfter();
					placed.put(typeId, (placed.containsKey(typeId)) ? placed.get(typeId) + 1 : 1);
				}
			}
		}catch (SQLException e){
			plugin.log.warn("Failed to lookup block changes. Please make sure Guardian is correctly configured.");
			e.printStackTrace();
		}
		
		data.put("broken", broken);
		data.put("placed", placed);
		
		return data;
	}
	
}
