package com.minebans.api;

import java.net.URL;

import org.json.simple.JSONObject;

public class APIRequest {
	
	public URL url;
	public JSONObject json;
	public APIResponseCallback callback;
	public int timeout;
	
	public boolean complete;
	
	public APIRequest(URL url, JSONObject json, APIResponseCallback callback, int timeout){
		this.url = url;
		this.json = json;
		this.callback = callback;
		this.timeout = timeout;
		this.complete = false;
	}
	
	public APIRequest(URL url, JSONObject json, int timeout){
		this.url = url;
		this.json = json;
		this.timeout = timeout;
		this.complete = false;
	}
	
	public APIRequest(URL url, JSONObject json, APIResponseCallback callback){
		this(url, json, callback, 250);
	}
	
}