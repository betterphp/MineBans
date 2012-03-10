package com.minebans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minebans.bans.BanReason;

public class PlayerGlobalBanEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	private BanReason reason;
	
	public PlayerGlobalBanEvent(String playerName, BanReason reason){
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
	
	public BanReason getBanReason(){
		return this.reason;
	}
	
}
