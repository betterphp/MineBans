package com.minebans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.minebans.MineBans;
import com.minebans.Permission;

import uk.co.jacekk.bukkit.baseplugin.v7.command.BaseCommandExecutor;
import uk.co.jacekk.bukkit.baseplugin.v7.command.CommandHandler;
import uk.co.jacekk.bukkit.baseplugin.v7.command.CommandTabCompletion;

public class KickExecutor extends BaseCommandExecutor<MineBans> {
	
	public KickExecutor(MineBans plugin){
		super(plugin);
	}
	
	@CommandHandler(names = {"kick", "k"}, description = "Disconnects a player from the server.", usage = "<player_name> [reason]")
	@CommandTabCompletion({"<player>"})
	public void kick(CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_KICK.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Useage: /" + label + " <player_name> [reason]"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /" + label + " wide_load"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /" + label + " wide_load doing bad things :("));
			return;
		}
		
		if (!plugin.server.getOfflinePlayer(args[0]).isOnline()){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + args[0] + " is not online."));
			return;
		}
		
		if (args.length == 1){
			plugin.banManager.kickPlayer(args[0], true);
		}else{
			StringBuilder message = new StringBuilder();
			
			message.append(args[1]);
			
			for (int i = 2; i < args.length; ++i){
				message.append(' ');
				message.append(args[i]);
			}
			
			plugin.banManager.kickPlayer(args[0], true, message.toString());
		}
	}
	
}
