package com.minebans.api.callback;

public abstract class PlayerBanCallback extends APICallback {
	
	public void onSuccess(String response){  }
	
	public abstract void onFailure(Exception exception);
	
}
