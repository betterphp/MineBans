package com.minebans.api.callback;

public abstract class PlayerUnbanCallback extends APICallback {
	
	public void onSuccess(String response){  }
	
	public abstract void onFailure(Exception exception);
	
}
