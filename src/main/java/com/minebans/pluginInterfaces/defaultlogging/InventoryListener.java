package com.minebans.pluginInterfaces.defaultlogging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
	
	private DefaultLoggingPluginInterface plugin;
	
	private HashMap<String, HashMap<Integer, Integer>> inventories;
	private ArrayList<InventoryType> containerTypes;
	
	public InventoryListener(DefaultLoggingPluginInterface plugin){
		this.plugin = plugin;
		
		this.inventories = new HashMap<String, HashMap<Integer, Integer>>();
		this.containerTypes = new ArrayList<InventoryType>(3);
		
		this.containerTypes.add(InventoryType.CHEST);
		this.containerTypes.add(InventoryType.FURNACE);
		this.containerTypes.add(InventoryType.DISPENSER);
	}
	
	private HashMap<Integer, Integer> combineItemStacks(ItemStack[] items){
		HashMap<Integer, Integer> combined = new HashMap<Integer, Integer>();
		
		for (ItemStack item : items){
			if (item != null){
				int itemId = item.getType().getId();
				int amount = item.getAmount();
				
				if (itemId != Material.AIR.getId()){
					combined.put(itemId, (combined.containsKey(item)) ? combined.get(item) + amount : amount);
				}
			}
		}
		
		return combined;
	}
	
	private HashMap<Integer, Integer> getInventoryDiff(HashMap<Integer, Integer> before, HashMap<Integer, Integer> after){
		HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();
		
		for (Entry<Integer, Integer> item : before.entrySet()){
			Integer typeId = item.getKey();
			int amount = item.getValue();
			
			int change = (after.containsKey(typeId)) ? after.get(typeId) - amount : -amount;
			
			items.put(typeId, change);
		}
		
		return items;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event){
		HumanEntity human = event.getPlayer();
		
		if (!(human instanceof Player)){
			return;
		}
		
		Player player = (Player) human;
		String playerName = player.getName();
		
		InventoryView inventory = event.getView();
		InventoryType type = inventory.getType();
		
		if (!this.containerTypes.contains(type)){
			return;
		}
		
		this.inventories.put(playerName, this.combineItemStacks(inventory.getTopInventory().getContents()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event){
		HumanEntity human = event.getPlayer();
		
		if (human instanceof Player == false){
			return;
		}
		
		Player player = (Player) human;
		String playerName = player.getName();
		
		if (this.inventories.containsKey(playerName)){
			HashMap<Integer, Integer> before = this.inventories.get(playerName);
			HashMap<Integer, Integer> after = this.combineItemStacks(event.getView().getTopInventory().getContents());
			
			HashMap<Integer, Integer> chestAccess = plugin.chestAccess.get(playerName);
			
			if (chestAccess == null){
				chestAccess = new HashMap<Integer, Integer>();
				plugin.chestAccess.put(playerName, chestAccess);
			}
			
			for (Entry<Integer, Integer> item : this.getInventoryDiff(before, after).entrySet()){
				Integer itemId = item.getKey();
				Integer amount = item.getValue();
				
				chestAccess.put(itemId, (chestAccess.containsKey(itemId)) ? chestAccess.get(itemId) + amount : amount);
			}
			
			this.inventories.remove(playerName);
		}
	}
	
}
