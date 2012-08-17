package com.minebans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minebans.api.ConnectionDeniedReason;

public class PlayerConnectionDeniedEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	private ConnectionDeniedReason reason;
	
	public PlayerConnectionDeniedEvent(String playerName, ConnectionDeniedReason reason){
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
	
	public ConnectionDeniedReason getReason(){
		return this.reason;
	}
	
}
