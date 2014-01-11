package com.minebans.minebans.api.request;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.OpenAppealsCallback;

public class OpenAppealsRequest extends APIRequest<OpenAppealsCallback> {
	
	protected String action;
	protected String issued_by;
	protected String issued_by_uuid;
	
	public OpenAppealsRequest(MineBans plugin, int timeout, String issuedBy, String issuedByUUID){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.action = "get_open_appeals";
		this.issued_by = issuedBy;
		this.issued_by_uuid = issuedByUUID;
	}
	
	public OpenAppealsRequest(MineBans plugin, String issuedBy, String issuedByUUID){
		this(plugin, 8000, issuedBy, issuedByUUID);
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
