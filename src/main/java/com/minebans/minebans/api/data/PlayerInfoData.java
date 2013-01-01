package com.minebans.minebans.api.data;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class PlayerInfoData {
	
	private Boolean isKnownCompromised;
	private Boolean shouldUnban;
	
	public PlayerInfoData(StringMap<?> response){
		StringMap<?> data = (StringMap<?>) response.get("player_info");
		
		this.isKnownCompromised = (Boolean) data.get("known_compromised");
		this.shouldUnban = (Boolean) data.get("should_unban");
	}
	
	public PlayerInfoData(String response){
		this((new Gson()).fromJson(response, StringMap.class));
	}
	
	public Boolean isKnownCompromised(){
		return this.isKnownCompromised;
	}
	
	public Boolean shouldUnban(){
		return this.shouldUnban;
	}
	
}
