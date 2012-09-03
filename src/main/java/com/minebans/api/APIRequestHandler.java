package com.minebans.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ArrayBlockingQueue;

import com.minebans.MineBans;

public class APIRequestHandler extends Thread implements Runnable {
	
	private MineBans plugin;
	
	private ArrayBlockingQueue<APIRequest> requestStack;
	private APIRequest currentRrequest;
	
	public APIRequestHandler(MineBans plugin){
		this.plugin = plugin;
		
		this.requestStack = new ArrayBlockingQueue<APIRequest>(500);
	}
	
	public String processRequestDirect(APIRequest request) throws UnsupportedEncodingException, SocketTimeoutException, IOException, APIException {
		this.currentRrequest = request;
		
		URLConnection conn = request.url.openConnection();
		
		conn.setUseCaches(false);
		conn.setConnectTimeout(request.timeout);
		conn.setReadTimeout(request.timeout);
		
		if (request.json != null){
			conn.setDoOutput(true);
			
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			
			out.write("request_data=" + URLEncoder.encode(request.json.toJSONString(), "UTF-8"));
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
		
		this.currentRrequest = null;
		
		return response;
	}
	
	public void run(){
		while (true){
			try{
				APIRequest request = this.requestStack.take();
				
				try{
					request.callback.onSuccess(this.processRequestDirect(request));
				}catch (UnsupportedEncodingException e){
					request.callback.onFailure(e);
				}catch (SocketTimeoutException e){
					request.callback.onFailure(e);
				}catch (IOException e){
					request.callback.onFailure(e);
				}catch (APIException e){
					request.callback.onFailure(e);
				}
				
				this.currentRrequest = null;
			}catch (InterruptedException e1){
				return;
			}
		}
	}
	
	public void addRequest(APIRequest request){
		// This is only to prevent accidental exponential queue growth DOSing the API.
		if (this.requestStack.remainingCapacity() == 0){
			plugin.log.warn("API request queue overloaded, waiting for some to complete.");
		}
		
		try{
			this.requestStack.put(request);
		}catch (InterruptedException e){  }
	}
	
	public APIRequest getCurrentRequest(){
		return this.currentRrequest;
	}
	
}
