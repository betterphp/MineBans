package com.minebans.api;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PlayerInfoData {
	
	private Boolean isKnownCompromised;
	private Boolean shouldUnban;
	
	public PlayerInfoData(JSONObject responce){
		JSONObject data = (JSONObject) responce.get("player_info");
		
		this.isKnownCompromised = (Boolean) data.get("known_compromised");
		this.shouldUnban = (Boolean) data.get("should_unban");
	}
	
	public PlayerInfoData(String responce) throws ParseException {
		this((JSONObject) (new JSONParser()).parse(responce));
	}
	
	public Boolean isKnownCompromised(){
		return this.isKnownCompromised;
	}
	
	public Boolean shouldUnban(){
		return this.shouldUnban;
	}
	
}
