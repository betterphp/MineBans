package com.minebans;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;

import uk.co.jacekk.bukkit.baseplugin.v1.event.BaseListener;

public class ServerVerificationListener extends BaseListener<MineBans> {
	
	public ServerVerificationListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onServerListPing(ServerListPingEvent event){
		try{
			if (event.getAddress().getHostAddress().equals(InetAddress.getByName("minebans.com").getHostAddress())){
				// TODO: Don't just use the API key for this.
				event.setMotd(plugin.config.getString(Config.API_KEY));
			}
		}catch (UnknownHostException e){
			e.printStackTrace();
		}
	}
	
}
