package com.minebans.minebans.api.request;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.PlayerJoinInfoCallback;
import com.minebans.minebans.api.data.PlayerJoinInfoData;

public class PlayerJoinInfoRequest extends APIRequest<PlayerJoinInfoCallback> {
	
	protected String action;
	protected String issued_by;
	protected String player_name;
	
	public PlayerJoinInfoRequest(MineBans plugin, int timeout, String issuedBy, String playerName){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.action = "get_player_join_info";
		this.issued_by = issuedBy;
		this.player_name = playerName;
	}
	
	public PlayerJoinInfoRequest(MineBans plugin, String issuedBy, String playerName){
		this(plugin, 8000, issuedBy, playerName);
	}
	
	public PlayerJoinInfoData process(){
		try{
			return PlayerJoinInfoData.fromString(plugin.api.getRequestHandler().processRequest(this));
		}catch (Exception e){
			plugin.api.handleException(e);
			
			return null;
		}
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
