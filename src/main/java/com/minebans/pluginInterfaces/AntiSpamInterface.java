package com.minebans.pluginInterfaces;

import com.minebans.MineBans;
import com.minebans.pluginInterfaces.defaultantispam.DefaultAntiSpamPluginInterface;
import com.minebans.pluginInterfaces.minebansantispam.MineBansAntiSpamPluginInterface;

public class AntiSpamInterface {
	
	private AntiSpamPluginInterface pluginInterface;
	
	public AntiSpamInterface(MineBans plugin){
		if (plugin.pluginManager.isPluginEnabled("MineBansAntiSpam")){
			this.pluginInterface = new MineBansAntiSpamPluginInterface(plugin);
		}else{
			this.pluginInterface = new DefaultAntiSpamPluginInterface(plugin);
		}
		
		plugin.log.info("Using " + this.pluginInterface.getPluginName() + " for spam data, checking config.");
		
		if (!this.pluginInterface.checkConfig()){
			plugin.log.fatal(this.pluginInterface.getPluginName() + " minimum config was not met.");
		}
	}
	
	public boolean foundAntiSpamPlugin(){
		return (this.pluginInterface != null);
	}
	
	public Integer getMaxViolationLevel(String playerName){
		if (!this.foundAntiSpamPlugin()){
			return null;
		}
		
		return this.pluginInterface.getMaxViolationLevel(playerName);
	}
	
}
