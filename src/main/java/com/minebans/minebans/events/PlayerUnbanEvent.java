package com.minebans.minebans.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minebans.minebans.bans.BanType;

public class PlayerUnbanEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	private BanType banType;
	
	private boolean isCancelled;
	
	public PlayerUnbanEvent(String playerName, BanType banType){
		this.playerName = playerName;
		this.banType = banType;
		
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
	
	public BanType getBanType(){
		return this.banType;
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
