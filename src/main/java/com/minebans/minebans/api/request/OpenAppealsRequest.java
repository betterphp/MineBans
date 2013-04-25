package com.minebans.minebans.api.request;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.OpenAppealsCallback;

public class OpenAppealsRequest extends APIRequest<OpenAppealsCallback> {
	
	protected String action;
	protected String issued_by;
	
	public OpenAppealsRequest(MineBans plugin, int timeout, String issuedBy){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.action = "get_open_appeals";
		this.issued_by = issuedBy;
	}
	
	public OpenAppealsRequest(MineBans plugin, String issuedBy){
		this(plugin, 8000, issuedBy);
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
