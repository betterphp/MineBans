package com.minebans.pluginInterfaces;

import java.util.HashMap;

import com.minebans.MineBans;

public class HawkEyePluginInterface extends LoggingPluginInterface {
	
	private MineBans plugin;
	
	public HawkEyePluginInterface(MineBans plugin){
		this.plugin = plugin;
	}
	
	public boolean pluginEnabled(){
		return plugin.pluginManager.isPluginEnabled("HawkEye");
	}
	
	public String getPluginName(){
		return "HawkEye";
	}
	
	public boolean checkConfig(){
		return false;
	}
	
	public HashMap<Short, Integer> getChestAccess(String playerName){
		return null;
	}
	
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		return null;
	}
	
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		return null;
	}
	
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		return null;
	}
	
}
