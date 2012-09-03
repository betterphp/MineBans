package com.minebans.api;

import java.net.URL;
import java.util.UUID;

import org.json.simple.JSONObject;

public class APIRequest {
	
	public URL url;
	public JSONObject json;
	public int timeout;
	public boolean complete;
	
	public APIResponseCallback callback;
	public String requestKey;
	
	public APIRequest(URL url, JSONObject json, int timeout){
		this.url = url;
		this.json = json;
		this.timeout = timeout;
		this.complete = false;
		
		this.requestKey = UUID.randomUUID().toString();
		
		if (this.json != null){
			this.json.put("request_key", this.requestKey);
		}
	}
	
	public APIRequest(URL url, JSONObject json, APIResponseCallback callback, int timeout){
		this(url, json, timeout);
		
		this.callback = callback;
	}
	
	public APIRequest(URL url, JSONObject json, APIResponseCallback callback){
		this(url, json, callback, 250);
	}
	
}