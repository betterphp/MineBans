package com.minebans.minebans.pluginInterfaces;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.pluginInterfaces.coreprotect.CoreProtectPluginIntereface;
import com.minebans.minebans.pluginInterfaces.defaultlogging.DefaultLoggingPluginInterface;
import com.minebans.minebans.pluginInterfaces.guardian.GuardianPluginInterface;
import com.minebans.minebans.pluginInterfaces.hawkeye.HawkEyePluginInterface;
import com.minebans.minebans.pluginInterfaces.logblock.LogBlockPluginInterface;
import com.minebans.minebans.pluginInterfaces.swatchdog.SWatchdogPluginInterface;

public class LoggingInterface {
	
	private LoggingPluginInterface pluginInterface;
	
	public LoggingInterface(MineBans plugin){
		LinkedHashMap<String, Class<? extends LoggingPluginInterface>> suppportedPlugins = new LinkedHashMap<String, Class<? extends LoggingPluginInterface>>(8);
		
		suppportedPlugins.put("LogBlock", LogBlockPluginInterface.class);
		suppportedPlugins.put("HawkEye", HawkEyePluginInterface.class);
		suppportedPlugins.put("Guardian", GuardianPluginInterface.class);
		suppportedPlugins.put("SWatchdog", SWatchdogPluginInterface.class);
		suppportedPlugins.put("CoreProtect", CoreProtectPluginIntereface.class);
		
		suppportedPlugins.put("MineBans", DefaultLoggingPluginInterface.class);
		
		for (Entry<String, Class<? extends LoggingPluginInterface>> entry : suppportedPlugins.entrySet()){
			String pluginName = entry.getKey();
			Class<? extends LoggingPluginInterface> cls = entry.getValue();
			
			if (plugin.pluginManager.isPluginEnabled(pluginName)){
				try{
					this.pluginInterface = cls.getConstructor(MineBans.class).newInstance(plugin);
					
					plugin.log.info("Using " + this.pluginInterface.getPluginName() + " for player data, checking config.");
					
					if (!this.pluginInterface.checkConfig()){
						plugin.log.fatal(this.pluginInterface.getPluginName() + " minimum config was not met.");
					}
					
					break;
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean foundLoggingPlugin(){
		return (this.pluginInterface != null);
	}
	
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		if (!this.foundLoggingPlugin()){
			return null;
		}
		
		return this.pluginInterface.getChestAccess(playerName);
	}
	
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		if (!this.foundLoggingPlugin()){
			return null;
		}
		
		return this.pluginInterface.getBlocksPlaced(playerName);
	}
	
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		if (!this.foundLoggingPlugin()){
			return null;
		}
		
		return this.pluginInterface.getBlocksBroken(playerName);
	}
	
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		if (!this.foundLoggingPlugin()){
			return null;
		}
		
		return this.pluginInterface.getBlockChanges(playerName);
	}
	
}
