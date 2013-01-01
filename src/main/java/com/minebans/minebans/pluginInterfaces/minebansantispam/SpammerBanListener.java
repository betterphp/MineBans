package com.minebans.minebans.pluginInterfaces.minebansantispam;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.minebans.antispam.events.PlayerSpamDetectedEvent;

public class SpammerBanListener implements Listener {
	
	private ArrayList<String> spammerList;
	
	public SpammerBanListener(ArrayList<String> spammerList){
		this.spammerList = spammerList;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerSpamDetected(PlayerSpamDetectedEvent event){
		String lowerName = event.getPlayerName().toLowerCase();
		
		if (!this.spammerList.contains(lowerName)){
			this.spammerList.add(lowerName);
		}
	}
	
}
