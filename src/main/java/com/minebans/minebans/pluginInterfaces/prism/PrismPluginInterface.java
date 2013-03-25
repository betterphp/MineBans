package com.minebans.minebans.pluginInterfaces.prism;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.actions.Handler;
import me.botsko.prism.actions.ItemStackAction;
import me.botsko.prism.commandlibs.Flag;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.pluginInterfaces.LoggingPluginInterface;

public class PrismPluginInterface extends LoggingPluginInterface {
	
	private MineBans plugin;
	private Prism prism;
	
	public PrismPluginInterface(MineBans plugin){
		this.plugin = plugin;
		this.prism = (Prism) plugin.pluginManager.getPlugin("Prism");
	}
	
	@Override
	public boolean pluginEnabled(){
		return (this.prism != null);
	}
	
	@Override
	public String getPluginName(){
		return "Prism";
	}
	
	@Override
	public boolean checkConfig(){
		FileConfiguration config = this.prism.getConfig();
		
		if (config.getStringList("prism.ignore.worlds").size() > 0){
			plugin.log.warn("To provide the best data Prism should be enabled for all worlds.");
		}
		
		for (String event : new String[]{"block-break", "block-place", "item-remove", "item-insert"}){
			if (!config.getBoolean("prism.tracking." + event)){
				plugin.log.warn("To provide the best data Prism should be set to track " + event + ".");
			}
		}
		
		return true;
	}
	
	@Override
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		HashMap<Integer, Integer> data = new HashMap<Integer, Integer>();
		
		QueryParameters parameters = new QueryParameters();
		parameters.addPlayerName(playerName);
		parameters.addActionType("item-remove");
		parameters.addActionType("item-insert");
		parameters.addFlag(Flag.NO_GROUP);
		parameters.setSinceTime((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date(System.currentTimeMillis() - 86400000)));
		
		ActionsQuery action = new ActionsQuery(prism);
		QueryResult result = action.lookup(parameters);
		
		List<Handler> actions = result.getActionResults();
		
		if (actions != null){
			for (Handler handler : actions){
				int blockId = handler.getBlockId();
				int amount = ((ItemStackAction) handler).getActionData().amt;
				
				if (handler.getType().getName().equals("item-remove")){
					amount *= -1;
				}
				
				data.put(blockId, (data.containsKey(blockId)) ? data.get(blockId) + amount : amount);
			}
		}
		
		return data;
	}
	
	@Override
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		HashMap<Integer, Integer> placed = new HashMap<Integer, Integer>();
		
		QueryParameters parameters = new QueryParameters();
		parameters.addPlayerName(playerName);
		parameters.addActionType("block-place");
		parameters.addFlag(Flag.NO_GROUP);
		parameters.setSinceTime((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date(System.currentTimeMillis() - 86400000)));
		
		ActionsQuery action = new ActionsQuery(prism);
		QueryResult result = action.lookup(parameters);
		
		List<Handler> actions = result.getActionResults();
		
		if (actions != null){
			for (Handler handler : actions){
				int blockId = handler.getBlockId();
				
				placed.put(blockId, (placed.containsKey(blockId)) ? placed.get(blockId) + 1 : 1);
			}
		}
		
		return placed;
	}
	
	@Override
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		HashMap<Integer, Integer> broken = new HashMap<Integer, Integer>();
		
		QueryParameters parameters = new QueryParameters();
		parameters.addPlayerName(playerName);
		parameters.addActionType("block-break");
		parameters.addFlag(Flag.NO_GROUP);
		parameters.setSinceTime((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date(System.currentTimeMillis() - 86400000)));
		
		ActionsQuery action = new ActionsQuery(prism);
		QueryResult result = action.lookup(parameters);
		
		List<Handler> actions = result.getActionResults();
		
		if (actions != null){
			for (Handler handler : actions){
				int blockId = handler.getBlockId();
				
				broken.put(blockId, (broken.containsKey(blockId)) ? broken.get(blockId) + 1 : 1);
			}
		}
		
		return broken;
	}
	
	@Override
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		HashMap<String, HashMap<Integer, Integer>> data = new HashMap<String, HashMap<Integer, Integer>>();
		
		HashMap<Integer, Integer> broken = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> placed = new HashMap<Integer, Integer>();
		
		QueryParameters parameters = new QueryParameters();
		parameters.addPlayerName(playerName);
		parameters.addActionType("block-place");
		parameters.addActionType("block-break");
		parameters.addFlag(Flag.NO_GROUP);
		parameters.setSinceTime((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date(System.currentTimeMillis() - 86400000)));
		
		ActionsQuery action = new ActionsQuery(prism);
		QueryResult result = action.lookup(parameters);
		
		List<Handler> actions = result.getActionResults();
		
		if (actions != null){
			for (Handler handler : actions){
				int blockId = handler.getBlockId();
				
				if (handler.getType().getName().equals("block-break")){
					broken.put(blockId, (broken.containsKey(blockId)) ? broken.get(blockId) + 1 : 1);
				}else{
					placed.put(blockId, (placed.containsKey(blockId)) ? placed.get(blockId) + 1 : 1);
				}
			}
		}
		
		data.put("broken", broken);
		data.put("placed", placed);
		
		return data;
	}
	
}
