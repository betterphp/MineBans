package com.minebans.minebans.api.data;

import com.google.gson.JsonObject;

public class PlayerInfoData extends APIData {
	
	private boolean known_compromised;
	private boolean should_unban;
	
	private PlayerInfoData(){
		
	}
	
	public static PlayerInfoData fromString(String response){
		JsonObject object = parser.parse(response).getAsJsonObject();
		
		return gson.fromJson(object.get("player_info"), PlayerInfoData.class);
	}
	
	public Boolean isKnownCompromised(){
		return this.known_compromised;
	}
	
	public Boolean shouldUnban(){
		return this.should_unban;
	}
	
}
