package com.minebans.minebans.api.request;

import java.net.URL;
import java.util.UUID;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.APICallback;

public abstract class APIRequest<Callback extends APICallback> {
	
	protected transient MineBans plugin;
	
	protected transient URL url;
	protected transient Integer timeout;
	
	protected String request_key; // Bad naming conventions for JSON backward compatibility :(
	private transient Boolean complete;
	private transient Integer attempts;
	
	protected transient Callback callback;
	
	public APIRequest(MineBans plugin, URL url, int timeout){
		this.plugin = plugin;
		
		this.url = url;
		this.timeout = timeout;
		
		this.request_key = UUID.randomUUID().toString();
		this.complete = false;
		this.attempts = 0;
	}
	
	public URL getURL(){
		synchronized (this.url){
			return this.url;
		}
	}
	
	public int getTimeout(){
		synchronized (this.timeout){
			return this.timeout;
		}
	}
	
	public String getRequestKey(){
		synchronized (this.request_key){
			return this.request_key;
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
