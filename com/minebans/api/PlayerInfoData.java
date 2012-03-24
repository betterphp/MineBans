package com.minebans.api;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PlayerInfoData {
	
	private Boolean isKnownCompromised;
	private Boolean shouldUnban;
	private Boolean bannedFromGroup;
	
	public PlayerInfoData(JSONObject response){
		JSONObject data = (JSONObject) response.get("player_info");
		
		this.isKnownCompromised = (Boolean) data.get("known_compromised");
		this.shouldUnban = (Boolean) data.get("should_unban");
		this.bannedFromGroup = (Boolean) data.get("banned_from_group");
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
	
	public Boolean bannedFromGroup(){
		return this.bannedFromGroup;
	}
	
}
