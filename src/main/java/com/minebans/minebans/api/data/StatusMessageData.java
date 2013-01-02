package com.minebans.minebans.api.data;

public class StatusMessageData extends APIData {
	
	private String message;
	
	private StatusMessageData(){
		
	}
	
	public static StatusMessageData fromString(String message){
		StatusMessageData data = new StatusMessageData();
		data.message = message;
		return data;
	}
	
	public String getMessage(){
		return this.message;
	}
	
}
