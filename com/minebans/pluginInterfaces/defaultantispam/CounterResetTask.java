package com.minebans.pluginInterfaces.defaultantispam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

public class CounterResetTask implements Runnable {
	
	private DefaultAntiSpamPluginInterface collector;
	
	public CounterResetTask(DefaultAntiSpamPluginInterface collector){
		this.collector = collector;
	}

	public void run(){
		Integer current;
		String playerName;
		HashMap<String, Integer> messageCount;
		
		for (Entry<String, HashMap<String, Integer>> entry : collector.messageCounter.entrySet()){
			playerName = entry.getKey();
			messageCount = entry.getValue();
			
			current = (messageCount.size() > 0) ? Collections.max(messageCount.values()) : 0;
			
			if (collector.maxViolationLevel.containsKey(playerName) == false || current > collector.maxViolationLevel.get(playerName)){
				collector.maxViolationLevel.put(playerName, current);
			}
		}
		
		collector.messageCounter.clear();
	}
	
}
