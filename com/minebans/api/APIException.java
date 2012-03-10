package com.minebans.api;

public class APIException extends Exception {
	
	private static final long serialVersionUID = -2326975797674124248L;
	
	private String responce;
	
	public APIException(String responce){
		this.responce = responce;
	}
	
	public String getResponce(){
		return this.responce;
	}
	
}
