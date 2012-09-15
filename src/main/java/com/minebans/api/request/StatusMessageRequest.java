package com.minebans.api.request;

import com.minebans.MineBans;
import com.minebans.api.callback.StatusMessageCallback;
import com.minebans.api.data.StatusMessageData;

public class StatusMessageRequest extends APIRequest<StatusMessageCallback> {
	
	public StatusMessageRequest(MineBans plugin, int timeout){
		super(plugin, plugin.api.getStatusURL(), timeout);
	}
	
	public StatusMessageRequest(MineBans plugin){
		this(plugin, 12000);
	}
	
	public StatusMessageData process(){
		try{
			return new StatusMessageData(plugin.api.getRequestHandler().processRequest(this));
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