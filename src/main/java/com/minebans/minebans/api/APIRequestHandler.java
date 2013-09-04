package com.minebans.minebans.api;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.APICallback;
import com.minebans.minebans.api.request.APIRequest;

public abstract class APIRequestHandler extends Thread {
	
	protected MineBans plugin;
	protected Gson gson;
	protected ArrayBlockingQueue<APIRequest<? extends APICallback>> requestStack;
	protected volatile APIRequest<? extends APICallback> currentRequest;
	
	public APIRequestHandler(MineBans plugin, String name){
		super(name);
		
		this.plugin = plugin;
		this.gson = new Gson();
		this.requestStack = new ArrayBlockingQueue<APIRequest<? extends APICallback>>(256);
	}
	
	public APIRequest<? extends APICallback> getCurrentRequest(){
		return this.currentRequest;
	}
	
	public void addRequest(APIRequest<? extends APICallback> request){
		// This is only to prevent accidental exponential queue growth DOSing the API.
		if (this.requestStack.remainingCapacity() == 0){
			plugin.log.warn("API request queue overloaded, waiting for some to complete.");
		}
		
		try{
			this.requestStack.put(request);
		}catch (InterruptedException e){  }
	}
	
	public void run(){
		while (true){
			try{
				final APIRequest<? extends APICallback> request = this.requestStack.take();
				
				try{
					final String response = this.processRequest(request);
					
					plugin.getServer().getScheduler().callSyncMethod(plugin, new Callable<Boolean>(){
						
						public Boolean call() throws Exception {
							request.onSuccess(response);
							return true;
						}
						
					});
				}catch (final Exception e){
					plugin.getServer().getScheduler().callSyncMethod(plugin, new Callable<Boolean>(){
						
						public Boolean call() throws Exception {
							request.onFailure(e);
							return true;
						}
						
					});
				}
			}catch (InterruptedException e1){
				return;
			}
		}
	}
	
	public abstract String processRequest(APIRequest<? extends APICallback> request) throws Exception;
	
}
