package com.minebans.minebans.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;

import org.json.simple.JSONObject;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.APICallback;
import com.minebans.minebans.api.request.APIRequest;

public class BungeeCordAPIRequestHandler extends APIRequestHandler {
	
	private Socket proxy;
	
	public BungeeCordAPIRequestHandler(MineBans plugin){
		super(plugin, "MineBans BungeeCord API Thread");
		
		try{
			this.proxy = new Socket(plugin.config.getString(Config.BUNGEE_CORD_MODE_ADDRESS), plugin.config.getInt(Config.BUNGEE_CORD_MODE_PORT));
		}catch (Exception e){
			plugin.log.fatal("Failed to communicate with proxy");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String processRequest(APIRequest<? extends APICallback> request) throws Exception {
		this.currentRequest = request;
		
		String response;
		
		try{
			StringBuilder motd = new StringBuilder(32);
			
			for (byte b : MessageDigest.getInstance("MD5").digest((plugin.api.getCurrentRequestKey() + plugin.config.getString(Config.API_KEY)).getBytes("UTF-8"))){
				String hex = Integer.toHexString(0x000000FF & b);
				
				if (hex.length() % 2 != 0){
					motd.append("0");
				}
				
				motd.append(hex);
			}
			
			String data = "";
			
			JSONObject requestData = request.getJSON();
			
			if (!requestData.isEmpty()){
				requestData.put("request_key", request.getRequestKey());
				data = requestData.toJSONString();
			}
			
			BufferedReader input = new BufferedReader(new InputStreamReader(this.proxy.getInputStream()));
			PrintWriter output = new PrintWriter(this.proxy.getOutputStream(), true);
			
			output.println(request.getURL().toString());
			output.println(data);
			output.println(motd.toString());
			
			String line;
			StringBuilder buffer = new StringBuilder();
			
			while ((line = input.readLine()) != null){
				buffer.append(line);
			}
			
			response = buffer.toString();
			
			input.close();
			output.close();
			
			if (MineBans.DEBUG_MODE){
				plugin.log.info("======================== REQUEST DUMP =========================");
				plugin.log.info(" Method: BungeeCord Proxy");
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
