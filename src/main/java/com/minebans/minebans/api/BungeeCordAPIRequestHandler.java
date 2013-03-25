package com.minebans.minebans.api;

import java.util.LinkedList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.APICallback;
import com.minebans.minebans.api.request.APIRequest;

public class BungeeCordAPIRequestHandler implements APIRequestHandler, PluginMessageListener {
	
	private MineBans plugin;
	
	private LinkedList<APIRequest<? extends APICallback>> requestStack;
	private APIRequest<? extends APICallback> currentRequest;
	
	public BungeeCordAPIRequestHandler(MineBans plugin){
		this.plugin = plugin;
		
		this.requestStack = new LinkedList<APIRequest<? extends APICallback>>();
		
		plugin.server.getMessenger().registerOutgoingPluginChannel(plugin, "MineBansBungee");
		plugin.server.getMessenger().registerIncomingPluginChannel(plugin, "MineBansBungee", this);
	}
	
	@Override
	public String processRequest(APIRequest<? extends APICallback> request) throws Exception {
		this.currentRequest = request;
		
		
		
		return "";
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message){
		
	}
	
	@Override
	public void addRequest(APIRequest<? extends APICallback> request){
		this.requestStack.addLast(request);
	}
	
	@Override
	public APIRequest<? extends APICallback> getCurrentRequest(){
		return this.currentRequest;
	}
	
}
