package com.minebans.minebans.pluginInterfaces.hawkeye;

import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import com.minebans.minebans.MineBans;

import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.entry.ContainerEntry;
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
		for (DataEntry entry : this.results){
			String data = ((ContainerEntry) entry).getStringData();
			
			for (String compressed : data.split("@")){
				ItemStack item = InventoryUtil.uncompressItem(compressed);
				
				int blockId = item.getTypeId();
				int amount = item.getAmount();
				
				if (compressed.startsWith("-")){
					amount *= -1;
				}
				
				this.taken.put(blockId, (this.taken.containsKey(blockId)) ? this.taken.get(blockId) + amount : amount);
			}
		}
		
		this.complete = true;
	}
	
}
