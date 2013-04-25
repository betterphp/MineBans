package com.minebans.minebans.api.request;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.PlayerBansCallback;

public class PlayerBansRequest extends APIRequest<PlayerBansCallback> {
	
	protected String action;
	protected String issued_by;
	protected String player_name;
	
	public PlayerBansRequest(MineBans plugin, int timeout, String issuedBy, String playerName){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.action = "get_player_bans";
		this.issued_by = issuedBy;
		this.player_name = playerName;
	}
	
	public PlayerBansRequest(MineBans plugin, String issuedBy, String playerName){
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
