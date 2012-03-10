package com.minebans.bans;

import java.util.Arrays;
import java.util.List;

public enum BanSeverity {
	
	TOTAL("total"),
	CONFIRMED("confirmed"),
	UNCONFIRMED("unconfirmed", 	0),
	LOW("low",					1),
	MEDIUM("medium", 			2),
	HIGH("high",				3);
	
	private String configName;
	private Integer id;
	
	BanSeverity(String configName){
		this.configName = configName;
	}
	
	BanSeverity(String configName, Integer id){
		this.configName = configName;
		this.id = id;
	}
	
	public static List<BanSeverity> getAll(){
		return Arrays.asList(BanSeverity.values());
	}
	
	public String getConfigName(){
		return this.configName;
	}
	
	public Integer getID(){
		return this.id;
	}
	
}
