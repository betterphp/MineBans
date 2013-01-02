package com.minebans.minebans.api.data;

import java.util.LinkedHashMap;

public class StatusData extends APIData {
	
	private LinkedHashMap<String, Double> load_avg;
	
	private transient long responceTime;
	
	private StatusData(){
		this.responceTime = System.currentTimeMillis();
	}
	
	public static StatusData fromString(String response){
		return gson.fromJson(response, StatusData.class);
	}
	
	public Double[] getLoadAverage(){
		return this.load_avg.values().toArray(new Double[0]);
	}
	
	public long getResponceTime(){
		return this.responceTime;
	}
	
}
