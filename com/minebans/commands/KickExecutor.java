package com.minebans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minebans.MineBans;

public class KickExecutor implements CommandExecutor {
	
	private MineBans plugin;
	
	public KickExecutor(MineBans plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if (sender.hasPermission("minebans.admin.kick") == false){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return true;
		}
		
		if (args.length == 0){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Useage: /kick <player_name>"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Example: /kick wide_load"));
			return true;
		}
		
		Player player = plugin.server.getPlayer(args[0]);
		
		if (player == null){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + args[0] + " is not online."));
			return true;
		}
		
		player.kickPlayer("You have been kicked from the server.");
		
		for (Player onlinePlayer : plugin.server.getOnlinePlayers()){
			if (onlinePlayer.hasPermission("globalbans.alert.onkick")){
				onlinePlayer.sendMessage(plugin.formatMessage(ChatColor.GREEN + args[0] + " has been kicked from the server."));
			}
		}
		
		plugin.log.info(args[0] + " has been kicked from the server.");
		
		return true;
	}
	
}
