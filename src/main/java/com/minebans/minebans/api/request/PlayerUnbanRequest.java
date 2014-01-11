package com.minebans.minebans.api.request;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.PlayerUnbanCallback;

public class PlayerUnbanRequest extends APIRequest<PlayerUnbanCallback> {
	
	protected String action;
	protected String issued_by;
	protected String issued_by_uuid;
	protected String player_name;
	
	public PlayerUnbanRequest(MineBans plugin, int timeout, String playerName, String issuedBy, String issuedByUUID){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.action = "unban_player";
		this.issued_by = issuedBy;
		this.issued_by_uuid = issuedByUUID;
		this.player_name = playerName;
	}
	
	public PlayerUnbanRequest(MineBans plugin, String playerName, String issuedBy, String issuedByUUID){
		this(plugin, 8000, playerName, issuedBy, issuedByUUID);
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
