package com.minebans.api;

public interface APIResponseCallback {
	
	public void onSuccess(String response);
	
	public void onFailure(Exception e);
	
}
