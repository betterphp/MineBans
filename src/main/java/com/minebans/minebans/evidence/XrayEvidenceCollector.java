package com.minebans.minebans.evidence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;

import com.minebans.minebans.MineBans;

public class XrayEvidenceCollector extends EvidenceCollector {
	
	private MineBans plugin;
	
	private List<Integer> oreIds;
	
	public XrayEvidenceCollector(MineBans plugin){
		this.plugin = plugin;
		
		this.oreIds = Arrays.asList(
						Material.COAL_ORE.getId(),
						Material.IRON_ORE.getId(),
						Material.GOLD_ORE.getId(),
						Material.DIAMOND_ORE.getId(),
						Material.REDSTONE_ORE.getId(),
						Material.LAPIS_ORE.getId(),
						Material.MOSSY_COBBLESTONE.getId()
					  );
	}
	
	@Override
	public HashMap<String, HashMap<Integer, ?>> collect(String playerName){
		HashMap<String, HashMap<Integer, ?>> data = new HashMap<String, HashMap<Integer, ?>>();
		HashMap<Integer, Integer> totals = new HashMap<Integer, Integer>();
		HashMap<Integer, Float> fractions = new HashMap<Integer, Float>();
		
		HashMap<Integer, Integer> broken = plugin.loggingPlugin.getBlocksBroken(playerName);
		
		float total = 0F;
		
		for (Integer blockID : broken.keySet()){
			total += broken.get(blockID);
		}
		
		for (Integer blockID : broken.keySet()){
			if (this.oreIds.contains(blockID)){
				totals.put(blockID, broken.get(blockID));
				fractions.put(blockID, broken.get(blockID) / total);
			}
		}
		
		if (totals.size() > 0 && fractions.size() > 0){
			data.put("total", totals);
			data.put("fraction", fractions);
		}
		
		return data;
	}
	
}
