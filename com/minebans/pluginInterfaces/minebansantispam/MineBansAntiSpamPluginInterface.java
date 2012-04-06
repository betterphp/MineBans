package com.minebans.pluginInterfaces.minebansantispam;

import java.util.ArrayList;

import com.minebans.MineBans;
import com.minebans.pluginInterfaces.AntiSpamPluginInterface;

public class MineBansAntiSpamPluginInterface extends AntiSpamPluginInterface {
	
	private MineBans plugin;
	
	protected ArrayList<String> detectedSpammers;
	
	public MineBansAntiSpamPluginInterface(MineBans plugin){
		this.plugin = plugin;
		
		this.detectedSpammers = new ArrayList<String>();
		
		plugin.pluginManager.registerEvents(new SpammerBanListener(this.detectedSpammers), plugin);
	}
	
	public boolean pluginEnabled(){
		return plugin.pluginManager.isPluginEnabled("MineBansAntiSpam");
	}
	
	public String getPluginName(){
		return "MineBansAntiSpam";
	}
	
	public boolean checkConfig(){
		// There is no config ;)
		return true;
	}
	
	public int getMaxViolationLevel(String playerName){
		return (this.detectedSpammers.contains(playerName.toLowerCase())) ? 20 : 0;
	}
	
}
