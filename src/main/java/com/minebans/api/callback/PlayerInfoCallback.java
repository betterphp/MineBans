package com.minebans.api.callback;

import org.json.simple.parser.ParseException;

import com.minebans.MineBans;
import com.minebans.api.data.PlayerInfoData;

public abstract class PlayerInfoCallback extends APICallback {
	
	public PlayerInfoCallback(MineBans plugin){
		super(plugin);
	}
	
	public void onSuccess(String response){
		try{
			this.onSuccess(new PlayerInfoData(response));
		}catch (ParseException e){
			this.onFailure(e);
		}
	}
	
	public abstract void onSuccess(PlayerInfoData data);
	
	public abstract void onFailure(Exception exception);
	
}
