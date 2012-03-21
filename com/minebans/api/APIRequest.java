package com.minebans.api;

import org.json.simple.JSONObject;

public class APIRequest {
	
	public JSONObject json;
	public APIResponseCallback callback;
	public int timeout;
	
	public boolean complete;
	
	public APIRequest(JSONObject json, APIResponseCallback callback, int timeout){
		this.json = json;
		this.callback = callback;
		this.timeout = timeout;
		this.complete = false;
	}
	
	public APIRequest(JSONObject json, int timeout){
		this.json = json;
		this.timeout = timeout;
		this.complete = false;
	}
	
	public APIRequest(JSONObject json, APIResponseCallback callback){
		this(json, callback, 250);
	}
	
}
