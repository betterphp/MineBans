package com.minebans.api;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PlayerJoinData {
	
	private PlayerInfoData infoData;
	private PlayerBanData banData;
	
	public PlayerJoinData(JSONObject responce){
		this.infoData = new PlayerInfoData(responce);
		this.banData = new PlayerBanData(responce);
	}
	
	public PlayerJoinData(String response) throws ParseException {
		this((JSONObject) (new JSONParser()).parse(response));
	}
	
	public PlayerInfoData getInfoData(){
		return this.infoData;
	}
	
	public PlayerBanData getBanData(){
		return this.banData;
	}
	
}
