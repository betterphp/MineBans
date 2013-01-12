package com.minebans.minebans.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minebans.minebans.bans.BanReason;

public class PlayerGlobalBanEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	private BanReason reason;
	
	private boolean isCancelled;
	
	public PlayerGlobalBanEvent(String playerName, BanReason reason){
		this.playerName = playerName;
		this.reason = reason;
		
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
	
	public BanReason getBanReason(){
		return this.reason;
	}
	
	@Override
	public boolean isCancelled(){
		return this.isCancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled){
		this.isCancelled = cancelled;
	}
	
}
