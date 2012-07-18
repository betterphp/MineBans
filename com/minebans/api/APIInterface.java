package com.minebans.api;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;
import com.minebans.bans.BanReason;

public class APIInterface {
	
	private MineBans plugin;
	
	private URL apiURL;
	private URL statusURL;
	private URL filesURL;
	
	private APIRequestHandler requestHandler;
	
	public APIInterface(MineBans plugin){
		try{
			String apiKey = plugin.config.getString(MineBansConfig.API_KEY);
			String hwid = null;
			String version = plugin.getVersion();
			
			for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ){
				NetworkInterface nic = interfaces.nextElement();
				byte[] address = nic.getHardwareAddress();
				
				if (address != null){
					hwid = UUID.nameUUIDFromBytes(address).toString();
					break;
				}
			}
			
			if (hwid == null){
				plugin.log.fatal("Failed to create system ID: No NIC found");
				plugin.pluginManager.disablePlugin(plugin);
				return;
			}
			
			this.apiURL = new URL("http://minebans.com/api.php?api_key=" + URLEncoder.encode(apiKey, "UTF-8") + "&hwid=" + URLEncoder.encode(hwid, "UTF-8") + "&version=" + URLEncoder.encode(version, "UTF-8"));
//			this.apiURL = new URL("http://192.168.1.10/minebans/api.php?api_key=" + URLEncoder.encode(apiKey, "UTF-8") + "&hwid=" + URLEncoder.encode(hwid, "UTF-8") + "&version=" + URLEncoder.encode(version, "UTF-8"));
			
			this.statusURL = new URL("http://dl.dropbox.com/s/vjngx1qzvhvtcqz/minebans_status_message.txt");
			this.filesURL = new URL("http://dev.bukkit.org/server-mods/minebans/files.rss");
		}catch (Exception e){
			plugin.log.fatal("Failed to create system ID");
			e.printStackTrace();
			plugin.pluginManager.disablePlugin(plugin);
			return;
		}
		
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
	
	public void lookupAPIStatusMessage(APIResponseCallback callback){
		this.requestHandler.addRequest(new APIRequest(this.statusURL, null, callback, 10000));
	}
	
	@SuppressWarnings("unchecked")
	public void lookupAPIStatus(String issuedBy, APIResponseCallback callback){
		JSONObject json = new JSONObject();
		
		json.put("action", "get_system_status");
		json.put("issued_by", issuedBy);
		
		this.requestHandler.addRequest(new APIRequest(this.apiURL, json, callback, 5000));
	}
	
	public void lookupLatestVersion(final APIResponseCallback callback){
		this.requestHandler.addRequest(new APIRequest(this.filesURL, null, new APIResponseCallback(){
			
			public void onSuccess(String response){
				try{
					Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response)));
					
					Node latestFile = document.getElementsByTagName("item").item(0);
					NodeList children = latestFile.getChildNodes();
					
					String version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]", "");
					
					callback.onSuccess(version);
				}catch (Exception e){
					this.onFailure(e);
				}
			}
			
			public void onFailure(Exception e){
				callback.onFailure(e);
			}
			
		}, 10000));
	}
	
	@SuppressWarnings("unchecked")
	public void lookupPlayerBans(final String playerName, String issuedBy, APIResponseCallback callback){
		JSONObject json = new JSONObject();
		
		json.put("action", "get_player_bans");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		this.requestHandler.addRequest(new APIRequest(this.apiURL, json, callback, 10000));
	}
	
	@SuppressWarnings("unchecked")
	public void lookupPlayerInfo(final String playerName, String issuedBy, APIResponseCallback callback){
		JSONObject json = new JSONObject();
		
		json.put("action", "get_player_info");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		this.requestHandler.addRequest(new APIRequest(this.apiURL, json, callback, 10000));
	}
	
	@SuppressWarnings("unchecked")
	public void lookupPlayerJoinInfo(final String playerName, String issuedBy, APIResponseCallback callback){
		JSONObject json = new JSONObject();
		
		json.put("action", "get_player_join_info");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		this.requestHandler.addRequest(new APIRequest(this.apiURL, json, callback, 10000));
	}
	
	@SuppressWarnings("unchecked")
	public SystemStatusData getAPIStatus(String issuedBy){
		JSONObject json = new JSONObject();
		
		json.put("action", "get_system_status");
		json.put("issued_by", issuedBy);
		
		try{
			return new SystemStatusData(this.requestHandler.processRequestDirect(new APIRequest(this.apiURL, json, 5000)));
		}catch (SocketTimeoutException ste){
			plugin.log.fatal("The API failed to respond in time.");
		}catch (IOException ioe){
			plugin.log.fatal("Failed to contact the API (you should report this).");
			ioe.printStackTrace();
		}catch (ParseException pe){
			plugin.log.fatal("Failed to parse API response (you should report this).");
			pe.printStackTrace();
		}catch (APIException apie){
			plugin.log.fatal("API Request Failed: " + ((APIException) apie).getResponse());
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void banPlayer(final String playerName, final String issuedBy, final BanReason reason, final Object evidence){
		JSONObject json = new JSONObject();
		
		json.put("action", "ban_player");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		json.put("reason", reason.getID());
		json.put("evidence", evidence);
		
		this.requestHandler.addRequest(new APIRequest(this.apiURL, json, new APIResponseCallback(){
			
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
		
		this.requestHandler.addRequest(new APIRequest(this.apiURL, json, new APIResponseCallback(){
			
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
	public PlayerBanData getPlayerBans(final String playerName, final String issuedBy, int timeout) throws SocketTimeoutException {
		JSONObject json = new JSONObject();
		
		json.put("action", "get_player_bans");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		try{
			return new PlayerBanData(this.requestHandler.processRequestDirect(new APIRequest(this.apiURL, json, timeout)));
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
	
	public PlayerBanData getPlayerBans(final String playerName, final String issuedBy) throws SocketTimeoutException {
		return this.getPlayerBans(playerName, issuedBy, 250);
	}
	
	@SuppressWarnings("unchecked")
	public PlayerInfoData getPlayerInfo(final String playerName, final String issuedBy, int timeout) throws SocketTimeoutException {
		JSONObject json = new JSONObject();
		
		json.put("action", "get_player_info");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		try{
			return new PlayerInfoData(this.requestHandler.processRequestDirect(new APIRequest(this.apiURL, json, timeout)));
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
	
	public PlayerInfoData getPlayerInfo(final String playerName, final String issuedBy) throws SocketTimeoutException {
		return this.getPlayerInfo(playerName, issuedBy, 250);
	}
	
	@SuppressWarnings("unchecked")
	public PlayerJoinData getPlayerJoinInfo(final String playerName, final String issuedBy, int timeout) throws SocketTimeoutException {
		JSONObject json = new JSONObject();
		
		json.put("action", "get_player_join_info");
		json.put("issued_by", issuedBy);
		json.put("player_name", playerName);
		
		try{
			return new PlayerJoinData(this.requestHandler.processRequestDirect(new APIRequest(this.apiURL, json, timeout)));
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
	
	public PlayerJoinData getPlayerJoinInfo(final String playerName, final String issuedBy) throws SocketTimeoutException {
		return this.getPlayerJoinInfo(playerName, issuedBy, 500);
	}
	
}
