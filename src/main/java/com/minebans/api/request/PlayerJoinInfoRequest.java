package com.minebans.api.request;

import com.minebans.MineBans;
import com.minebans.api.callback.PlayerJoinInfoCallback;
import com.minebans.api.data.PlayerJoinInfoData;

public class PlayerJoinInfoRequest extends APIRequest<PlayerJoinInfoCallback> {
	
	@SuppressWarnings("unchecked")
	public PlayerJoinInfoRequest(MineBans plugin, int timeout, String issuedBy, String playerName){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.json.put("action", "get_player_join_info");
		this.json.put("issued_by", issuedBy);
		this.json.put("player_name", playerName);
	}
	
	public PlayerJoinInfoRequest(MineBans plugin, String issuedBy, String playerName){
		this(plugin, 8000, issuedBy, playerName);
	}
	
	public PlayerJoinInfoData process(){
		try{
			return new PlayerJoinInfoData(plugin.api.getRequestHandler().processRequest(this));
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
