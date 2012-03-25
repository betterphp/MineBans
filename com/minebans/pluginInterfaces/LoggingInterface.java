package com.minebans.pluginInterfaces;

import java.util.HashMap;

import com.minebans.MineBans;

public class LoggingInterface {
	
	private LoggingPluginInterface pluginInterface;
	
	public LoggingInterface(MineBans plugin){
		if (plugin.pluginManager.isPluginEnabled("LogBlock")){
			this.pluginInterface = new LogBlockPluginInterface(plugin);
		}else if (plugin.pluginManager.isPluginEnabled("HawkEye")){
			this.pluginInterface = new HawkEyePluginInterface(plugin);
		}else{
			plugin.log.warn("A suitable logging plugin was not found.");
			plugin.log.warn("It is strongly recomended that you install a plugin such as LogBlock");
			plugin.log.warn("this will be used to calculate the severity of any bans you make.");
		}
		
		if (this.foundLoggingPlugin()){
			plugin.log.info("Using " + this.pluginInterface.getPluginName() + " for player data, checking config.");
			
			if (this.pluginInterface.checkConfig() == false){
				plugin.log.fatal(this.pluginInterface.getPluginName() + " minimum config was not met.");
			}
		}
	}
	
	public boolean foundLoggingPlugin(){
		return (this.pluginInterface != null);
	}
	
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		if (this.foundLoggingPlugin() == false){
			return null;
		}
		
		return this.pluginInterface.getChestAccess(playerName);
	}
	
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		if (this.foundLoggingPlugin() == false){
			return null;
		}
		
		return this.pluginInterface.getBlocksPlaced(playerName);
	}
	
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		if (this.foundLoggingPlugin() == false){
			return null;
		}
		
		return this.pluginInterface.getBlocksBroken(playerName);
	}
	
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		if (this.foundLoggingPlugin() == false){
			return null;
		}
		
		return this.pluginInterface.getBlockChanges(playerName);
	}
	
}
