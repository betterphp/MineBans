package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.data.PlayerJoinInfoData;

public abstract class PlayerJoinInfoCallback extends APICallback {
	
	public PlayerJoinInfoCallback(MineBans plugin){
		super(plugin);
	}
	
	public void onSuccess(String response){
		this.onSuccess(PlayerJoinInfoData.fromString(response));
	}
	
	public abstract void onSuccess(PlayerJoinInfoData data);
	
	public abstract void onFailure(Exception exception);
	
}
