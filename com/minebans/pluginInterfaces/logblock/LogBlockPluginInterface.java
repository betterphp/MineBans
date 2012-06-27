package com.minebans.pluginInterfaces.logblock;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;
import com.minebans.bans.BanReason;
import com.minebans.pluginInterfaces.LoggingPluginInterface;

import de.diddiz.LogBlock.BlockChange;
import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;
import de.diddiz.util.BukkitUtils;

public class LogBlockPluginInterface extends LoggingPluginInterface {
	
	private MineBans plugin;
	private LogBlock logblock;
	
	public LogBlockPluginInterface(MineBans plugin){
		this.plugin = plugin;
		this.logblock = (LogBlock) plugin.pluginManager.getPlugin("LogBlock");
	}
	
	public boolean pluginEnabled(){
		return (this.logblock != null);
	}
	
	public String getPluginName(){
		return "LogBlock";
	}
	
	public boolean checkConfig(){
		String worldName;
		String lbFolder = this.logblock.getDataFolder().getAbsolutePath();
		File worldConfigFile;
		
		YamlConfiguration worldConfig = new YamlConfiguration();
		
		for (World world : plugin.server.getWorlds()){
			worldName = world.getName();
			
			worldConfigFile = new File(lbFolder + File.separator + BukkitUtils.friendlyWorldname(worldName) + ".yml");
			
			plugin.log.info("Checking LogBlock config for '" + worldName + "'");
			
			if (worldConfigFile.exists() == false){
				plugin.log.warn("To provide the best data LogBlock should be enabled for all worlds.");
				break;
			}
			
			try{
				worldConfig.load(worldConfigFile);
				
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.GRIEF))){
					if (worldConfig.getBoolean("logging.BLOCKBREAK") == false){
						plugin.log.warn("To provide the best data LogBlock should be set to log block breaks for all worlds.");
					}
					
					if (worldConfig.getBoolean("logging.BLOCKPLACE") == false){
						plugin.log.warn("To provide the best data LogBlock should be set to log blocks placed for all worlds.");
					}
				}
				
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.THEFT))){
					if (worldConfig.getBoolean("logging.CHESTACCESS") == false){
						plugin.log.warn("To provide the best data LogBlock should be set to log chest access for all worlds.");
					}
				}
			}catch (Exception e){
				plugin.log.fatal("Failed to read LogBlock config file.");
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		HashMap<Integer, Integer> data = new HashMap<Integer, Integer>();
		
		Field typeField, amountField;
		Short type, amount;
		
		for (World world : plugin.server.getWorlds()){
			try{
				QueryParams params = new QueryParams(this.logblock);
				
				params.setPlayer(playerName);
				params.world = world;
				params.bct = BlockChangeType.CHESTACCESS;
				params.limit = -1;
				params.since = (int) ((System.currentTimeMillis() / 60000) - 1440);
				
				params.needChestAccess = true;
				
				for (BlockChange change : this.logblock.getBlockChanges(params)){
					typeField = change.ca.getClass().getDeclaredField("itemType");
					amountField = change.ca.getClass().getDeclaredField("itemAmount");
					
					typeField.setAccessible(true);
					amountField.setAccessible(true);
					
					type = typeField.getShort(change.ca);
					amount = amountField.getShort(change.ca);
					
					if (data.containsKey(type)){
						data.put((int) type, data.get(type) + amount);
					}else{
						data.put((int) type, new Integer(amount));
					}
				}
			}catch (NullPointerException e){
				// This happens when LB is not enabled for a world, we can ignore that.
			}catch (Exception e){
				plugin.log.warn("LogBlock lookup failed.");
				e.printStackTrace();
			}
		}
		
		return data;
	}
	
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		HashMap<Integer, Integer> placed = new HashMap<Integer, Integer>();
		
		for (World world : plugin.server.getWorlds()){
			try{
				QueryParams params = new QueryParams(this.logblock);
				
				params.setPlayer(playerName);
				params.world = world;
				params.bct = BlockChangeType.CREATED;
				params.limit = -1;
				params.since = (int) ((System.currentTimeMillis() / 60000) - 1440);
				
				params.needType = true;
				
				for (BlockChange change : this.logblock.getBlockChanges(params)){
					placed.put(change.type, (placed.containsKey(change.replaced)) ? placed.get(change.type) + 1 : 1);
				}
			}catch (NullPointerException e){
				// This happens when LB is not enabled for a world, we can ignore that.
			}catch (Exception e){
				plugin.log.warn("LogBlock lookup failed.");
				e.printStackTrace();
			}
		}
		
		return placed;
	}
	
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		HashMap<Integer, Integer> broken = new HashMap<Integer, Integer>();
		
		for (World world : plugin.server.getWorlds()){
			try{
				QueryParams params = new QueryParams(this.logblock);
				
				params.setPlayer(playerName);
				params.world = world;
				params.bct = BlockChangeType.DESTROYED;
				params.limit = -1;
				params.since = (int) ((System.currentTimeMillis() / 60000) - 1440);
				
				params.needType = true;
				
				for (BlockChange change : this.logblock.getBlockChanges(params)){
					broken.put(change.replaced, (broken.containsKey(change.replaced)) ? broken.get(change.replaced) + 1 : 1);
				}
			}catch (NullPointerException e){
				// This happens when LB is not enabled for a world, we can ignore that.
			}catch (Exception e){
				plugin.log.warn("LogBlock lookup failed.");
				e.printStackTrace();
			}
		}
		
		return broken;
	}
	
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		HashMap<String, HashMap<Integer, Integer>> data = new HashMap<String, HashMap<Integer, Integer>>();
		
		HashMap<Integer, Integer> broken = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> placed = new HashMap<Integer, Integer>();
		
		for (World world : plugin.server.getWorlds()){
			try{
				QueryParams params = new QueryParams(this.logblock);
				
				params.setPlayer(playerName);
				params.world = world;
				params.bct = BlockChangeType.BOTH;
				params.limit = -1;
				params.since = (int) ((System.currentTimeMillis() / 60000) - 1440);
				
				params.needType = true;
				
				for (BlockChange change : this.logblock.getBlockChanges(params)){
					if (change.type == 0){
						broken.put(change.replaced, (broken.containsKey(change.replaced)) ? broken.get(change.replaced) + 1 : 1);
					}else{
						placed.put(change.type, (placed.containsKey(change.type)) ? placed.get(change.type) + 1 : 1);
					}
				}
			}catch (NullPointerException e){
				// This happens when LB is not enabled for a world, we can ignore that.
			}catch (Exception e){
				plugin.log.warn("LogBlock lookup failed.");
				e.printStackTrace();
			}
		}
		
		data.put("broken", broken);
		data.put("placed", placed);
		
		return data;
	}
	
}
