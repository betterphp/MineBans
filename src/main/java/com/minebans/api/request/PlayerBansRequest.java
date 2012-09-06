package com.minebans.api.request;

import org.json.simple.JSONObject;

import com.minebans.MineBans;
import com.minebans.api.callback.PlayerBansCallback;
import com.minebans.api.data.PlayerBansData;

public class PlayerBansRequest extends APIRequest<PlayerBansCallback> {
	
	@SuppressWarnings("unchecked")
	public PlayerBansRequest(MineBans plugin, int timeout, String issuedBy, String playerName){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.json = new JSONObject();
		
		this.json.put("action", "get_player_bans");
		this.json.put("issued_by", issuedBy);
		this.json.put("player_name", playerName);
	}
	
	public PlayerBansRequest(MineBans plugin, String issuedBy, String playerName){
		this(plugin, 8000, issuedBy, playerName);
	}
	
	public PlayerBansData process(){
		try{
			return new PlayerBansData(plugin.api.getRequestHandler().processRequest(this));
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
