package com.minebans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import uk.co.jacekk.bukkit.baseplugin.BaseCommandExecutor;

import com.minebans.MineBans;
import com.minebans.MineBansPermission;

public class KickExecutor extends BaseCommandExecutor<MineBans> {
	
	public KickExecutor(MineBans plugin){
		super(plugin);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if (!MineBansPermission.ADMIN_KICK.playerHasPermission(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return true;
		}
		
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Useage: /" + label + " <player_name>"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /" + label + " wide_load"));
			return true;
		}
		
		if (!plugin.server.getOfflinePlayer(args[0]).isOnline()){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + args[0] + " is not online."));
			return true;
		}
		
		plugin.banManager.kickPlayer(args[0], true);
		
		return true;
	}
	
}
