package com.minebans.minebans.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import org.json.simple.JSONObject;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.APICallback;
import com.minebans.minebans.api.request.APIRequest;

public class APIRequestHandler extends Thread implements Runnable {
	
	private MineBans plugin;
	
	private ArrayBlockingQueue<APIRequest<? extends APICallback>> requestStack;
	private volatile APIRequest<? extends APICallback> currentRequest;
	
	public APIRequestHandler(MineBans plugin){
		super("MineBans API Thread");
		
		this.plugin = plugin;
		
		this.requestStack = new ArrayBlockingQueue<APIRequest<? extends APICallback>>(256);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized String processRequest(APIRequest<? extends APICallback> request) throws Exception {
		this.currentRequest = request;
		
		String response;
		
		try{
			URLConnection conn = request.getURL().openConnection();
			
			conn.setUseCaches(false);
			conn.setConnectTimeout(request.getTimeout());
			conn.setReadTimeout(request.getTimeout());
			
			JSONObject requestData = request.getJSON();
			
			if (!requestData.isEmpty()){
				conn.setDoOutput(true);
				
				OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
				
				requestData.put("request_key", request.getRequestKey());
				
				out.write("request_data=" + URLEncoder.encode(requestData.toJSONString(), "UTF-8"));
				
				out.flush();
				out.close();
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String line;
			StringBuilder buffer = new StringBuilder();
			
			while ((line = in.readLine()) != null){
				buffer.append(line);
			}
			
			response = buffer.toString();
			
			in.close();
			
			if (MineBans.DEBUG_MODE){
				plugin.log.info("======================== REQUEST DUMP =========================");
				plugin.log.info(" URL: " + request.getURL().toString());
				plugin.log.info(" Request: " + requestData.toJSONString());
				plugin.log.info(" Response: " + response);
				plugin.log.info("===============================================================");
			}
			
			if (response == null || response.startsWith("E")){
				throw new APIException(response);
			}
		}catch (Exception exception){
			throw exception;
		}finally{
			this.currentRequest = null;
		}
		
		return response;
	}
	
	public void run(){
		while (true){
			try{
				final APIRequest<? extends APICallback> request = this.requestStack.take();
				
				try{
					final String response = this.processRequest(request);
					
					plugin.scheduler.callSyncMethod(plugin, new Callable<Boolean>(){
						
						public Boolean call() throws Exception{
							request.onSuccess(response);
							return true;
						}
						
					});
				}catch (final Exception e){
					plugin.scheduler.callSyncMethod(plugin, new Callable<Boolean>(){
						
						public Boolean call() throws Exception{
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
	
	public void addRequest(APIRequest<? extends APICallback> request){
		// This is only to prevent accidental exponential queue growth DOSing the API.
		if (this.requestStack.remainingCapacity() == 0){
			plugin.log.warn("API request queue overloaded, waiting for some to complete.");
		}
		
		try{
			this.requestStack.put(request);
		}catch (InterruptedException e){  }
	}
	
	public APIRequest<? extends APICallback> getCurrentRequest(){
		return this.currentRequest;
	}
	
}