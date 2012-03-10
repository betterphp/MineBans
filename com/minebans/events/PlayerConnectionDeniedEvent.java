package com.minebans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minebans.bans.BanReason;
import com.minebans.bans.BanSeverity;

public class PlayerConnectionDeniedEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	private BanReason reason;
	private BanSeverity severity;
	
	public PlayerConnectionDeniedEvent(String playerName, BanReason reason, BanSeverity severity){
		this.playerName = playerName;
		this.reason = reason;
		this.severity = severity;
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
	
	public BanSeverity getBanSeverity(){
		return this.severity;
	}
	
}
