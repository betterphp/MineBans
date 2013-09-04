package com.minebans.minebans.joindatalisteners;

import javax.naming.NamingException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import uk.co.jacekk.bukkit.baseplugin.event.BaseListener;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.ConnectionDeniedReason;
import com.minebans.minebans.events.PlayerLoginDataEvent;
import com.minebans.minebans.util.DNSBLChecker;

public class PublicProxyListener extends BaseListener<MineBans> {
	
	private DNSBLChecker dnsblChecker;
	
	public PublicProxyListener(MineBans plugin){
		super(plugin);
		
		try{
			this.dnsblChecker = new DNSBLChecker();
			
			for (String dnsbl : plugin.config.getStringList(Config.PROXY_DNSBL_LIST)){
				this.dnsblChecker.addDNSBL(dnsbl);
			}
		}catch (NamingException e){
			plugin.log.fatal("Something odd happened (you should report this)");
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLoginData(PlayerLoginDataEvent event){
		if (this.dnsblChecker.ipFound(event.getPlayerAddress())){
			if (plugin.config.getBoolean(Config.PROXY_BLOCK)){
				event.setPreventConnection(true);
				event.setReason(ConnectionDeniedReason.PUBLIC_PROXY);
				event.setKickMessage(ConnectionDeniedReason.PUBLIC_PROXY.getKickMessage());
				event.setLogMessage(ConnectionDeniedReason.PUBLIC_PROXY.getLogMessage());
			}
			
			event.setProxyDetected(true);
		}
	}
	
}
