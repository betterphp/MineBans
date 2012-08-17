package com.minebans.pluginInterfaces.defaultlogging;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
	
	private DefaultLoggingPluginInterface plugin;
	
	public BlockListener(DefaultLoggingPluginInterface plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event){
		String playerName = event.getPlayer().getName();
		Integer blockId = event.getBlock().getTypeId();
		
		HashMap<Integer, Integer> blocksBroken = plugin.blocksBroken.get(playerName);
		
		if (blocksBroken == null){
			blocksBroken = new HashMap<Integer, Integer>();
			plugin.blocksBroken.put(playerName, blocksBroken);
		}
		
		blocksBroken.put(blockId, (blocksBroken.containsKey(blockId)) ? blocksBroken.get(blockId) + 1 : 1);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event){
		String playerName = event.getPlayer().getName();
		Integer blockId = event.getBlock().getTypeId();
		
		HashMap<Integer, Integer> blocksPlaced = plugin.blocksPlaced.get(playerName);
		
		if (blocksPlaced == null){
			blocksPlaced = new HashMap<Integer, Integer>();
			plugin.blocksPlaced.put(playerName, blocksPlaced);
		}
		
		blocksPlaced.put(blockId, (blocksPlaced.containsKey(blockId)) ? blocksPlaced.get(blockId) + 1 : 1);
	}
	
}
