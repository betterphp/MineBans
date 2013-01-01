package com.minebans.minebans.bans;

public enum BanType {
	
	TEMP(0),
	LOCAL(1),
	GLOBAL(2);
	
	private int id;
	
	private BanType(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
}
