package com.minebans.minebans.api.data;

public class PlayerJoinInfoData {
	
	private PlayerInfoData infoData;
	private PlayerBansData bansData;
	
	private PlayerJoinInfoData(){
		
	}
	
	public static PlayerJoinInfoData fromString(String response){
		PlayerJoinInfoData data = new PlayerJoinInfoData();
		
		data.infoData = PlayerInfoData.fromString(response);
		data.bansData = PlayerBansData.fromString(response);
		
		return data;
	}
	
	public PlayerInfoData getInfoData(){
		return this.infoData;
	}
	
	public PlayerBansData getBansData(){
		return this.bansData;
	}
	
}
