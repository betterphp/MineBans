package com.minebans.pluginInterfaces.hawkeye;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
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
import com.minebans.pluginInterfaces.LoggingPluginInterface;

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
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis() - 86400000L);
		
		SearchParser search = new SearchParser();
		search.players = Arrays.asList(playerName);
		search.actions = Arrays.asList(DataType.CONTAINER_TRANSACTION);
		search.worlds = worldNames;
		search.dateFrom = format.format(cal);
		
		HawkEyeChestAccessCallback callback = new HawkEyeChestAccessCallback(plugin); 
		
		HawkEyeAPI.performSearch(callback, search, SearchDir.DESC);
		
		while (!callback.complete){
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
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis() - 86400000L);
		
		SearchParser search = new SearchParser();
		search.players = Arrays.asList(playerName);
		search.actions = Arrays.asList(DataType.BLOCK_PLACE);
		search.worlds = worldNames;
		search.dateFrom = format.format(cal);
		
		HawkEyeBlockPlacedCallback callback = new HawkEyeBlockPlacedCallback(plugin); 
		
		HawkEyeAPI.performSearch(callback, search, SearchDir.DESC);
		
		while (!callback.complete){
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
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis() - 86400000L);
		
		SearchParser search = new SearchParser();
		search.players = Arrays.asList(playerName);
		search.actions = Arrays.asList(DataType.BLOCK_BREAK);
		search.worlds = worldNames;
		search.dateFrom = format.format(cal);
		
		HawkEyeBlockBrokenCallback callback = new HawkEyeBlockBrokenCallback(plugin); 
		
		HawkEyeAPI.performSearch(callback, search, SearchDir.DESC);
		
		while (!callback.complete){
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
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis() - 86400000L);
		
		SearchParser search = new SearchParser();
		search.players = Arrays.asList(playerName);
		search.actions = Arrays.asList(DataType.BLOCK_PLACE);
		search.worlds = worldNames;
		search.dateFrom = format.format(cal);
		
		HawkEyeBlockPlacedCallback placeCallback = new HawkEyeBlockPlacedCallback(plugin); 
		
		HawkEyeAPI.performSearch(placeCallback, search, SearchDir.DESC);
		
		search = new SearchParser();
		search.players = Arrays.asList(playerName);
		search.actions = Arrays.asList(DataType.BLOCK_BREAK);
		search.worlds = worldNames;
		
		HawkEyeBlockBrokenCallback breakCallback = new HawkEyeBlockBrokenCallback(plugin); 
		
		HawkEyeAPI.performSearch(breakCallback, search, SearchDir.DESC);
		
		while (!placeCallback.complete || !breakCallback.complete){
			synchronized (this){
				try{
					this.wait(100);
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
