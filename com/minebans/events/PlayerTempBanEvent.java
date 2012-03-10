package com.minebans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTempBanEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	private Integer banDuration;
	
	public PlayerTempBanEvent(String playerName, Integer banDuration){
		this.playerName = playerName;
		this.banDuration = banDuration;
	}
	
	public HandlerList getHandlers(){
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
	
	public String getPlayerName(){
		return this.playerName;
	}
	
	public Integer getBanDuration(){
		return this.banDuration;
	}
	
}
