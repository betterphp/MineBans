package com.minebans.joinchecks;

import javax.naming.NamingException;

import com.minebans.MineBans;
import com.minebans.util.DNSBLChecker;

public class PublicProxyCheck extends LocalJoinCheck {
	
	private DNSBLChecker dnsblChecker;
	
	public PublicProxyCheck(MineBans plugin){
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
	
	public boolean shouldPreventConnection(String playerName, String playerAddress){
		return this.dnsblChecker.ipFound(playerAddress);
	}
	
}
