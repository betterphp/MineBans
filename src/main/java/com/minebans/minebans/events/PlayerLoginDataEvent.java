package com.minebans.minebans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minebans.minebans.api.ConnectionDeniedReason;
import com.minebans.minebans.api.data.PlayerJoinInfoData;

public class PlayerLoginDataEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String playerName;
	private String playerAdress;
	private PlayerJoinInfoData joinData;
	
	private Boolean preventConnecton;
	private ConnectionDeniedReason reason;
	private String kickMessage;
	private String logMessage;
	
	public PlayerLoginDataEvent(String playerName, String playerAddress, PlayerJoinInfoData joinData){
		this.playerName = playerName;
		this.playerAdress = playerAddress;
		this.joinData = joinData;
		
		this.preventConnecton = false;
		this.reason = null;
		this.kickMessage = "";
		this.logMessage = "";
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
	
	public String getPlayerAddress(){
		return this.playerAdress;
	}
	
	public PlayerJoinInfoData getJoinData(){
		return this.joinData;
	}
	
	public Boolean isConnectionPrevented(){
		return this.preventConnecton;
	}
	
	public ConnectionDeniedReason getReason(){
		if (this.reason == null){
			this.reason = ConnectionDeniedReason.OTHER;
		}
		
		return this.reason;
	}
	
	public String getKickMessage(){
		if (this.kickMessage.equals("")){
			this.kickMessage = ConnectionDeniedReason.OTHER.getKickMessage();
		}
		
		return this.kickMessage;
	}
	
	public String getLogMessage(){
		if (this.logMessage.equals("")){
			this.logMessage = ConnectionDeniedReason.OTHER.getLogMessage();
		}
		
		return this.logMessage;
	}
	
	public void setPreventConnection(Boolean prevent){
		this.preventConnecton = prevent;
	}
	
	public void setReason(ConnectionDeniedReason reason){
		this.reason = reason;
	}
	
	public void setKickMessage(String message){
		this.kickMessage = message;
	}
	
	public void setLogMessage(String message){
		this.logMessage = message;
	}
	
}
