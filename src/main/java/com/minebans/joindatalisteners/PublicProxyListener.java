package com.minebans.joindatalisteners;

import javax.naming.NamingException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import uk.co.jacekk.bukkit.baseplugin.v2.event.BaseListener;

import com.minebans.MineBans;
import com.minebans.api.ConnectionDeniedReason;
import com.minebans.events.PlayerLoginDataEvent;
import com.minebans.util.DNSBLChecker;

public class PublicProxyListener extends BaseListener<MineBans> {
	
	private DNSBLChecker dnsblChecker;
	
	public PublicProxyListener(MineBans plugin){
		super(plugin);
		
		try{
			this.dnsblChecker = new DNSBLChecker();
			
			this.dnsblChecker.addDNSBL("dnsbl.proxybl.org");
			this.dnsblChecker.addDNSBL("http.dnsbl.sorbs.net");
			this.dnsblChecker.addDNSBL("socks.dnsbl.sorbs.net");
			this.dnsblChecker.addDNSBL("misc.dnsbl.sorbs.net");
			this.dnsblChecker.addDNSBL("tor.dnsbl.sectoor.de");
		}catch (NamingException e){
			plugin.log.fatal("Something odd happened (you should report this)");
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLoginData(PlayerLoginDataEvent event){
		if (this.dnsblChecker.ipFound(event.getPlayerAddress())){
			event.setPreventConnection(true);
			event.setReason(ConnectionDeniedReason.PUBLIC_PROXY);
			event.setKickMessage(ConnectionDeniedReason.PUBLIC_PROXY.getKickMessage());
			event.setLogMessage(ConnectionDeniedReason.PUBLIC_PROXY.getLogMessage());
		}
	}
	
}
