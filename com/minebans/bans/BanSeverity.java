package com.minebans.bans;

import java.util.Arrays;
import java.util.List;

public enum BanSeverity {
	
	TOTAL(),
	CONFIRMED(),
	UNCONFIRMED(0),
	LOW(1),
	MEDIUM(2),
	HIGH(3);
	
	private Integer id;
	
	BanSeverity(){ }
	
	BanSeverity(Integer id){
		this.id = id;
	}
	
	public static List<BanSeverity> getAll(){
		return Arrays.asList(BanSeverity.values());
	}
	
	public Integer getID(){
		return this.id;
	}
	
}
