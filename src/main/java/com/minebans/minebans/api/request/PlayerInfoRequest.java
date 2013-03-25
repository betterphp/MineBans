package com.minebans.minebans.api.request;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.PlayerInfoCallback;

public class PlayerInfoRequest extends APIRequest<PlayerInfoCallback> {
	
	@SuppressWarnings("unchecked")
	public PlayerInfoRequest(MineBans plugin, int timeout, String issuedBy, String playerName){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.json.put("action", "get_player_info");
		this.json.put("issued_by", issuedBy);
		this.json.put("player_name", playerName);
	}
	
	public PlayerInfoRequest(MineBans plugin, String issuedBy, String playerName){
		this(plugin, 8000, issuedBy, playerName);
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
