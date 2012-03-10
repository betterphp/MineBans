package com.minebans.pluginapi;

import com.minebans.api.PlayerBanData;

public interface PluginAPIResponceCallback {
	
	public void onSuccess(PlayerBanData responce);
	
	public void onFailure(Exception e);
	
}
