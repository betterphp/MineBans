package com.minebans.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ArrayBlockingQueue;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;

public class APIRequestHandler extends Thread implements Runnable {
	
	private URL apiURL;
	
	private MineBans plugin;
	
	private ArrayBlockingQueue<APIRequest> requestStack;
	
	public APIRequestHandler(MineBans plugin){
		try{
			this.apiURL = new URL("http://minebans.com/api.php?api_key=" + URLEncoder.encode(plugin.config.getString(MineBansConfig.API_KEY), "UTF-8") + "&version = " + URLEncoder.encode(plugin.getVersion(), "UTF-8"));
		//	this.apiURL = new URL("http://192.168.1.10/minebans/api.php?api_key=" + URLEncoder.encode(plugin.config.getString(MineBansConfig.API_KEY), "UTF-8") + "&version = " + URLEncoder.encode(plugin.getVersion(), "UTF-8"));
		}catch (Exception e){
			e.printStackTrace();
		}
		
		this.plugin = plugin;
		
		this.requestStack = new ArrayBlockingQueue<APIRequest>(500);
	}
	
	public String processRequestDirect(APIRequest request) throws UnsupportedEncodingException, SocketTimeoutException, IOException, APIException {
		String data = "request_data=" + URLEncoder.encode(request.json.toJSONString(), "UTF-8");
		
		URLConnection conn = this.apiURL.openConnection();
		
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		conn.setConnectTimeout(request.timeout);
		conn.setReadTimeout(request.timeout);
		
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		
		out.write(data);
		out.flush();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		String line;
		StringBuilder buffer = new StringBuilder();
		
		while ((line = in.readLine()) != null){
			buffer.append(line);
		}
		
		String response = buffer.toString();
		
		out.close();
		in.close();
		
		if (response == null || response.startsWith("E")){
			throw new APIException(response);
		}
		
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
	
}
