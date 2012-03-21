package com.minebans.commands;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.minebans.MineBans;
import com.minebans.api.APIException;
import com.minebans.api.APIResponseCallback;
import com.minebans.api.PlayerBanData;
import com.minebans.bans.BanReason;
import com.minebans.bans.BanSeverity;

public class MineBansExecutor implements CommandExecutor {
	
	private MineBans plugin;
	
	public MineBansExecutor(MineBans plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args){
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Useage: /minebans <option> [args]"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Options:"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   reasons - Lists all of the ban reasons."));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   lookup - Gets a summary of a players bans."));
			return true;
		}
		
		String option = args[0];
		
		if (option.equalsIgnoreCase("reasons") || option.equalsIgnoreCase("r")){
			if (sender.hasPermission("minebans.admin.ban") == false){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
				return true;
			}
			
			sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Available ban reasons:"));
			
			BanReason[] reasons = BanReason.getAll();
			BanReason reason;
			
			StringBuilder line;
			List<String> keywords;
			int i, c;
			
			for (i = 0; i < reasons.length; ++i){
				reason = reasons[i];
				
				if (plugin.config.getBoolean(reason.getEnabledKey())){
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
			if (sender.hasPermission("minebans.admin.lookup") == false){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
				return true;
			}
			
			if (args.length != 2){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Useage: /minebans lookup <player_name>"));
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /minebans lookup wide_load"));
				return true;
			}
			
			plugin.api.lookupPlayerBans(args[1], sender.getName(), new APIResponseCallback(){
				
				public void onSuccess(String response) {
					PlayerBanData data;
					
					try{
						data = new PlayerBanData((JSONObject) ((JSONObject) (new JSONParser()).parse(response)));
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
						plugin.log.fatal("The API failed to response in time.");
					}else if (e instanceof UnsupportedEncodingException || e instanceof IOException){
						plugin.log.fatal("Failed to contact the API (you should report this on BukkitDev).");
						e.printStackTrace();
					}else if (e instanceof ParseException){
						plugin.log.fatal("Failed to parse API response (you should report this on BukkitDev).");
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
		}else{
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Invalid option, see /minebans for a list of options."));
			return true;
		}
		
		return true;
	}
	
}
