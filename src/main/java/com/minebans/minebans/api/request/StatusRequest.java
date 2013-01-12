package com.minebans.minebans.api.request;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.StatusCallback;
import com.minebans.minebans.api.data.StatusData;

public class StatusRequest extends APIRequest<StatusCallback> {
	
	@SuppressWarnings("unchecked")
	public StatusRequest(MineBans plugin, int timeout, String issuedBy){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.json.put("action", "get_system_status");
		this.json.put("issued_by", issuedBy);
	}
	
	public StatusRequest(MineBans plugin, String issuedBy){
		this(plugin, 8000, issuedBy);
	}
	
	public StatusData process(){
		try{
			return StatusData.fromString(plugin.api.getRequestHandler().processRequest(this));
		}catch (Exception e){
			plugin.api.handleException(e);
			
			return null;
		}
	}
	
	@Override
	public void onSuccess(String response){
		synchronized (this.callback){
			this.callback.onSuccess(response);
		}
	}
	
	@Override
	public void onFailure(Exception e){
		synchronized (this.callback){
			this.callback.onFailure(e);
		}
	}
	
}
