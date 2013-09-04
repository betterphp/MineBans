package com.minebans.minebans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.NotificationManager;
import com.minebans.minebans.Permission;

import uk.co.jacekk.bukkit.baseplugin.command.BaseCommandExecutor;
import uk.co.jacekk.bukkit.baseplugin.command.CommandHandler;
import uk.co.jacekk.bukkit.baseplugin.command.CommandTabCompletion;

public class KickExecutor extends BaseCommandExecutor<MineBans> {
	
	public KickExecutor(MineBans plugin){
		super(plugin);
	}
	
	@CommandHandler(names = {"kick", "k"}, description = "Disconnects a player from the server.", usage = "<player_name> [reason]")
	@CommandTabCompletion({"<player>"})
	public void kick(CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_KICK.has(sender)){
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			return;
		}
		
		if (args.length == 0){
			sender.sendMessage(ChatColor.RED + "Useage: /" + label + " <player_name> [reason]");
			sender.sendMessage(ChatColor.RED + "Example: /" + label + " wide_load");
			sender.sendMessage(ChatColor.RED + "Example: /" + label + " wide_load doing bad things :(");
			return;
		}
		
		if (!plugin.getServer().getOfflinePlayer(args[0]).isOnline()){
			sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return;
		}
		
		String playerName = args[0];
		String issuedBy = sender.getName();
		
		if (issuedBy.equals("CONSOLE")){
			issuedBy = "console";
		}
		
		if (args.length == 1){
			plugin.banManager.kickPlayer(playerName, issuedBy, true);
		}else{
			StringBuilder message = new StringBuilder();
			
			message.append(args[1]);
			
			for (int i = 2; i < args.length; ++i){
				message.append(' ');
				message.append(args[i]);
			}
			
			plugin.banManager.kickPlayer(playerName, issuedBy, true, message.toString());
			
			sender.sendMessage(NotificationManager.parseNotification(plugin.config.getString(Config.MESSAGE_KICK_SERVER), playerName, issuedBy, null, 0));
		}
	}
	
}
