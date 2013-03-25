package com.minebans.minebans.api;

import com.minebans.minebans.api.callback.APICallback;
import com.minebans.minebans.api.request.APIRequest;

public interface APIRequestHandler {
	
	public String processRequest(APIRequest<? extends APICallback> request) throws Exception;
	
	public void addRequest(APIRequest<? extends APICallback> request);
	
	public APIRequest<? extends APICallback> getCurrentRequest();
	
}
