package com.minebans.minebans.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.APICallback;
import com.minebans.minebans.api.request.APIRequest;

public class BukkitAPIRequestHandler extends APIRequestHandler {
	
	public BukkitAPIRequestHandler(MineBans plugin){
		super(plugin, "MineBans Bukkit API Thread");
	}
	
	@Override
	public synchronized String processRequest(APIRequest<? extends APICallback> request) throws Exception {
		this.currentRequest = request;
		
		String response;
		
		try{
			URLConnection conn = request.getURL().openConnection();
			
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setConnectTimeout(request.getTimeout());
			conn.setReadTimeout(request.getTimeout());
			
			String json = this.gson.toJson(request);
			
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			
			out.write("request_data=" + URLEncoder.encode(json, "UTF-8"));
			
			out.close();
			
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
				plugin.log.info(" Method: Bukkit Direct Connection");
				plugin.log.info(" URL: " + request.getURL().toString());
				plugin.log.info(" Request: " + json);
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
	
}
