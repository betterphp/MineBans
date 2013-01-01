package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.data.PlayerBansData;

public abstract class PlayerBansCallback extends APICallback {
	
	public PlayerBansCallback(MineBans plugin){
		super(plugin);
	}
	
	public void onSuccess(String response){
		this.onSuccess(new PlayerBansData(response));
	}
	
	public abstract void onSuccess(PlayerBansData data);
	
	public abstract void onFailure(Exception exception);
	
}
