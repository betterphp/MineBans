package com.minebans.pluginapi;

import com.minebans.api.PlayerBanData;

public interface PluginAPIResponseCallback {
	
	public void onSuccess(PlayerBanData response);
	
	public void onFailure(Exception e);
	
}
