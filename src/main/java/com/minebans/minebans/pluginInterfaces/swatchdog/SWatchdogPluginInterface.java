package com.minebans.minebans.pluginInterfaces.swatchdog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;

import org.bukkit.Material;

import me.Sanzennin.SWatchdog.SWatchdog;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.pluginInterfaces.LoggingPluginInterface;

public class SWatchdogPluginInterface extends LoggingPluginInterface {
	
	private MineBans plugin;
	private SWatchdog watchdog;
	
	public SWatchdogPluginInterface(MineBans plugin){
		this.plugin = plugin;
		this.watchdog = (SWatchdog) plugin.pluginManager.getPlugin("SWatchdog");
	}
	
	public boolean pluginEnabled(){
		return (this.watchdog != null);
	}
	
	public String getPluginName(){
		return "SWatchdog";
	}
	
	public boolean checkConfig(){
		Properties config = new Properties();
		
		try{
			config.load(new FileInputStream(new File("plugins/SWatchdog/util/config.txt")));
			
			if (!config.getProperty("WatchUser").equalsIgnoreCase("true")){
				plugin.log.warn("To provide the best data SWatchdog should be set to log block changes made by players.");
			}
			
			if (!config.getProperty("WatchChestAccess").equalsIgnoreCase("true")){
				plugin.log.warn("To provide the best data SWatchdog should be set to log chest access.");
			}
		}catch (Exception e){
			plugin.log.fatal("Unable to reasd SWatchdog config file");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		// SWatchdog doesn't have chest logging :(
		return new HashMap<Integer, Integer>();
	}
	
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		File folder = new File("plugins/SWatchdog");
		
		String[] files = folder.list(new FilenameFilter(){
			
			public boolean accept(File dir, String name){
				return (name.startsWith("placed.") && name.endsWith(".txt"));
			}
			
		});
		
		HashMap<Integer, Integer> placed = new HashMap<Integer, Integer>();
		
		Long ageLimit = System.currentTimeMillis() - 86400000;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm/dd/MM/yyyy");
		
		for (String name : files){
			File file = new File(folder.getAbsolutePath() + File.separator + name);
			
			if (file.lastModified() > ageLimit){
				try{
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String line;
					
					while ((line = reader.readLine()) != null){
						String[] parts = line.split("\\.");
						
						if (parts[4].equalsIgnoreCase(playerName)){
							int breakPos = parts[3].indexOf(":");
							
							String blockName = (breakPos != -1) ? parts[3].substring(0, breakPos) : parts[3];
							Integer blockId = Material.getMaterial(blockName).getId();
							Long time = dateFormat.parse(parts[5]).getTime();
							
							if (time > ageLimit){
								placed.put(blockId, (placed.containsKey(blockId)) ? placed.get(blockId) + 1 : 1);
							}
						}
					}
					
					reader.close();
				}catch (FileNotFoundException e){
					// This won't ever happen, silly Java !
				}catch (IOException e){
					plugin.log.warn("Failed reading file, " + file.getAbsolutePath());
					e.printStackTrace();
				}catch (ParseException e){
					plugin.log.warn("Failed to parse date in file, " + file.getAbsolutePath());
					e.printStackTrace();
				}
			}
		}
		
		return placed;
	}
	
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		File folder = new File("plugins/SWatchdog");
		
		String[] files = folder.list(new FilenameFilter(){
			
			public boolean accept(File dir, String name){
				return (name.startsWith("broke.") && name.endsWith(".txt"));
			}
			
		});
		
		HashMap<Integer, Integer> broken = new HashMap<Integer, Integer>();
		
		Long ageLimit = System.currentTimeMillis() - 86400000;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm/dd/MM/yyyy");
		
		for (String name : files){
			File file = new File(folder.getAbsolutePath() + File.separator + name);
			
			if (file.lastModified() > ageLimit){
				try{
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String line;
					
					while ((line = reader.readLine()) != null){
						String[] parts = line.split("\\.");
						
						if (parts[4].equalsIgnoreCase(playerName)){
							int breakPos = parts[3].indexOf(":");
							
							String blockName = (breakPos != -1) ? parts[3].substring(0, breakPos) : parts[3];
							Integer blockId = Material.getMaterial(blockName).getId();
							Long time = dateFormat.parse(parts[5]).getTime();
							
							if (time > ageLimit){
								broken.put(blockId, (broken.containsKey(blockId)) ? broken.get(blockId) + 1 : 1);
							}
						}
					}
					
					reader.close();
				}catch (FileNotFoundException e){
					// This won't ever happen, silly Java !
				}catch (IOException e){
					plugin.log.warn("Failed reading file, " + file.getAbsolutePath());
					e.printStackTrace();
				}catch (ParseException e){
					plugin.log.warn("Failed to parse date in file, " + file.getAbsolutePath());
					e.printStackTrace();
				}
			}
		}
		
		return broken;
	}
	
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		HashMap<String, HashMap<Integer, Integer>> data = new HashMap<String, HashMap<Integer, Integer>>();
		
		data.put("broken", this.getBlocksBroken(playerName));
		data.put("placed", this.getBlocksPlaced(playerName));
		
		return null;
	}
	
}
