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
		
		plugin.getServer().getPluginManager().registerEvents(new ChatListener(this), plugin);
		plugin.getServer().getPluginManager().registerEvents(new BanListener(this), plugin);
		
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new CounterResetTask(this), 200, 200);
	}
	
	@Override
	public boolean pluginEnabled(){
		// This is not really a plugin ...
		return true;
	}
	
	@Override
	public String getPluginName(){
		return "DefaultAntiSpam";
	}
	
	@Override
	public boolean checkConfig(){
		// ... and there is no config.
		return true;
	}
	
	@Override
	public int getMaxViolationLevel(String playerName){
		if (!this.maxViolationLevel.containsKey(playerName)){
			return 0;
		}
		
		return this.maxViolationLevel.get(playerName);
	}
	
}
