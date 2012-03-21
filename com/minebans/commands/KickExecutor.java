package com.minebans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.minebans.MineBans;
import com.minebans.MineBansPermission;

public class KickExecutor implements CommandExecutor {
	
	private MineBans plugin;
	
	public KickExecutor(MineBans plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if (MineBansPermission.ADMIN_KICK.playerHasPermission(sender) == false){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return true;
		}
		
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Useage: /kick <player_name>"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /kick wide_load"));
			return true;
		}
		
		if (plugin.server.getOfflinePlayer(args[0]).isOnline() == false){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + args[0] + " is not online."));
			return true;
		}
		
		plugin.banManager.kickPlayer(args[0], true);
		
		return true;
	}
	
}
