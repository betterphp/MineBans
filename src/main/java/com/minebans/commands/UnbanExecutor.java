package com.minebans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.jacekk.bukkit.baseplugin.BaseCommandExecutor;

import com.minebans.MineBans;
import com.minebans.Permission;

public class UnbanExecutor extends BaseCommandExecutor<MineBans> {
	
	public UnbanExecutor(MineBans plugin){
		super(plugin);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if (!Permission.ADMIN_BAN.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return true;
		}
		
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /" + label + " <player_name>"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /" + label + " wide_load"));
			return true;
		}
		
		String playerName = args[0];
		
		if (plugin.banManager.isBanned(playerName) == false){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " has not been banned from this server."));
			return true;
		}
		
		plugin.banManager.unbanPlayer(playerName, sender.getName(), (sender instanceof Player));
		
		sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been unbanned."));
		
		return true;
	}
	
}
