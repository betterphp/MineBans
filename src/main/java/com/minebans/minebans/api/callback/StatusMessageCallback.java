package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.data.StatusMessageData;

public abstract class StatusMessageCallback extends APICallback {
	
	public StatusMessageCallback(MineBans plugin){
		super(plugin);
	}
	
	@Override
	public void onSuccess(String response){
		this.onSuccess(StatusMessageData.fromString(response));
	}
	
	public abstract void onSuccess(StatusMessageData data);
	
	public abstract void onFailure(Exception exception);
	
}
