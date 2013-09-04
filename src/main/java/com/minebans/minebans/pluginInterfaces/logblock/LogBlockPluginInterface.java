package com.minebans.minebans.pluginInterfaces.logblock;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.pluginInterfaces.LoggingPluginInterface;

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
		this.logblock = (LogBlock) plugin.getServer().getPluginManager().getPlugin("LogBlock");
	}
	
	@Override
	public boolean pluginEnabled(){
		return (this.logblock != null);
	}
	
	@Override
	public String getPluginName(){
		return "LogBlock";
	}
	
	@Override
	public boolean checkConfig(){
		String worldName;
		String lbFolder = this.logblock.getDataFolder().getAbsolutePath();
		File worldConfigFile;
		
		YamlConfiguration worldConfig = new YamlConfiguration();
		
		for (World world : plugin.getServer().getWorlds()){
			worldName = world.getName();
			
			worldConfigFile = new File(lbFolder + File.separator + BukkitUtils.friendlyWorldname(worldName) + ".yml");
			
			plugin.log.info("Checking LogBlock config for '" + worldName + "'");
			
			if (!worldConfigFile.exists()){
				plugin.log.warn("To provide the best data LogBlock should be enabled for all worlds.");
				break;
			}
			
			try{
				worldConfig.load(worldConfigFile);
				
				if (plugin.config.getBoolean(Config.getReasonEnabled(BanReason.GRIEF))){
					if (!worldConfig.getBoolean("logging.BLOCKBREAK")){
						plugin.log.warn("To provide the best data LogBlock should be set to log block breaks for all worlds.");
					}
					
					if (!worldConfig.getBoolean("logging.BLOCKPLACE")){
						plugin.log.warn("To provide the best data LogBlock should be set to log blocks placed for all worlds.");
					}
				}
				
				if (plugin.config.getBoolean(Config.getReasonEnabled(BanReason.THEFT))){
					if (!worldConfig.getBoolean("logging.CHESTACCESS")){
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
	
	@Override
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		HashMap<Integer, Integer> data = new HashMap<Integer, Integer>();
		
		Field typeField, amountField;
		Short type, amount;
		
		for (World world : plugin.getServer().getWorlds()){
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
	
	private HashMap<Integer, Integer> getBlockChanges(String playerName, BlockChangeType type){
		HashMap<Integer, Integer> blocks = new HashMap<Integer, Integer>();
		
		for (World world : plugin.getServer().getWorlds()){
			try{
				QueryParams params = new QueryParams(this.logblock);
				
				params.setPlayer(playerName);
				params.world = world;
				params.bct = type;
				params.limit = -1;
				params.since = (int) ((System.currentTimeMillis() / 60000) - 1440);
				
				params.needType = true;
				
				for (BlockChange change : this.logblock.getBlockChanges(params)){
					int blockType = (type == BlockChangeType.CREATED) ? change.type : change.replaced;
					
					blocks.put(blockType, (blocks.containsKey(blockType)) ? blocks.get(blockType) + 1 : 1);
				}
			}catch (NullPointerException e){
				// This happens when LB is not enabled for a world, we can ignore that.
			}catch (Exception e){
				plugin.log.warn("LogBlock lookup failed.");
				e.printStackTrace();
			}
		}
		
		return blocks;
	}
	
	@Override
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		return this.getBlockChanges(playerName, BlockChangeType.CREATED);
	}
	
	@Override
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		return this.getBlockChanges(playerName, BlockChangeType.DESTROYED);
	}
	
	@Override
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		HashMap<String, HashMap<Integer, Integer>> data = new HashMap<String, HashMap<Integer, Integer>>();
		
		data.put("broken", this.getBlocksBroken(playerName));
		data.put("placed", this.getBlocksPlaced(playerName));
		
		return data;
	}
	
}
