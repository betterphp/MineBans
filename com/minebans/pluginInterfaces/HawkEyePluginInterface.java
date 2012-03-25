package com.minebans.pluginInterfaces;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.util.HawkEyeAPI;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;
import com.minebans.bans.BanReason;

public class HawkEyePluginInterface extends LoggingPluginInterface {
	
	private MineBans plugin;
	
	public HawkEyePluginInterface(MineBans plugin){
		this.plugin = plugin;
	}
	
	public boolean pluginEnabled(){
		return plugin.pluginManager.isPluginEnabled("HawkEye");
	}
	
	public String getPluginName(){
		return "HawkEye";
	}
	
	public boolean checkConfig(){
		YamlConfiguration config = new YamlConfiguration();
		
		try{
			config.load("plugins/HawkEye/config.yml");
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.GRIEF))){
				if (config.getBoolean("log.block-break") == false){
					plugin.log.warn("To provide the best data HawkEye should be set to log block breaks.");
				}
				
				if (config.getBoolean("log.block-place") == false){
					plugin.log.warn("To provide the best data HawkEye should be set to log blocks placed.");
				}
			}
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.THEFT))){
				if (config.getBoolean("log.container-transaction") == false){
					plugin.log.warn("To provide the best data HawkEye should be set to log container transactions.");
				}
			}
			
			if (config.getStringList("ignore-worlds").size() > 0){
				plugin.log.warn("To provide the best data HawkEye should be enabled for all worlds.");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		List<World> worlds = plugin.server.getWorlds();
		String[] worldNames = new String[worlds.size()];
		
		for (int i = 0; i < worlds.size(); ++i){
			worldNames[i] = worlds.get(i).getName();
		}
		
		SearchParser search = new SearchParser();
		search.players = Arrays.asList(playerName);
		search.actions = Arrays.asList(DataType.CONTAINER_TRANSACTION);
		search.worlds = worldNames;
		
		HawkEyeChestAccessCallback callback = new HawkEyeChestAccessCallback(); 
		
		HawkEyeAPI.performSearch(callback, search, SearchDir.DESC);
		
		while (!callback.gotData){
			synchronized (this){
				try{
					this.wait(50);
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		
		return callback.taken;
	}
	
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		List<World> worlds = plugin.server.getWorlds();
		String[] worldNames = new String[worlds.size()];
		
		for (int i = 0; i < worlds.size(); ++i){
			worldNames[i] = worlds.get(i).getName();
		}
		
		SearchParser search = new SearchParser();
		search.players = Arrays.asList(playerName);
		search.actions = Arrays.asList(DataType.BLOCK_PLACE);
		search.worlds = worldNames;
		
		HawkEyeBlockPlacedCallback callback = new HawkEyeBlockPlacedCallback(); 
		
		HawkEyeAPI.performSearch(callback, search, SearchDir.DESC);
		
		while (!callback.gotData){
			synchronized (this){
				try{
					this.wait(50);
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		
		return callback.placed;
	}
	
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		List<World> worlds = plugin.server.getWorlds();
		String[] worldNames = new String[worlds.size()];
		
		for (int i = 0; i < worlds.size(); ++i){
			worldNames[i] = worlds.get(i).getName();
		}
		
		SearchParser search = new SearchParser();
		search.players = Arrays.asList(playerName);
		search.actions = Arrays.asList(DataType.BLOCK_BREAK);
		search.worlds = worldNames;
		
		HawkEyeBlockBrokenCallback callback = new HawkEyeBlockBrokenCallback(); 
		
		HawkEyeAPI.performSearch(callback, search, SearchDir.DESC);
		
		while (!callback.gotData){
			synchronized (this){
				try{
					this.wait(50);
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		
		return callback.broken;
	}
	
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		HashMap<String, HashMap<Integer, Integer>> data = new HashMap<String, HashMap<Integer, Integer>>();
		
		List<World> worlds = plugin.server.getWorlds();
		String[] worldNames = new String[worlds.size()];
		
		for (int i = 0; i < worlds.size(); ++i){
			worldNames[i] = worlds.get(i).getName();
		}
		
		SearchParser search = new SearchParser();
		search.players = Arrays.asList(playerName);
		search.actions = Arrays.asList(DataType.BLOCK_PLACE);
		search.worlds = worldNames;
		
		HawkEyeBlockPlacedCallback placeCallback = new HawkEyeBlockPlacedCallback(); 
		
		HawkEyeAPI.performSearch(placeCallback, search, SearchDir.DESC);
		
		search = new SearchParser();
		search.players = Arrays.asList(playerName);
		search.actions = Arrays.asList(DataType.BLOCK_BREAK);
		search.worlds = worldNames;
		
		HawkEyeBlockBrokenCallback breakCallback = new HawkEyeBlockBrokenCallback(); 
		
		HawkEyeAPI.performSearch(breakCallback, search, SearchDir.DESC);
		
		while (!placeCallback.gotData || !breakCallback.gotData){
			synchronized (this){
				try{
					this.wait(50);
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		
		data.put("broken", breakCallback.broken);
		data.put("placed", placeCallback.placed);
		
		return data;
	}
	
}
