package com.minebans.minebans.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.simple.JSONObject;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.APICallback;
import com.minebans.minebans.api.request.APIRequest;

public class BukkitAPIRequestHandler extends APIRequestHandler {
	
	public BukkitAPIRequestHandler(MineBans plugin){
		super(plugin, "MineBans Bukkit API Thread");
	}
	
	@SuppressWarnings("unchecked")
	@Override
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
				plugin.log.info(" Method: Bukkit Direct Connection");
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
	
}
