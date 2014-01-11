package com.minebans.minebans.api.request;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.PlayerBanCallback;
import com.minebans.minebans.bans.BanReason;

public class PlayerBanRequest extends APIRequest<PlayerBanCallback> {
	
	protected String action;
	protected String issued_by;
	protected String issued_by_uuid;
	protected String player_name;
	protected int reason;
	protected Object evidence;
	
	public PlayerBanRequest(MineBans plugin, int timeout, String playerName, String issuedBy, String issuedByUUID, BanReason reason, Object evidence){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.action = "ban_player";
		this.issued_by = issuedBy;
		this.issued_by_uuid = issuedByUUID;
		this.player_name = playerName;
		this.reason = reason.getID();
		this.evidence = evidence;
	}
	
	public PlayerBanRequest(MineBans plugin, String playerName, String issuedBy, String issuedByUUID, BanReason reason, Object evidence){
		this(plugin, 8000, playerName, issuedBy, issuedByUUID, reason, evidence);
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
