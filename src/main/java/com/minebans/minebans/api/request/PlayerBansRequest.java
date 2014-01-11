package com.minebans.minebans.api.request;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.PlayerBansCallback;

public class PlayerBansRequest extends APIRequest<PlayerBansCallback> {
	
	protected String action;
	protected String issued_by;
	protected String issued_by_uuid;
	protected String player_name;
	
	public PlayerBansRequest(MineBans plugin, int timeout, String issuedBy, String issuedByUUID, String playerName){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.action = "get_player_bans";
		this.issued_by = issuedBy;
		this.issued_by_uuid = issuedByUUID;
		this.player_name = playerName;
	}
	
	public PlayerBansRequest(MineBans plugin, String issuedBy, String issuedByUUID, String playerName){
		this(plugin, 8000, issuedBy, issuedByUUID, playerName);
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
