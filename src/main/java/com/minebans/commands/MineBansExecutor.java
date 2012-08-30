package com.minebans.commands;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uk.co.jacekk.bukkit.baseplugin.command.BaseCommandExecutor;
import uk.co.jacekk.bukkit.baseplugin.command.CommandHandler;

import com.minebans.MineBans;
import com.minebans.Config;
import com.minebans.Permission;
import com.minebans.api.APIException;
import com.minebans.api.APIResponseCallback;
import com.minebans.api.PlayerBanData;
import com.minebans.api.SystemStatusData;
import com.minebans.bans.BanReason;
import com.minebans.bans.BanSeverity;

public class MineBansExecutor extends BaseCommandExecutor<MineBans> {
	
	public MineBansExecutor(MineBans plugin){
		super(plugin);
	}
	
	@CommandHandler(names = {"minebans", "mbans", "mb"}, description = "Provides various commands relating to the system.", usage = "[option]")
	public void minebans(final CommandSender sender, String label, final String[] args){
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /" + label + " <option> [args]"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Options:"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   status - Gets the status of the API."));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   update - Checks for new versions of the plugin."));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   reasons - Lists all of the ban reasons."));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   lookup - Gets a summary of a players bans."));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   listtemp - Lists all of the players that are temporarily banned."));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   exec - Executes the commands for the last ban made."));
			return;
		}
		
		String senderName = sender.getName();
		String option = args[0];
		
		if (option.equalsIgnoreCase("status") || option.equalsIgnoreCase("s")){
			if (!Permission.ADMIN_STATUS.has(sender)){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
				return;
			}
			
			final long timeStart = System.currentTimeMillis();
			
			plugin.api.lookupAPIStatus(senderName, new APIResponseCallback(){
				
				public void onSuccess(String response){
					try{
						SystemStatusData status = new SystemStatusData(response);
						Double[] loadAvg = status.getLoadAvg();
						
						sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "The API responded in " + (status.getResponceTime() - timeStart) + "ms"));
						sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Server Load Average: " + ((loadAvg[0] > 8L) ? ChatColor.RED : ChatColor.GREEN) + loadAvg[0] + " " + loadAvg[1] + " " + loadAvg[2]));
					}catch (ParseException e){
						this.onFailure(e);
					}
				}
				
				public void onFailure(Exception e){
					if (e instanceof SocketTimeoutException){
						plugin.log.fatal("The API failed to respond in time.");
					}else if (e instanceof UnsupportedEncodingException || e instanceof IOException){
						plugin.log.fatal("Failed to contact the API (you should report this).");
						e.printStackTrace();
					}else if (e instanceof ParseException){
						plugin.log.fatal("Failed to parse API response (you should report this).");
						e.printStackTrace();
					}else if (e instanceof APIException){
						plugin.log.fatal("API Request Failed: " + ((APIException) e).getResponse());
					}
					
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "The API failed to respond, checking for known problems..."));
					
					plugin.api.lookupAPIStatusMessage(new APIResponseCallback(){
						
						public void onSuccess(String response){
							sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Result: " + response));
						}
						
						public void onFailure(Exception e){
							plugin.log.warn("We use Dropbox to provide the status announcements, for some reason it did not respond within 8 seconds.");
							sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Status: " + ChatColor.RED + "Unable to get info, check your server.log"));
						}
						
					});
				}
				
			});
		}else if (option.equalsIgnoreCase("update") || option.equalsIgnoreCase("u")){
			if (!Permission.ADMIN_UPDATE.has(sender)){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
				return;
			}
			
			plugin.api.lookupLatestVersion(new APIResponseCallback(){
				
				public void onSuccess(String response){
					if (plugin.getVersion().equals(response)){
						sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Up to date :D"));
					}else{
						sender.sendMessage(plugin.formatMessage(ChatColor.RED + "A new version is available, v" + response));
						sender.sendMessage(plugin.formatMessage(ChatColor.RED + "http://dev.bukkit.org/server-mods/minebans/files/"));
					}
				}
				
				public void onFailure(Exception e){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Failed to fetch latest version"));
					plugin.log.warn("Failed to fetch latest version: " + e.getMessage());
				}
				
			});
		}else if (option.equalsIgnoreCase("reasons") || option.equalsIgnoreCase("r")){
			if (!Permission.ADMIN_BAN.has(sender)){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
				return;
			}
			
			sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Available ban reasons:"));
			
			BanReason[] reasons = BanReason.getAll();
			BanReason reason;
			
			StringBuilder line;
			List<String> keywords;
			int i, c;
			
			for (i = 0; i < reasons.length; ++i){
				reason = reasons[i];
				
				if (plugin.config.getBoolean(Config.getReasonEnabled(reason))){
					line = new StringBuilder();
					
					keywords = reason.getKeywords();
					
					line.append(ChatColor.GREEN);
					line.append(String.valueOf(i + 1));
					
					if (i < 9){
						line.append(" ");
					}
					
					line.append(" - ");
					line.append(reason.getDescription());
					line.append(ChatColor.GRAY);
					line.append(" ");
					
					line.append(keywords.get(0));
					
					for (c = 1; c < keywords.size(); ++c){
						line.append(", ");
						line.append(keywords.get(c));
					}
					
					sender.sendMessage(line.toString());
				}
			}
		}else if (option.equalsIgnoreCase("lookup") || option.equalsIgnoreCase("l")){
			if (!Permission.ADMIN_LOOKUP.has(sender)){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
				return;
			}
			
			if (args.length != 2){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /minebans lookup <player_name>"));
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /minebans lookup wide_load"));
				return;
			}
			
			plugin.api.lookupPlayerBans(args[1], senderName, new APIResponseCallback(){
				
				public void onSuccess(String response) {
					PlayerBanData data;
					
					try{
						data = new PlayerBanData((JSONObject) (new JSONParser()).parse(response));
					}catch (ParseException e){
						this.onFailure(e);
						return;
					}
					
					Long total = data.getTotal();
					Long last24 = data.getLast24();
					Long removed = data.getRemoved();
					
					sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Summary for " + args[1]));
					
					sender.sendMessage(ChatColor.GREEN + "Total bans on record: " + ((total <= 5L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + total);
					sender.sendMessage(ChatColor.GREEN + "Bans in the last 24 hours: " + ((last24 == 0L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + last24);
					sender.sendMessage(ChatColor.GREEN + "Bans that have been removed: " + ((removed <= 10L) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + removed);
					
					if (data.getTotalRulesBroken() > 0){
						sender.sendMessage(ChatColor.GREEN + "Rules broken:");
						
						Long unconfirmed, confirmed, low, medium, high;
						
						for (BanReason banReason : data.getBans().keySet()){
							if (banReason.getSeverties().contains(BanSeverity.HIGH)){
								unconfirmed	= data.get(banReason, BanSeverity.UNCONFIRMED);
								low			= data.get(banReason, BanSeverity.LOW);
								medium		= data.get(banReason, BanSeverity.MEDIUM);
								high		= data.get(banReason, BanSeverity.HIGH);
								
								sender.sendMessage(ChatColor.GREEN + "   - " + banReason.getDescription() + ": " + ChatColor.GRAY + unconfirmed + " " + ChatColor.DARK_GREEN + low + " " + ChatColor.YELLOW + medium + " " + ChatColor.DARK_RED + high);
							}else{
								unconfirmed	= data.get(banReason, BanSeverity.UNCONFIRMED);
								confirmed	= data.get(banReason, BanSeverity.CONFIRMED);
								
								sender.sendMessage(ChatColor.GREEN + "   - " + banReason.getDescription() + ": " + ChatColor.GRAY + unconfirmed + " " + ChatColor.DARK_RED + confirmed);
							}
						}
					}
				}
				
				public void onFailure(Exception e){
					if (e instanceof SocketTimeoutException){
						plugin.log.fatal("The API failed to respond in time.");
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
						sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Failed to fetch bans for '" + args[1] + "'."));
						
						if (e instanceof APIException){
							sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Server Response: " + ((APIException) e).getResponse()));
						}
					}
				}
				
			});
		}else if (option.equalsIgnoreCase("listtemp") || option.equalsIgnoreCase("lt")){
			if (!Permission.ADMIN_LISTTEMP.has(sender)){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
				return;
			}
			
			List<String> playerNames = plugin.banManager.getTempBannedPlayers();
			
			sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "There are " + playerNames.size() + " players temporarily banned."));
			
			for (String playerName : playerNames){
				sender.sendMessage(ChatColor.GREEN + "  " + playerName + " - " + plugin.banManager.getTempBanRemaining(playerName) / 3600 + " hours");
			}
		}else if (option.equalsIgnoreCase("exec") || option.equalsIgnoreCase("e")){
			if (!Permission.ADMIN_BAN_COMMAND.has(sender)){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
				return;
			}
			
			ArrayList<String> cmds = plugin.banCommands.get(senderName);
			
			if (cmds == null){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "There are no commands to be executed."));
				return;
			}
			
			for (String cmd : cmds){
				plugin.server.dispatchCommand(sender, cmd);
			}
			
			plugin.banCommands.remove(senderName);
		}else{
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Invalid option, see /" + label + " for a list of options."));
		}
	}
	
}
