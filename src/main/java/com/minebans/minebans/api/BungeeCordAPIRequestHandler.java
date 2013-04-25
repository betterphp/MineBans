package com.minebans.minebans.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.APICallback;
import com.minebans.minebans.api.request.APIRequest;

public class BungeeCordAPIRequestHandler extends APIRequestHandler {
	
	private String authStr;
	
	public BungeeCordAPIRequestHandler(MineBans plugin){
		super(plugin, "MineBans BungeeCord API Thread");
		
		this.authStr = plugin.config.getString(Config.BUNGEE_CORD_MODE_AUTH_STR);
	}
	
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
			
			String json = this.gson.toJson(request);
			
			Socket proxy = new Socket(plugin.config.getString(Config.BUNGEE_CORD_MODE_ADDRESS), plugin.config.getInt(Config.BUNGEE_CORD_MODE_PORT));
			
			PrintWriter output = new PrintWriter(proxy.getOutputStream(), true);
			
			output.println(this.authStr);
			output.println(request.getURL().toString());
			output.println(json);
			output.println(motd.toString());
			
			BufferedReader input = new BufferedReader(new InputStreamReader(proxy.getInputStream()));
			
			String line;
			StringBuilder buffer = new StringBuilder();
			
			while ((line = input.readLine()) != null){
				buffer.append(line);
			}
			
			response = buffer.toString();
			
			input.close();
			output.close();
			
			proxy.close();
			
			if (MineBans.DEBUG_MODE){
				plugin.log.info("======================== REQUEST DUMP =========================");
				plugin.log.info(" Method: BungeeCord Proxy");
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
