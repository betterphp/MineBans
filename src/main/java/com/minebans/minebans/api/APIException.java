package com.minebans.minebans.api;

public class APIException extends Exception {
	
	private static final long serialVersionUID = -2326975797674124248L;
	
	private String response;
	
	public APIException(String response){
		this.response = response;
	}
	
	public String getResponse(){
		return this.response;
	}
	
}
