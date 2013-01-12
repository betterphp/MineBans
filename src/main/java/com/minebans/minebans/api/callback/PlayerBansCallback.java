package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.data.PlayerBansData;

public abstract class PlayerBansCallback extends APICallback {
	
	public PlayerBansCallback(MineBans plugin){
		super(plugin);
	}
	
	@Override
	public void onSuccess(String response){
		this.onSuccess(PlayerBansData.fromString(response));
	}
	
	public abstract void onSuccess(PlayerBansData data);
	
	public abstract void onFailure(Exception exception);
	
}
