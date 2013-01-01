package com.minebans.minebans.api.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PlayerJoinInfoData {
	
	private PlayerInfoData infoData;
	private PlayerBansData bansData;
	
	public PlayerJoinInfoData(JSONObject responce){
		this.infoData = new PlayerInfoData(responce);
		this.bansData = new PlayerBansData(responce);
	}
	
	public PlayerJoinInfoData(String response) throws ParseException {
		this((JSONObject) (new JSONParser()).parse(response));
	}
	
	public PlayerInfoData getInfoData(){
		return this.infoData;
	}
	
	public PlayerBansData getBansData(){
		return this.bansData;
	}
	
}
