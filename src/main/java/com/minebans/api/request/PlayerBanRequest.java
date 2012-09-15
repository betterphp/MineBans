package com.minebans.api.request;

import com.minebans.MineBans;
import com.minebans.api.callback.PlayerBanCallback;
import com.minebans.bans.BanReason;

public class PlayerBanRequest extends APIRequest<PlayerBanCallback> {
	
	@SuppressWarnings("unchecked")
	public PlayerBanRequest(MineBans plugin, int timeout, String playerName, String issuedBy, BanReason reason, Object evidence){
		super(plugin, plugin.api.getAPIURL(), timeout);
		
		this.json.put("action", "ban_player");
		this.json.put("issued_by", issuedBy);
		this.json.put("player_name", playerName);
		this.json.put("reason", reason.getID());
		this.json.put("evidence", evidence);
	}
	
	public PlayerBanRequest(MineBans plugin, String playerName, String issuedBy, BanReason reason, Object evidence){
		this(plugin, 8000, playerName, issuedBy, reason, evidence);
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