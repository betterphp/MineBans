package com.minebans;

import java.net.InetAddress;
import java.security.MessageDigest;

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
				StringBuilder message = new StringBuilder(32);
				
				for (byte b : MessageDigest.getInstance("MD5").digest(plugin.config.getString(Config.API_KEY).getBytes("UTF-8"))){
					int val = 0x000000FF & b;
					
					if (val < 10){
						message.append("0");
					}
					
					message.append(Integer.toHexString(val));
				}
				
				event.setMotd(message.toString());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
