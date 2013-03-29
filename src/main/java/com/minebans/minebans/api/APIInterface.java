package com.minebans.minebans.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.simple.parser.ParseException;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.api.callback.APICallback;
import com.minebans.minebans.api.request.APIRequest;

public class APIInterface {
	
	private MineBans plugin;
	
	private URL apiURL;
	private URL statusURL;
	
	private APIRequestHandler requestHandler;
	
	public APIInterface(MineBans plugin){
		try{
			String apiKey = plugin.config.getString(Config.API_KEY);
			String version = plugin.getVersion();
			
			if (MineBans.DEBUG_MODE){
				this.apiURL = new URL("http://192.168.1.10/minebans/api.php?api_key=" + URLEncoder.encode(apiKey, "UTF-8") + "&version=" + URLEncoder.encode(version, "UTF-8"));
			}else{
				this.apiURL = new URL("http://minebans.com/api.php?api_key=" + URLEncoder.encode(apiKey, "UTF-8") + "&version=" + URLEncoder.encode(version, "UTF-8"));
			}
			
			this.statusURL = new URL("http://dl.dropbox.com/s/vjngx1qzvhvtcqz/minebans_status_message.txt");
		}catch (Exception e){
			e.printStackTrace();
			plugin.pluginManager.disablePlugin(plugin);
			return;
		}
		
		this.plugin = plugin;
		this.startThread();
	}
	
	public URL getAPIURL(){
		return this.apiURL;
	}
	
	public URL getStatusURL(){
		return this.statusURL;
	}
	
	public void startThread(){
		if (plugin.config.getBoolean(Config.BUNGEE_CORD_MODE_ENABLED)){
			this.requestHandler = new BungeeCordAPIRequestHandler(plugin);
		}else{
			this.requestHandler = new BukkitAPIRequestHandler(plugin);
		}
		
		this.requestHandler.start();
	}
	
	public void stopThread(){
		this.requestHandler.interrupt();
	}
	
	public String getCurrentRequestKey(){
		APIRequest<? extends APICallback> request = this.requestHandler.getCurrentRequest();
		
		if (request == null){
			return "NONE";
		}
		
		return request.getRequestKey();
	}
	
	public APIRequestHandler getRequestHandler(){
		return this.requestHandler;
	}
	
	public void handleException(Exception exception){
		if (exception instanceof SocketTimeoutException){
			plugin.log.fatal("The API failed to respond in time.");
		}else if (exception instanceof UnsupportedEncodingException || exception instanceof IOException){
			plugin.log.fatal("Failed to contact the API (you should report this).");
			exception.printStackTrace();
		}else if (exception instanceof ParseException){
			plugin.log.fatal("Failed to parse API response (you should report this).");
			exception.printStackTrace();
		}else if (exception instanceof APIException){
			plugin.log.fatal("API Request Failed: " + ((APIException) exception).getResponse());
		}else{
			exception.printStackTrace();
		}
	}
	
	public void handleException(Exception exception, CommandSender sender){
		this.handleException(exception);
		
		if (sender != null){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "API request failed :("));
			
			if (exception instanceof APIException){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Server Response: " + ((APIException) exception).getResponse()));
			}
		}
	}
	
}
