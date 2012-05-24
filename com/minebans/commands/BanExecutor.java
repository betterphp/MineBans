package com.minebans.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.jacekk.bukkit.baseplugin.BaseCommandExecutor;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;
import com.minebans.MineBansPermission;
import com.minebans.bans.BanReason;

public class BanExecutor extends BaseCommandExecutor<MineBans> {
	
	public BanExecutor(MineBans plugin){
		super(plugin);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if (MineBansPermission.ADMIN_BAN.playerHasPermission(sender) == false){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return true;
		}
		
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /ban <player_name> [reason / ban_duration]"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (local): /ban wide_load"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (global): /ban wide_load 2"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (global): /ban wide_load griefing"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (temporary): /ban wide_load 12h"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example (temporary): /ban wide_load 7d"));
			return true;
		}
		
		String playerName = args[0];
		
		if (plugin.server.getOnlineMode() == false){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Your server must be running in online-mode."));
			return true;
		}
		
		if (sender instanceof Player && playerName.equalsIgnoreCase(sender.getName())){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You cannot ban yourself, that would be silly."));
			return true;
		}
		
		if (plugin.banManager.isBanned(playerName)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " has already been banned from this server."));
			return true;
		}
		
		if (args.length == 2){
			if (args[1].matches("\\d+[hd]{1}")){
				int banDuration = ((args[1].charAt(args[1].length() - 1) == 'h') ? 3600 : 86400) * (Integer.parseInt(args[1].substring(0, args[1].length() - 1)));
				
				if (banDuration <= 0){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "The ban duration must be positive."));
					return true;
				}
				
				if (banDuration > 604800){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You cannot temp ban a player for longer than 1 week."));
					return true;
				}
				
				plugin.banManager.tempBanPlayer(playerName, banDuration, (sender instanceof Player));
				sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been temporarily banned for " + args[1] + "."));
			}else{
				OfflinePlayer player = plugin.server.getOfflinePlayer(playerName);
				
				if (plugin.seenPlayers.contains(playerName.toLowerCase()) == false && player.isOnline() == false && player.hasPlayedBefore() == false){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You cannot globally ban a player that has never connected to the server."));
					return true;
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
					return true;
				}
				
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(reason)) == false){
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You cannot ban a player for a reason not enabled on the server."));
					sender.sendMessage(plugin.formatMessage(ChatColor.RED + "See /minebans reasons for a list of available reasons."));
					return true;
				}
				
				plugin.banManager.globallyBanPlayer(playerName, sender.getName(), reason, (sender instanceof Player));
				sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been globally banned."));
			}
		}else{
			plugin.banManager.locallyBanPlayer(playerName, (sender instanceof Player));
			sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been banned from the server."));
		}
		
		return true;
	}
	
}
