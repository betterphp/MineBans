package com.minebans.pluginInterfaces;

import java.util.HashMap;

import com.minebans.MineBans;
import com.minebans.pluginInterfaces.defaultlogging.DefaultLoggingPluginInterface;
import com.minebans.pluginInterfaces.guardian.GuardianPluginInterface;
import com.minebans.pluginInterfaces.hawkeye.HawkEyePluginInterface;
import com.minebans.pluginInterfaces.logblock.LogBlockPluginInterface;
import com.minebans.pluginInterfaces.swatchdog.SWatchdogPluginInterface;

public class LoggingInterface {
	
	private LoggingPluginInterface pluginInterface;
	
	public LoggingInterface(MineBans plugin){
		if (plugin.pluginManager.isPluginEnabled("LogBlock")){
			this.pluginInterface = new LogBlockPluginInterface(plugin);
		}else if (plugin.pluginManager.isPluginEnabled("HawkEye")){
			this.pluginInterface = new HawkEyePluginInterface(plugin);
		}else if (plugin.pluginManager.isPluginEnabled("Guardian")){
			this.pluginInterface = new GuardianPluginInterface(plugin);
		}else if (plugin.pluginManager.isPluginEnabled("SWatchdog")){
			this.pluginInterface = new SWatchdogPluginInterface(plugin);
		}else{
			this.pluginInterface = new DefaultLoggingPluginInterface(plugin);
		}
		
		plugin.log.info("Using " + this.pluginInterface.getPluginName() + " for player data, checking config.");
		
		if (!this.pluginInterface.checkConfig()){
			plugin.log.fatal(this.pluginInterface.getPluginName() + " minimum config was not met.");
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
