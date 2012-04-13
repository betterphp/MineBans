package com.minebans.pluginInterfaces.guardian;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import org.guardian.ActionType;
import org.guardian.Guardian;
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
		return true;
	}
	
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		return new HashMap<Integer, Integer>();
	}
	
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		HashMap<Integer, Integer> placed = new HashMap<Integer, Integer>();
		
		QueryParams params = new QueryParams();
		
		params.players = Arrays.asList(playerName);
		params.actions = Arrays.asList(ActionType.BLOCK_PLACE);
		params.worlds = plugin.server.getWorlds();
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
