package com.minebans.pluginInterfaces.hawkeye;

import java.util.HashMap;

import org.bukkit.Material;

import com.minebans.MineBans;

import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.entry.DataEntry;

public class HawkEyeBlockBrokenCallback extends BaseCallback {
	
	private MineBans plugin;
	
	public Boolean complete;
	public HashMap<Integer, Integer> broken;
	
	public HawkEyeBlockBrokenCallback(MineBans plugin){
		this.plugin = plugin;
		
		this.complete = false;
		this.broken = new HashMap<Integer, Integer>();
	}
	
	public void error(SearchError error, String message){
		plugin.log.warn(plugin.formatMessage("HawkEye Error: " + error.name() + " " + message, false));
		
		this.complete = true;
	}
	
	public void execute(){
		String data;
		Integer blockId;
		
		int end;
		
		for (DataEntry entry : results){
			data = entry.getStringData();
			
			end = data.lastIndexOf(":");
			
			if (end > 0){
				blockId = Material.getMaterial(data.substring(0, end)).getId();
			}else{
				blockId = Material.getMaterial(data).getId();
			}
			
			this.broken.put(blockId, (this.broken.containsKey(blockId)) ? this.broken.get(blockId) + 1 : 1);
		}
		
		this.complete = true;
	}
	
}
