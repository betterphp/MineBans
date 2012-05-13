package com.minebans.pluginInterfaces.hawkeye;

import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import com.minebans.MineBans;

import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

public class HawkEyeChestAccessCallback extends BaseCallback {
	
	private MineBans plugin;
	
	public Boolean complete;
	public HashMap<Integer, Integer> taken;
	
	public HawkEyeChestAccessCallback(MineBans plugin){
		this.plugin = plugin;
		
		this.complete = false;
		this.taken = new HashMap<Integer, Integer>();
	}
	
	public void error(SearchError error, String message){
		plugin.log.warn(plugin.formatMessage("HawkEye Error: " + error.name() + " " + message, false));
		
		this.complete = true;
	}
	
	public void execute(){
		String data;
		Integer blockId, amount, mult;
		
		for (DataEntry entry : results){
			data = entry.getSqlData();
			
			System.out.println(data);
			
			mult = (data.startsWith("@")) ? -1 : 1;
			
			for (HashMap<String, Integer> compressedInv : InventoryUtil.interpretDifferenceString(data)){
				for (ItemStack item : InventoryUtil.uncompressInventory(compressedInv)){
					blockId = item.getTypeId();
					amount = item.getAmount() * mult;
					
					this.taken.put(blockId, (this.taken.containsKey(blockId)) ? this.taken.get(blockId) + amount : amount);
				}
			}
		}
		
		this.complete = true;
	}
	
}
