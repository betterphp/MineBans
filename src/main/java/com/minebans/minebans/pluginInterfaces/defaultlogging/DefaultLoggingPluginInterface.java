package com.minebans.minebans.pluginInterfaces.defaultlogging;

import java.util.HashMap;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.pluginInterfaces.LoggingPluginInterface;

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
	
	@Override
	public boolean pluginEnabled(){
		// This is not really a plugin ...
		return true;
	}
	
	@Override
	public String getPluginName(){
		return "DefaultLoggingPlugin";
	}
	
	@Override
	public boolean checkConfig(){
		// ... and it has no config.
		return true;
	}
	
	@Override
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		if (!this.chestAccess.containsKey(playerName)){
			return new HashMap<Integer, Integer>();
		}
		
		return this.chestAccess.get(playerName);
	}
	
	@Override
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		if (!this.blocksPlaced.containsKey(playerName)){
			return new HashMap<Integer, Integer>();
		}
		
		return this.blocksPlaced.get(playerName);
	}
	
	@Override
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		if (!this.blocksBroken.containsKey(playerName)){
			return new HashMap<Integer, Integer>();
		}
		
		return this.blocksBroken.get(playerName);
	}
	
	@Override
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		HashMap<String, HashMap<Integer, Integer>> data = new HashMap<String, HashMap<Integer, Integer>>();
		
		data.put("broken", this.getBlocksBroken(playerName));
		data.put("placed", this.getBlocksPlaced(playerName));
		
		return data;
	}
	
}
