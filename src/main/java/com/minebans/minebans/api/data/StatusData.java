package com.minebans.minebans.api.data;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class StatusData {
	
	private Double[] loagAvg;
	private long responceTime;
	
	public StatusData(StringMap<?> response){
		StringMap<Double> load = (StringMap<Double>) response.get("load_avg");
		
		this.loagAvg = new Double[3];
		
		this.loagAvg[0] = Double.parseDouble(load.get("0").toString());
		this.loagAvg[1] = Double.parseDouble(load.get("1").toString());
		this.loagAvg[2] = Double.parseDouble(load.get("2").toString());
		
		this.responceTime = System.currentTimeMillis();
	}
	
	public StatusData(String response){
		this((new Gson()).fromJson(response, StringMap.class));
	}
	
	public Double[] getLoadAvg(){
		return this.loagAvg;
	}
	
	public long getResponceTime(){
		return this.responceTime;
	}
	
}
