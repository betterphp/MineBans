package com.minebans.minebans.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTempBanEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	private Integer banDuration;
	
	private boolean isCancelled;
	
	public PlayerTempBanEvent(String playerName, Integer banDuration){
		this.playerName = playerName;
		this.banDuration = banDuration;
		
		this.isCancelled = false;
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
	
	public boolean isCancelled(){
		return this.isCancelled;
	}
	
	public void setCancelled(boolean cancelled){
		this.isCancelled = cancelled;
	}
	
}
