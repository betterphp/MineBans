package com.minebans.minebans.api.callback;

import org.json.simple.parser.ParseException;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.data.PlayerJoinInfoData;

public abstract class PlayerJoinInfoCallback extends APICallback {
	
	public PlayerJoinInfoCallback(MineBans plugin){
		super(plugin);
	}
	
	public void onSuccess(String response){
		try{
			this.onSuccess(new PlayerJoinInfoData(response));
		}catch (ParseException e){
			this.onFailure(e);
		}
	}
	
	public abstract void onSuccess(PlayerJoinInfoData data);
	
	public abstract void onFailure(Exception exception);
	
}
