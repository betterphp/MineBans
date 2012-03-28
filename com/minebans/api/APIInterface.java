package com.minebans.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.minebans.MineBans;
import com.minebans.bans.BanReason;

public class APIInterface {
	
	private MineBans plugin;
	
	private APIRequestHandler requestHandler;
	
	public APIInterface(MineBans plugin){
		this.plugin = plugin;
		this.startThread();
	}
	
	public void startThread(){
		this.requestHandler = new APIRequestHandler(plugin);
		this.requestHandler.start();
	}
	
	public void stopThread(){
		this.requestHandler.interrupt();
	}
	
	@SuppressWarnings("unchecked")
	public void lookupPlayerBans(final String playerName, String issuedBy, APIResponseCallback callback){
		JSONObject json = new JSONObject();
		
		json.put("action", "lookup_player");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		this.requestHandler.addRequest(new APIRequest(json, callback, 10000));
	}
	
	@SuppressWarnings("unchecked")
	public void lookupPlayerInfo(final String playerName, String issuedBy, APIResponseCallback callback){
		JSONObject json = new JSONObject();
		
		json.put("action", "get_player_info");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		this.requestHandler.addRequest(new APIRequest(json, callback, 10000));
	}
	
	@SuppressWarnings("unchecked")
	public void banPlayer(final String playerName, final String issuedBy, final BanReason reason, final Object evidence){
		JSONObject json = new JSONObject();
		
		json.put("action", "ban_player");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		json.put("reason", reason.getID());
		json.put("evidence", evidence);
		
		this.requestHandler.addRequest(new APIRequest(json, new APIResponseCallback(){
			
			CommandSender sender = (issuedBy.equalsIgnoreCase("console")) ? Bukkit.getConsoleSender() : Bukkit.getServer().getPlayer(issuedBy);
			
			public void onSuccess(String response){  }
			
			public void onFailure(Exception e){
				if (e instanceof SocketTimeoutException){
					plugin.log.fatal("The API failed to response in time.");
				}else if (e instanceof UnsupportedEncodingException || e instanceof IOException){
					plugin.log.fatal("Failed to contact the API (you should report this).");
					e.printStackTrace();
				}else if (e instanceof ParseException){
					plugin.log.fatal("Failed to parse API response (you should report this).");
					e.printStackTrace();
				}else if (e instanceof APIException){
					plugin.log.fatal("API Request Failed: " + ((APIException) e).getResponse());
				}
				
				if (sender != null){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Failed to upload ban for '" + playerName + "'."));
					
					if (e instanceof APIException){
						sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Server Response: " + ((APIException) e).getResponse()));
					}
				}
			}
			
		}, 10000));
	}
	
	@SuppressWarnings("unchecked")
	public void unbanPlayer(final String playerName, final String issuedBy){
		JSONObject json = new JSONObject();
		
		json.put("action", "unban_player");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		this.requestHandler.addRequest(new APIRequest(json, new APIResponseCallback(){
			
			CommandSender sender = (issuedBy.equalsIgnoreCase("console")) ? Bukkit.getConsoleSender() : Bukkit.getServer().getPlayer(issuedBy);
			
			public void onSuccess(String response){
				plugin.banManager.unbanPlayerAPICallback(playerName);
			}
			
			public void onFailure(Exception e){
				if (e instanceof SocketTimeoutException){
					plugin.log.fatal("The API failed to response in time.");
				}else if (e instanceof UnsupportedEncodingException || e instanceof IOException){
					plugin.log.fatal("Failed to contact the API (you should report this).");
					e.printStackTrace();
				}else if (e instanceof ParseException){
					plugin.log.fatal("Failed to parse API response (you should report this).");
					e.printStackTrace();
				}else if (e instanceof APIException){
					plugin.log.fatal("API Request Failed: " + ((APIException) e).getResponse());
				}
				
				if (sender != null){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Failed to remove global ban for '" + playerName + "'."));
					
					if (e instanceof APIException){
						sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Server Response: " + ((APIException) e).getResponse()));
					}
				}
			}
			
		}, 10000));
	}
	
	@SuppressWarnings("unchecked")
	public PlayerBanData getPlayerBans(final String playerName, final String issuedBy) throws SocketTimeoutException {
		JSONObject json = new JSONObject();
		
		json.put("action", "lookup_player");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		try{
			return new PlayerBanData(this.requestHandler.processRequestDirect(new APIRequest(json, 250)));
		}catch (Exception e){
			CommandSender sender = (issuedBy.equalsIgnoreCase("console")) ? Bukkit.getConsoleSender() : Bukkit.getServer().getPlayer(issuedBy);
			
			if (e instanceof SocketTimeoutException){
				throw (SocketTimeoutException) e;
			}else if (e instanceof APIException){
				plugin.log.fatal("Unable to contact the API. Response: " + ((APIException) e).getResponse());
			}else if (e instanceof UnsupportedEncodingException || e instanceof IOException){
				plugin.log.fatal("Failed to contact the API (you should report this).");
				e.printStackTrace();
			}else if (e instanceof ParseException){
				plugin.log.fatal("Failed to parse API response (you should report this).");
				e.printStackTrace();
			}
			
			if (sender != null){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Failed to fetch bans for '" + playerName + "'."));
				
				if (e instanceof APIException){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Server Response: " + ((APIException) e).getResponse()));
				}
			}
			
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public PlayerInfoData getPlayerInfo(final String playerName, final String issuedBy) throws SocketTimeoutException {
		JSONObject json = new JSONObject();
		
		json.put("action", "get_player_info");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		try{
			return new PlayerInfoData(this.requestHandler.processRequestDirect(new APIRequest(json, 250)));
		}catch (Exception e){
			CommandSender sender = (issuedBy.equalsIgnoreCase("CONSOLE")) ? Bukkit.getConsoleSender() : Bukkit.getServer().getPlayer(issuedBy);
			
			if (e instanceof SocketTimeoutException){
				throw (SocketTimeoutException) e;
			}else if (e instanceof APIException){
				plugin.log.fatal("Unable to contact the API. Response: " + ((APIException) e).getResponse());
			}else if (e instanceof UnsupportedEncodingException || e instanceof IOException){
				plugin.log.fatal("Failed to contact the API (you should report this).");
				e.printStackTrace();
			}else if (e instanceof ParseException){
				plugin.log.fatal("Failed to parse API response (you should report this).");
				e.printStackTrace();
			}
			
			if (sender != null){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Failed to fetch info for '" + playerName + "'."));
				
				if (e instanceof APIException){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Server Response: " + ((APIException) e).getResponse()));
				}
			}
			
			return null;
		}
	}
	
}
