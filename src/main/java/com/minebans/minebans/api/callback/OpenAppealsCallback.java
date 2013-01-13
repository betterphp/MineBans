package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.data.OpenAppealsData;

public abstract class OpenAppealsCallback extends APICallback {
	
	public OpenAppealsCallback(MineBans plugin){
		super(plugin);
	}
	
	@Override
	public void onSuccess(String response){
		this.onSuccess(OpenAppealsData.fromString(response));
	}
	
	public abstract void onSuccess(OpenAppealsData data);
	
	public abstract void onFailure(Exception exception);
	
}
