package com.minebans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minebans.api.ConnectionAllowedReason;

public class PlayerConnectionAllowedEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	private ConnectionAllowedReason reason;
	
	public PlayerConnectionAllowedEvent(String playerName, ConnectionAllowedReason reason){
		this.playerName = playerName;
		this.reason = reason;
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
	
	public ConnectionAllowedReason getReason(){
		return this.reason;
	}
	
}
