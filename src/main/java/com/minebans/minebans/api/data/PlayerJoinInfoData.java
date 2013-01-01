package com.minebans.minebans.api.data;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class PlayerJoinInfoData {
	
	private PlayerInfoData infoData;
	private PlayerBansData bansData;
	
	public PlayerJoinInfoData(StringMap<?> responce){
		this.infoData = new PlayerInfoData(responce);
		this.bansData = new PlayerBansData(responce);
	}
	
	public PlayerJoinInfoData(String response){
		this((new Gson()).fromJson(response, StringMap.class));
	}
	
	public PlayerInfoData getInfoData(){
		return this.infoData;
	}
	
	public PlayerBansData getBansData(){
		return this.bansData;
	}
	
}
