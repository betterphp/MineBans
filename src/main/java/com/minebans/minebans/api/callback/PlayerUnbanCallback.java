package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;

public abstract class PlayerUnbanCallback extends APICallback {
	
	private String playerName;
	private String issuedBy;
	
	public PlayerUnbanCallback(MineBans plugin, String playerName, String issuedBy){
		super(plugin);
		
		this.playerName = playerName;
		this.issuedBy = issuedBy;
	}
	
	@Override
	public void onSuccess(String response){
		plugin.banManager.unbanPlayerAPICallback(this.playerName, this.issuedBy);
	}
	
	public abstract void onFailure(Exception exception);
	
}
