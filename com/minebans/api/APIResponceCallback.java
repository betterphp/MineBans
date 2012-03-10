package com.minebans.api;

public interface APIResponceCallback {
	
	public void onSuccess(String responce);
	
	public void onFailure(Exception e);
	
}
