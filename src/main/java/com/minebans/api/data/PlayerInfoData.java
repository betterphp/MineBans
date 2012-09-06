package com.minebans.api.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PlayerInfoData {
	
	private Boolean isKnownCompromised;
	private Boolean shouldUnban;
	
	public PlayerInfoData(JSONObject response){
		JSONObject data = (JSONObject) response.get("player_info");
		
		this.isKnownCompromised = (Boolean) data.get("known_compromised");
		this.shouldUnban = (Boolean) data.get("should_unban");
	}
	
	public PlayerInfoData(String response) throws ParseException {
		this((JSONObject) (new JSONParser()).parse(response));
	}
	
	public Boolean isKnownCompromised(){
		return this.isKnownCompromised;
	}
	
	public Boolean shouldUnban(){
		return this.shouldUnban;
	}
	
}
