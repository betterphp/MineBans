package com.minebans.minebans.api.callback;

import org.json.simple.parser.ParseException;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.data.PlayerBansData;

public abstract class PlayerBansCallback extends APICallback {
	
	public PlayerBansCallback(MineBans plugin){
		super(plugin);
	}
	
	public void onSuccess(String response){
		try{
			this.onSuccess(new PlayerBansData(response));
		}catch (ParseException e){
			this.onFailure(e);
		}
	}
	
	public abstract void onSuccess(PlayerBansData data);
	
	public abstract void onFailure(Exception exception);
	
}
