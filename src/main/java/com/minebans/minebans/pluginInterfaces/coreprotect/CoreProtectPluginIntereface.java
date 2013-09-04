package com.minebans.minebans.pluginInterfaces.coreprotect;

import java.util.HashMap;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.ParseResult;
import net.coreprotect.model.Config;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.pluginInterfaces.LoggingPluginInterface;

public class CoreProtectPluginIntereface extends LoggingPluginInterface {
	
	private MineBans plugin;
	private CoreProtect coreProtect;
	private CoreProtectAPI coreProtectAPI;
	
	public CoreProtectPluginIntereface(MineBans plugin){
		this.plugin = plugin;
		this.coreProtect = (CoreProtect) plugin.getServer().getPluginManager().getPlugin("CoreProtect");
		this.coreProtectAPI = this.coreProtect.getAPI();
	}
	
	@Override
	public boolean pluginEnabled(){
		return (this.coreProtect != null);
	}
	
	@Override
	public String getPluginName(){
		return "CoreProtect";
	}
	
	@Override
	public boolean checkConfig(){
		if (!Config.config.get("api-enabled").equals(1)){
			plugin.log.warn("To provide the best data you need to enable the CoreProtect API.");
		}
		
		if (!Config.config.get("block-place").equals(1)){
			plugin.log.warn("To provide the best data CoreProtect should be set to log block changes made by players.");
		}
		
		if (!Config.config.get("item-transactions").equals(1)){
			plugin.log.warn("To provide the best data CoreProtect should be set to log item transactions made by players.");
		}
		
		return true;
	}
	
	@Override
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		HashMap<Integer, Integer> taken = new HashMap<Integer, Integer>();
		
		//TODO: Work this out
		
		return taken;
	}
	
	private HashMap<Integer, Integer> getblockChanges(String playerName, int actionId){
		HashMap<Integer, Integer> blocks = new HashMap<Integer, Integer>();
		
		for (String[] entry : this.coreProtectAPI.performLookup(playerName, 86400, 0, null, null, null)){
			ParseResult result = this.coreProtectAPI.parseResult(entry);
			
			if (result.getActionId() == actionId){
				int blockId = result.getTypeId();
				
				blocks.put(blockId, (blocks.containsKey(blockId)) ? blocks.get(blockId) + 1 : 1);
			}
		}
		
		return blocks;
	}
	
	@Override
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		return this.getblockChanges(playerName, 1);
	}
	
	@Override
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		return this.getblockChanges(playerName, 0);
	}
	
	@Override
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		HashMap<String, HashMap<Integer, Integer>> data = new HashMap<String, HashMap<Integer, Integer>>();
		
		data.put("broken", this.getBlocksBroken(playerName));
		data.put("placed", this.getBlocksPlaced(playerName));
		
		return data;
	}
	
}
