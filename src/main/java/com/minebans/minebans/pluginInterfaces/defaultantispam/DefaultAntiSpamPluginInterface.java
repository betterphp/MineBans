package com.minebans.minebans.pluginInterfaces.defaultantispam;

import java.util.HashMap;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.pluginInterfaces.AntiSpamPluginInterface;

public class DefaultAntiSpamPluginInterface extends AntiSpamPluginInterface {
	
	protected HashMap<String, Integer> maxViolationLevel;
	protected HashMap<String, HashMap<String, Integer>> messageCounter;
	
	public DefaultAntiSpamPluginInterface(MineBans plugin){
		this.maxViolationLevel = new HashMap<String, Integer>();
		this.messageCounter = new HashMap<String, HashMap<String, Integer>>();
		
		plugin.pluginManager.registerEvents(new ChatListener(this), plugin);
		plugin.pluginManager.registerEvents(new BanListener(this), plugin);
		
		plugin.scheduler.scheduleSyncRepeatingTask(plugin, new CounterResetTask(this), 200, 200);
	}
	
	public boolean pluginEnabled(){
		// This is not really a plugin ...
		return true;
	}
	
	public String getPluginName(){
		return "DefaultAntiSpam";
	}
	
	public boolean checkConfig(){
		// ... and there is no config.
		return true;
	}
	
	public int getMaxViolationLevel(String playerName){
		if (!this.maxViolationLevel.containsKey(playerName)){
			return 0;
		}
		
		return this.maxViolationLevel.get(playerName);
	}
	
}
