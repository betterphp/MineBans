package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.data.StatusData;

public abstract class StatusCallback extends APICallback {
	
	public StatusCallback(MineBans plugin){
		super(plugin);
	}
	
	public void onSuccess(String response){
		this.onSuccess(StatusData.fromString(response));
	}
	
	public abstract void onSuccess(StatusData data);
	
	public abstract void onFailure(Exception exception);
	
}
