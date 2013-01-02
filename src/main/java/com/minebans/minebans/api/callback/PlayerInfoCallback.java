package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.data.PlayerInfoData;

public abstract class PlayerInfoCallback extends APICallback {
	
	public PlayerInfoCallback(MineBans plugin){
		super(plugin);
	}
	
	public void onSuccess(String response){
		this.onSuccess(PlayerInfoData.fromString(response));
	}
	
	public abstract void onSuccess(PlayerInfoData data);
	
	public abstract void onFailure(Exception exception);
	
}
