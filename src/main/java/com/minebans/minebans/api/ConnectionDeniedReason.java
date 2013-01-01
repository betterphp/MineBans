package com.minebans.minebans.api;

public enum ConnectionDeniedReason {
	
	PUBLIC_PROXY(		"You cannot connect to this server via a proxy",												"was prevented from connecting for using a public proxy"),
	KNOWN_COMPROMISED(	"You are using an account that is known to be compromised, you should change your password", 	"was prevented from connecting for using an account that is known to be compromised"),
	GROUP_BAN(			"You have been banned from all of this owners's servers (appeal at minebans.com)",				"was prevented from connecting as they have been banned from another server linked to your account"),
	GLOBALLY_BANNED(	"You have been globally banned by this server (appeal at minebans.com)",						"was prevented from connecting as they have been globally banned by this server"),
	LOCALLY_BANNED(		"You have been banned from this server",														"was prevented from connecting as they have been banned"),
	TEMP_BANNED(		"You have been temporarily banned from this server",											"was prevented from connecting as they have been temporarily banned"),
	TOO_MANY_BANS(		"You have too many bans on record to connect to this server",									"was prevented from connecting as they have too many bans on record"),
	
	OTHER(				"You have been banned",																			"was prevented from connecting");
	
	private String kickMessage;
	private String logMessage;
	
	private ConnectionDeniedReason(String kickMessage, String logMessage){
		this.kickMessage = kickMessage;
		this.logMessage = logMessage;
	}
	
	public String getKickMessage(){
		return this.kickMessage;
	}
	
	public String getLogMessage(){
		return this.logMessage;
	}
	
}
