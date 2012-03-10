package com.minebans.pluginInterfaces;

import java.util.HashMap;

public abstract class LoggingPluginInterface extends PluginInterface {
	
	public abstract HashMap<Short, Integer> getChestAccess(String playerName);
	
	public abstract HashMap<Integer, Integer> getBlocksPlaced(String playerName);
	
	public abstract HashMap<Integer, Integer> getBlocksBroken(String playerName);
	
	public abstract HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName);
	
}
