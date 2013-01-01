package com.minebans.minebans.api.request;

import java.net.URL;
import java.util.UUID;

import org.json.simple.JSONObject;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.APICallback;

public abstract class APIRequest<Callback extends APICallback> {
	
	protected MineBans plugin;
	
	protected URL url;
	protected JSONObject json;
	protected Integer timeout;
	
	private String requestKey;
	private Boolean complete;
	private Integer attempts;
	
	protected Callback callback;
	
	public APIRequest(MineBans plugin, URL url, int timeout){
		this.plugin = plugin;
		
		this.url = url;
		this.json = new JSONObject();
		this.timeout = timeout;
		
		this.requestKey = UUID.randomUUID().toString();
		this.complete = false;
		this.attempts = 0;
	}
	
	public URL getURL(){
		synchronized (this.url){
			return this.url;
		}
	}
	
	public JSONObject getJSON(){
		synchronized (this.json){
			return this.json;
		}
	}
	
	public int getTimeout(){
		synchronized (this.timeout){
			return this.timeout;
		}
	}
	
	public String getRequestKey(){
		synchronized (this.requestKey){
			return this.requestKey;
		}
	}
	
	public boolean isComplete(){
		synchronized (this.complete){
			return this.complete;
		}
	}
	
	public void setComplete(boolean complete){
		synchronized (this.complete){
			this.complete = complete;
		}
	}
	
	public int getAttempts(){
		synchronized (this.attempts){
			return this.attempts;
		}
	}
	
	public void addAttempt(){
		synchronized (this.attempts){
			++this.attempts;
		}
	}
	
	public void process(Callback callback){
		this.callback = callback;
		
		plugin.api.getRequestHandler().addRequest(this);
	}
	
	public abstract void onSuccess(String response);
	
	public abstract void onFailure(Exception e);
	
}
