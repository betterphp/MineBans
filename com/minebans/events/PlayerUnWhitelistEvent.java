package com.minebans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUnWhitelistEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	
	public PlayerUnWhitelistEvent(String playerName){
		this.playerName = playerName;
	}
	
	public HandlerList getHandlers(){
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
	
	public String getUnWhitelistedName(){
		return this.playerName;
	}
	
}
