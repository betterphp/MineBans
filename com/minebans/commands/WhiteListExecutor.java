package com.minebans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minebans.MineBans;

public class WhiteListExecutor implements CommandExecutor {
	
	private MineBans plugin;
	
	public WhiteListExecutor(MineBans plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args){
		if (sender.hasPermission("minebans.admin.whitelist") == false){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return true;
		}
		
		if (args.length != 2){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /whitelist <option> <player_name>"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Options:"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   add - Adds the player to the whitelist."));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   remove - Removed the player from the whitelist."));
			return true;
		}
		
		String option = args[0];
		String playerName = args[1];
		
		if (option.equalsIgnoreCase("add") || option.equalsIgnoreCase("a")){
			if (plugin.banManager.isWhitelisted(playerName)){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " is already on the whitelist."));
				return true;
			}
			
			plugin.banManager.whiteListPlayer(playerName, (sender instanceof Player));
			
			sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been added to the whitelist."));
		}else if (option.equalsIgnoreCase("remove") || option.equalsIgnoreCase("r")){
			if (plugin.banManager.isWhitelisted(playerName) == false){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " is not on the whitelist."));
				return true;
			}
			
			plugin.banManager.unWhiteListPlayer(playerName, (sender instanceof Player));
			
			sender.sendMessage(plugin.formatMessage(ChatColor.GREEN + playerName + " has been removed from the whitelist."));
		}else{
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Invalid option, see /whitelist for a list of options."));
			return true;
		}
		
		return true;
	}
	
}
