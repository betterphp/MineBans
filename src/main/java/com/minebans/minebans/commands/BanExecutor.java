package com.minebans.minebans.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.jacekk.bukkit.baseplugin.v9.command.BaseCommandExecutor;
import uk.co.jacekk.bukkit.baseplugin.v9.command.CommandHandler;
import uk.co.jacekk.bukkit.baseplugin.v9.command.CommandTabCompletion;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.NotificationManager;
import com.minebans.minebans.Permission;
import com.minebans.minebans.bans.BanReason;

public class BanExecutor extends BaseCommandExecutor<MineBans> {
	
	public BanExecutor(MineBans plugin){
		super(plugin);
	}
	
	public List<String> banReasonCompletor(CommandSender sender, String[] args){
		ArrayList<String> values = new ArrayList<String>();
		
		for (BanReason reason : BanReason.getAll()){
			if (plugin.config.getBoolean(Config.getReasonEnabled(reason))){
				values.add(reason.getKeywords().get(0));
			}
		}
		
		values.add("2h");
		values.add("12h");
		values.add("1d");
		values.add("2d");
		values.add("5d");
		values.add("7d");
		
		return values;
	}
	
	@CommandHandler(names = {"ban", "b"}, description = "Bans a player from the server.", usage = "<player_name> [reason_id/reason_keyword]")
	@CommandTabCompletion({"<player>", "[banReasonCompletor]"})
	public void ban(CommandSender sender, String label, String[] args){
		boolean all = Permission.ADMIN_BAN.has(sender);
		boolean global = Permission.ADMIN_BAN_GLOBAL.has(sender);
		boolean local = Permission.ADMIN_BAN_LOCAL.has(sender);
		boolean temp = Permission.ADMIN_BAN_TEMP.has(sender);
		
		if (!all && !global && !local && !temp){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /" + label + " <player_name> [reason / ban_duration]"));
			
			if (local){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (local): /" + label + " wide_load"));
			}
			
			if (global){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (global): /" + label + " wide_load 2"));
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (global): /" + label + " wide_load griefing"));
			}
			
			if (temp){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (temporary): /" + label + " wide_load 12h"));
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (temporary): /" + label + " wide_load 7d"));
			}
			
			return;
		}
		
		String playerName = args[0];
		String issuedBy = sender.getName();
		
		if (issuedBy.equals("CONSOLE")){
			issuedBy = "console";
		}
		
		if (!plugin.server.getOnlineMode()){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Your server must be running in online-mode."));
			return;
		}
		
		if (sender instanceof Player && playerName.equalsIgnoreCase(sender.getName())){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You cannot ban yourself, that would be silly."));
			return;
		}
		
		if (plugin.banManager.isBanned(playerName)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " has already been banned from this server."));
			return;
		}
		
		ArrayList<String> cmds = new ArrayList<String>();
		
		if (args.length == 2){
			if (args[1].matches("\\d+[hd]{1}")){
				if (!all && !temp){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
					return;
				}
				
				String maxDurationString = plugin.config.getString(Config.MAX_TEMP_BAN_DURATION);
				
				int banDuration = ((args[1].charAt(args[1].length() - 1) == 'h') ? 3600 : 86400) * (Integer.parseInt(args[1].substring(0, args[1].length() - 1)));
				int maxDuration = ((maxDurationString.charAt(maxDurationString.length() - 1) == 'h') ? 3600 : 86400) * (Integer.parseInt(maxDurationString.substring(0, maxDurationString.length() - 1)));
				
				if (banDuration <= 0){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "The ban duration must be positive."));
					return;
				}
				
				if (banDuration > maxDuration){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You cannot temp ban a player for longer than " + maxDurationString + "."));
					return;
				}
				
				plugin.banManager.tempBanPlayer(playerName, issuedBy, banDuration, (sender instanceof Player));
				sender.sendMessage(plugin.formatMessage(NotificationManager.parseNotification(plugin.config.getString(Config.MESSAGE_TEMP_BAN_SERVER), playerName, issuedBy, null, banDuration)));
				
				for (String cmd : plugin.config.getStringList(Config.TEMP_BAN_COMMANDS)){
					cmds.add(cmd.replace("%player_name%", playerName));
				}
			}else{
				if (!all && !global){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
					return;
				}
				
				OfflinePlayer player = plugin.server.getOfflinePlayer(playerName);
				
				if (!plugin.seenPlayers.contains(playerName.toLowerCase()) && !player.isOnline() && !player.hasPlayedBefore()){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You cannot globally ban a player that has never connected to the server."));
					return;
				}
				
				BanReason reason = null;
				
				if (args[1].matches("\\d+")){
					try{
						reason = BanReason.getFromID(Integer.parseInt(args[1]) - 1);
					}catch (Exception e){
						e.printStackTrace();
					}
				}else{
					reason = BanReason.getFromKeyword(args[1].toLowerCase());
				}
				
				if (reason == null){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "That ban reason is not valid, try using"));
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "/minebans reasons for a list of reasons."));
					return;
				}
				
				if (!plugin.config.getBoolean(Config.getReasonEnabled(reason))){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You cannot ban a player for a reason not enabled on the server."));
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "See /minebans reasons for a list of available reasons."));
					return;
				}
				
				plugin.banManager.globallyBanPlayer(playerName, sender.getName(), reason, (sender instanceof Player));
				sender.sendMessage(plugin.formatMessage(NotificationManager.parseNotification(plugin.config.getString(Config.MESSAGE_GLOBAL_BAN_SERVER), playerName, issuedBy, reason, 0)));
				
				for (String cmd : plugin.config.getStringList(Config.GLOBAL_BAN_COMMANDS)){
					cmds.add(cmd.replace("%player_name%", playerName));
				}
			}
		}else{
			if (!all && !local){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
				return;
			}
			
			plugin.banManager.locallyBanPlayer(playerName, issuedBy, (sender instanceof Player));
			sender.sendMessage(plugin.formatMessage(NotificationManager.parseNotification(plugin.config.getString(Config.MESSAGE_LOCAL_BAN_SERVER), playerName, issuedBy, null, 0)));
			
			for (String cmd : plugin.config.getStringList(Config.LOCAL_BAN_COMMANDS)){
				cmds.add(cmd.replace("%player_name%", playerName));
			}
		}
		
		if (Permission.ADMIN_BAN_COMMAND.has(sender) && !cmds.isEmpty()){
			if (plugin.config.getBoolean(Config.BAN_COMMANDS_AUTO)){
				for (String cmd : cmds){
					plugin.server.dispatchCommand(sender, cmd);
				}
			}else{
				sender.sendMessage(plugin.formatMessage(ChatColor.GREEN.toString() + ChatColor.ITALIC + "/minebans exec" + ChatColor.RESET + ChatColor.GREEN + " will execute the following commands."));
				
				for (String cmd : cmds){
					sender.sendMessage(ChatColor.GREEN + "  - /" + cmd);
				}
				
				plugin.banCommands.put(sender.getName(), cmds);
			}
		}
	}
	
	@CommandHandler(names = {"unban", "ub"}, description = "Unbans a player.", usage = "<player_name>")
	@CommandTabCompletion({"<player>"})
	public void unban(CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_BAN.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /" + label + " <player_name>"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /" + label + " wide_load"));
			return;
		}
		
		String playerName = args[0];
		String issuedBy = sender.getName();
		
		if (issuedBy.equals("CONSOLE")){
			issuedBy = "console";
		}
		
		if (plugin.banManager.isBanned(playerName) == false){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " has not been banned from this server."));
			return;
		}
		
		plugin.banManager.unbanPlayer(playerName, issuedBy, (sender instanceof Player));
		
		sender.sendMessage(plugin.formatMessage(NotificationManager.parseNotification(plugin.config.getString(Config.MESSAGE_UNBAN_SERVER), playerName, issuedBy, null, 0)));
	}
	
}
