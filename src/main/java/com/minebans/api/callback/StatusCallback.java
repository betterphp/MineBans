package com.minebans.api.callback;

import org.json.simple.parser.ParseException;

import com.minebans.MineBans;
import com.minebans.api.data.StatusData;

public abstract class StatusCallback extends APICallback {
	
	public StatusCallback(MineBans plugin){
		super(plugin);
	}
	
	public void onSuccess(String response){
		try{
			this.onSuccess(new StatusData(response));
		}catch (ParseException e){
			this.onFailure(e);
		}
	}
	
	public abstract void onSuccess(StatusData data);
	
	public abstract void onFailure(Exception exception);
	
}
