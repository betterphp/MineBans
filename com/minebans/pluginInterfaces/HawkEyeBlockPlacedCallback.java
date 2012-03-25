package com.minebans.pluginInterfaces;

import java.util.HashMap;

import org.bukkit.Material;

import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.entry.DataEntry;

public class HawkEyeBlockPlacedCallback extends BaseCallback {
	
	public Boolean gotData;
	public HashMap<Integer, Integer> placed;
	
	public HawkEyeBlockPlacedCallback(){
		this.gotData = false;
		this.placed = new HashMap<Integer, Integer>();
	}
	
	public void error(SearchError error, String message){  }
	
	public void execute(){
		String data;
		Integer blockId;
		
		int start, end;
		
		for (DataEntry entry : results){
			data = entry.getStringData();
			
			start = data.lastIndexOf(" ") + 1;
			end = data.lastIndexOf(":");
			
			if (end > 0){
				blockId = Material.getMaterial(data.substring(start, end)).getId();
			}else{
				blockId = Material.getMaterial(data.substring(start)).getId();
			}
			
			this.placed.put(blockId, (this.placed.containsKey(blockId)) ? this.placed.get(blockId) + 1 : 1);
		}
		
		this.gotData = true;
	}
	
}
