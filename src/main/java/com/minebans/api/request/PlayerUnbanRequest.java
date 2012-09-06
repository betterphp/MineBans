package com.minebans.api.request;

import org.json.simple.JSONObject;

import com.minebans.MineBans;
import com.minebans.api.callback.PlayerUnbanCallback;

public class PlayerUnbanRequest extends APIRequest<PlayerUnbanCallback> {
	
	@SuppressWarnings("unchecked")
	public PlayerUnbanRequest(MineBans plugin, int timeout, String playerName, String issuedBy){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.json = new JSONObject();
		
		this.json.put("action", "unban_player");
		this.json.put("issued_by", issuedBy);
		this.json.put("player_name", playerName);
	}
	
	public PlayerUnbanRequest(MineBans plugin, String playerName, String issuedBy){
		this(plugin, 8000, playerName, issuedBy);
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
