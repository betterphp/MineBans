package com.minebans.minebans.pluginInterfaces.minebansantispam;

import java.util.ArrayList;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.pluginInterfaces.AntiSpamPluginInterface;

public class MineBansAntiSpamPluginInterface extends AntiSpamPluginInterface {
	
	private MineBans plugin;
	
	protected ArrayList<String> detectedSpammers;
	
	public MineBansAntiSpamPluginInterface(MineBans plugin){
		this.plugin = plugin;
		
		this.detectedSpammers = new ArrayList<String>();
		
		plugin.pluginManager.registerEvents(new SpammerBanListener(this.detectedSpammers), plugin);
	}
	
	@Override
	public boolean pluginEnabled(){
		return plugin.pluginManager.isPluginEnabled("MineBansAntiSpam");
	}
	
	@Override
	public String getPluginName(){
		return "MineBansAntiSpam";
	}
	
	@Override
	public boolean checkConfig(){
		// There is no config ;)
		return true;
	}
	
	@Override
	public int getMaxViolationLevel(String playerName){
		return (this.detectedSpammers.contains(playerName.toLowerCase())) ? 20 : 0;
	}
	
}
