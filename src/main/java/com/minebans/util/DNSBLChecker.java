package com.minebans.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class DNSBLChecker {
	
	private boolean cache;
	private ArrayList<String> cachedIPs;
	
	private DirContext ictx;
	private List<String> dnsbls;
	private String[] types;
	
	public DNSBLChecker() throws NamingException {
		this.cache = true;
		this.cachedIPs = new ArrayList<String>();
		
		this.dnsbls = new ArrayList<String>();
		this.types = new String[]{"A"};
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");	   
		env.put("com.sun.jndi.dns.timeout.initial", "150");
		env.put("com.sun.jndi.dns.timeout.retries", "1");
		env.put(Context.PROVIDER_URL, "dns://8.8.8.8 dns://8.8.4.4");
		
		this.ictx = new InitialDirContext(env);
	}
	
	public void setCacheEnabled(boolean cache){
		this.cache = cache;
	}
	
	public void addDNSBL(String bl){
		this.dnsbls.add(bl);
	}
	
	public boolean ipFound(String ip){
		if (this.cache && this.cachedIPs.contains(ip)){
			return false;
		}
		
		String[] parts = ip.split("\\.");
		String reversedAddress = parts[3] + "." + parts[2] + "." + parts[1] + "." + parts[0];
		
		Attribute attribute;
		Attributes attributes;
		
		for (String service : this.dnsbls){
			try{
				attributes = this.ictx.getAttributes(reversedAddress + "." + service, this.types);
				attribute = attributes.get("A");
				
				if (attribute != null){
					return true;
				}
			}catch (Exception e){  }
		}
		
		if (this.cache && this.cachedIPs.contains(ip) == false){
			this.cachedIPs.add(ip);
		}
		
		return false;
	}
	
}
