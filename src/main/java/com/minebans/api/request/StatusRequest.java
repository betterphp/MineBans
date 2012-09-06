package com.minebans.api.request;

import com.minebans.MineBans;
import com.minebans.api.callback.StatusCallback;
import com.minebans.api.data.StatusData;

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
			return new StatusData(plugin.api.getRequestHandler().processRequest(this));
		}catch (Exception e){
			plugin.api.handleException(e);
			
			return null;
		}
	}
	
	public void onSuccess(String response){
		synchronized (this.callback){
			this.callback.onSuccess(response);
		}
	}
	
	public void onFailure(Exception e){
		synchronized (this.callback){
			this.callback.onFailure(e);
		}
	}
	
}
