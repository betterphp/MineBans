package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;

public abstract class PlayerBanCallback extends APICallback {
	
	public PlayerBanCallback(MineBans plugin){
		super(plugin);
	}
	
	@Override
	public void onSuccess(String response){  }
	
	public abstract void onFailure(Exception exception);
	
}
