package com.minebans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minebans.bans.BanType;

public class PlayerBanEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	private BanType banType;
	
	public PlayerBanEvent(String playerName, BanType banType){
		this.playerName = playerName;
		this.banType = banType;
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
	
}
