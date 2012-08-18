package com.minebans.pluginapi;

import com.minebans.api.PlayerBanData;

public interface PluginAPIResponseCallback {
	
	/**
	 * Called when the API response completes successfully.
	 * 
	 * @param response	The {@link PlayerBanData} for the player that was queried.
	 */
	public void onSuccess(PlayerBanData response);
	
	/**
	 * Called when the API failed to respond to a query.
	 * 
	 * @param exception		The exception that resulted from the failure.
	 */
	public void onFailure(Exception exception);
	
}
