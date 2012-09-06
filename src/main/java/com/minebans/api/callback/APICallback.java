package com.minebans.api.callback;

public abstract class APICallback {
	
	public abstract void onSuccess(String response);
	
	public abstract void onFailure(Exception exception);
	
}
