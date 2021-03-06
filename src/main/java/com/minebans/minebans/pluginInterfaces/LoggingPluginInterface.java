package com.minebans.minebans.pluginInterfaces;

import java.util.HashMap;

public abstract class LoggingPluginInterface extends PluginInterface {
	
	public abstract HashMap<Integer, Integer> getChestAccess(String playerName);
	
	public abstract HashMap<Integer, Integer> getBlocksPlaced(String playerName);
	
	public abstract HashMap<Integer, Integer> getBlocksBroken(String playerName);
	
	public abstract HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName);
	
}
