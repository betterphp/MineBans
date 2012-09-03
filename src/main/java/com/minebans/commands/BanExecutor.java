package com.minebans.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.jacekk.bukkit.baseplugin.v1.command.BaseCommandExecutor;
import uk.co.jacekk.bukkit.baseplugin.v1.command.CommandHandler;

import com.minebans.MineBans;
import com.minebans.Config;
import com.minebans.Permission;
import com.minebans.bans.BanReason;

public class BanExecutor extends BaseCommandExecutor<MineBans> {
	
	public BanExecutor(MineBans plugin){
		super(plugin);
	}
	
	@CommandHandler(names = {"ban", "b"}, description = "Bans a player from the server.", usage = "[player_name] [reason_id/reason_keyword]")
	public void ban(CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_BAN.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /" + label + " <player_name> [reason / ban_duration]"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (local): /" + label + " wide_load"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (global): /" + label + " wide_load 2"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (global): /" + label + " wide_load griefing"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (temporary): /" + label + " wide_load 12h"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (temporary): /" + label + " wide_load 7d"));
			return;
		}
		
		String playerName = args[0];
		
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
				int banDuration = ((args[1].charAt(args[1].length() - 1) == 'h') ? 3600 : 86400) * (Integer.parseInt(args[1].substring(0, args[1].length() - 1)));
				
				if (banDuration <= 0){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "The ban duration must be positive."));
					return;
				}
				
				if (banDuration > 604800){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You cannot temp ban a player for longer than 1 week."));
					return;
				}
				
				plugin.banManager.tempBanPlayer(playerName, banDuration, (sender instanceof Player));
				sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been temporarily banned for " + args[1] + "."));
				
				for (String cmd : plugin.config.getStringList(Config.TEMP_BAN_COMMANDS)){
					cmds.add(cmd.replace("%player_name%", playerName));
				}
			}else{
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
				sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been globally banned."));
				
				for (String cmd : plugin.config.getStringList(Config.GLOBAL_BAN_COMMANDS)){
					cmds.add(cmd.replace("%player_name%", playerName));
				}
			}
		}else{
			plugin.banManager.locallyBanPlayer(playerName, (sender instanceof Player));
			sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been banned from the server."));
			
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
	
	@CommandHandler(names = {"unban", "ub"}, description = "Unbans a player.", usage = "/unban [player_name]")
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
		
		if (plugin.banManager.isBanned(playerName) == false){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " has not been banned from this server."));
			return;
		}
		
		plugin.banManager.unbanPlayer(playerName, sender.getName(), (sender instanceof Player));
		
		sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been unbanned."));
	}
	
	@CommandHandler(names = {"kick", "k"}, description = "Disconnects a player from the server.", usage = "/kick [player_name]")
	public void kick(CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_KICK.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Useage: /" + label + " <player_name>"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /" + label + " wide_load"));
			return;
		}
		
		if (!plugin.server.getOfflinePlayer(args[0]).isOnline()){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + args[0] + " is not online."));
			return;
		}
		
		plugin.banManager.kickPlayer(args[0], true);
	}
	
}
