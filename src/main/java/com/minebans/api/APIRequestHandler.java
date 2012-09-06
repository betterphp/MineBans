package com.minebans.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ArrayBlockingQueue;

import org.json.simple.JSONObject;

import com.minebans.MineBans;
import com.minebans.api.callback.APICallback;
import com.minebans.api.request.APIRequest;

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
		URLConnection conn = request.getURL().openConnection();
		
		conn.setUseCaches(false);
		conn.setConnectTimeout(request.getTimeout());
		conn.setReadTimeout(request.getTimeout());
		
		JSONObject requestData = request.getJSON();
		
		if (requestData != null){
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
		
		String response = buffer.toString();
		
		in.close();
		
		if (response == null || response.startsWith("E")){
			throw new APIException(response);
		}
		
		return response;
	}
	
	public void run(){
		while (true){
			try{
				APIRequest<? extends APICallback> request = this.requestStack.take();
				
				try{
					request.onSuccess(this.processRequest(request));
				}catch (Exception e){
					request.onFailure(e);
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
