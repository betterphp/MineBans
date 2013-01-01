package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;

public abstract class APICallback {
	
	protected MineBans plugin;
	
	public APICallback(MineBans plugin){
		this.plugin = plugin;
	}
	
	public abstract void onSuccess(String response);
	
	public abstract void onFailure(Exception exception);
	
}
