package com.minebans.minebans.api.callback;

import com.minebans.minebans.MineBans;

public abstract class PlayerUnbanCallback extends APICallback {
	
	private String playerName;
	
	public PlayerUnbanCallback(MineBans plugin, String playerName){
		super(plugin);
		
		this.playerName = playerName;
	}
	
	public void onSuccess(String response){
		plugin.banManager.unbanPlayerAPICallback(this.playerName);
	}
	
	public abstract void onFailure(Exception exception);
	
}
