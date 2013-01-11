package com.minebans.minebans;

import java.net.InetAddress;
import java.security.MessageDigest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;

import uk.co.jacekk.bukkit.baseplugin.v8.event.BaseListener;

public class RequestVerificationListener extends BaseListener<MineBans> {
	
	public RequestVerificationListener(MineBans plugin){
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onServerListPing(ServerListPingEvent event){
		try{
			InetAddress address = event.getAddress();
			
			if ((!address.isAnyLocalAddress() && address.getHostAddress().equals(InetAddress.getByName("minebans.com").getHostAddress())) || MineBans.DEBUG_MODE){
				StringBuilder message = new StringBuilder(32);
				
				for (byte b : MessageDigest.getInstance("MD5").digest((plugin.api.getCurrentRequestKey() + plugin.config.getString(Config.API_KEY)).getBytes("UTF-8"))){
					String hex = Integer.toHexString(0x000000FF & b);
					
					if (hex.length() % 2 != 0){
						message.append("0");
					}
					
					message.append(hex);
				}
				
				event.setMotd(message.toString());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
