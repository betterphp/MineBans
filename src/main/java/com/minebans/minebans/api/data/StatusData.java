package com.minebans.minebans.api.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StatusData {
	
	private Double[] loagAvg;
	private long responceTime;
	
	public StatusData(JSONObject apiResponse){
		JSONObject load = (JSONObject) apiResponse.get("load_avg");
		
		this.loagAvg = new Double[3];
		
		this.loagAvg[0] = Double.parseDouble(load.get("0").toString());
		this.loagAvg[1] = Double.parseDouble(load.get("1").toString());
		this.loagAvg[2] = Double.parseDouble(load.get("2").toString());
		
		this.responceTime = System.currentTimeMillis();
	}
	
	public StatusData(String response) throws ParseException {
		this((JSONObject) (new JSONParser()).parse(response));
	}
	
	public Double[] getLoadAvg(){
		return this.loagAvg;
	}
	
	public long getResponceTime(){
		return this.responceTime;
	}
	
}
