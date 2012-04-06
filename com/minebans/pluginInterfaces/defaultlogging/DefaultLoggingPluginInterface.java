package com.minebans.pluginInterfaces.defaultlogging;

import java.util.HashMap;

import com.minebans.MineBans;
import com.minebans.pluginInterfaces.LoggingPluginInterface;

public class DefaultLoggingPluginInterface extends LoggingPluginInterface {
	
	protected HashMap<String, HashMap<Integer, Integer>> blocksBroken;
	protected HashMap<String, HashMap<Integer, Integer>> blocksPlaced;
	
	protected HashMap<String, HashMap<Integer, Integer>> chestAccess;
	
	public DefaultLoggingPluginInterface(MineBans plugin){
		this.blocksBroken = new HashMap<String, HashMap<Integer, Integer>>();
		this.blocksPlaced = new HashMap<String, HashMap<Integer, Integer>>();
		
		this.chestAccess = new HashMap<String, HashMap<Integer, Integer>>();
		
		plugin.pluginManager.registerEvents(new BlockListener(this), plugin);
		plugin.pluginManager.registerEvents(new InventoryListener(this), plugin);
	}
	
	public boolean pluginEnabled(){
		// This is not really a plugin ...
		return true;
	}
	
	public String getPluginName(){
		return "DefaultLoggingPlugin";
	}
	
	public boolean checkConfig(){
		// ... and it has no config.
		return true;
	}
	
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		return this.chestAccess.get(playerName.toLowerCase());
	}
	
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		return this.blocksPlaced.get(playerName.toLowerCase());
	}
	
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		return this.blocksBroken.get(playerName.toLowerCase());
	}
	
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		HashMap<String, HashMap<Integer, Integer>> data = new HashMap<String, HashMap<Integer, Integer>>();
		
		data.put("broken", this.getBlocksBroken(playerName));
		data.put("placed", this.getBlocksPlaced(playerName));
		
		return data;
	}
	
}
