package com.minebans.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLocalBanEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	
	private boolean isCancelled;
	
	public PlayerLocalBanEvent(String playerName){
		this.playerName = playerName;
		
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
	
	public boolean isCancelled(){
		return this.isCancelled;
	}
	
	public void setCancelled(boolean cancelled){
		this.isCancelled = cancelled;
	}
	
}
