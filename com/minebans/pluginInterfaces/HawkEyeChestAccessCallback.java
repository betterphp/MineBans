package com.minebans.pluginInterfaces;

import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

public class HawkEyeChestAccessCallback extends BaseCallback {
	
	public Boolean gotData;
	public HashMap<Integer, Integer> taken;
	
	public HawkEyeChestAccessCallback(){
		this.gotData = false;
		this.taken = new HashMap<Integer, Integer>();
	}
	
	public void error(SearchError error, String message){  }
	
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
		
		this.gotData = true;
	}
	
}
