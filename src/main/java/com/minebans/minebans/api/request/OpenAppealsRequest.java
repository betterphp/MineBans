package com.minebans.minebans.api.request;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.OpenAppealsCallback;
import com.minebans.minebans.api.data.OpenAppealsData;

public class OpenAppealsRequest extends APIRequest<OpenAppealsCallback> {
	
	@SuppressWarnings("unchecked")
	public OpenAppealsRequest(MineBans plugin, int timeout, String issuedBy){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.json.put("action", "get_open_appeals");
		this.json.put("issued_by", issuedBy);
	}
	
	public OpenAppealsRequest(MineBans plugin, String issuedBy){
		this(plugin, 8000, issuedBy);
	}
	
	public OpenAppealsData process(){
		try{
			return OpenAppealsData.fromString(plugin.api.getRequestHandler().processRequest(this));
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
