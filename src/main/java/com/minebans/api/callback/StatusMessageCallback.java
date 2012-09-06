package com.minebans.api.callback;

import com.minebans.api.data.StatusMessageData;

public abstract class StatusMessageCallback extends APICallback {
	
	public void onSuccess(String response){
		this.onSuccess(new StatusMessageData(response));
	}
	
	public abstract void onSuccess(StatusMessageData data);
	
	public abstract void onFailure(Exception exception);
	
}
