package com.minebans.minebans.pluginInterfaces;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.pluginInterfaces.defaultantispam.DefaultAntiSpamPluginInterface;
import com.minebans.minebans.pluginInterfaces.minebansantispam.MineBansAntiSpamPluginInterface;

public class AntiSpamInterface {
	
	private AntiSpamPluginInterface pluginInterface;
	
	public AntiSpamInterface(MineBans plugin){
		LinkedHashMap<String, Class<? extends AntiSpamPluginInterface>> suppportedPlugins = new LinkedHashMap<String, Class<? extends AntiSpamPluginInterface>>(8);
		
		suppportedPlugins.put("MineBansAntiSpam", MineBansAntiSpamPluginInterface.class);
		
		suppportedPlugins.put("MineBans", DefaultAntiSpamPluginInterface.class);
		
		for (Entry<String, Class<? extends AntiSpamPluginInterface>> entry : suppportedPlugins.entrySet()){
			String pluginName = entry.getKey();
			Class<? extends AntiSpamPluginInterface> cls = entry.getValue();
			
			if (plugin.getServer().getPluginManager().isPluginEnabled(pluginName)){
				try{
					this.pluginInterface = cls.getConstructor(MineBans.class).newInstance(plugin);
					
					plugin.log.info("Using " + this.pluginInterface.getPluginName() + " for spam data, checking config.");
					
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
