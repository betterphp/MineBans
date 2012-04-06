package com.minebans.pluginInterfaces.defaultantispam;

import java.util.Collections;

public class CounterResetTask implements Runnable {
	
	private DefaultAntiSpamPluginInterface collector;
	
	public CounterResetTask(DefaultAntiSpamPluginInterface collector){
		this.collector = collector;
	}

	public void run(){
		Integer current, max;
		
		for (String playerName : collector.messageCounter.keySet()){
			max = 0;
			
			current = Collections.max(collector.messageCounter.get(playerName).values());
			max = collector.maxViolationLevel.containsKey(playerName) ? collector.maxViolationLevel.get(playerName) : 0;
			
			if (current > max){
				max = current;
			}
			
			collector.maxViolationLevel.put(playerName, max);
		}
		
		collector.messageCounter.clear();
	}
	
}
