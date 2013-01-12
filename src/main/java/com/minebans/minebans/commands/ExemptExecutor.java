package com.minebans.minebans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.jacekk.bukkit.baseplugin.v8.command.BaseCommandExecutor;
import uk.co.jacekk.bukkit.baseplugin.v8.command.CommandHandler;
import uk.co.jacekk.bukkit.baseplugin.v8.command.CommandTabCompletion;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.NotificationManager;
import com.minebans.minebans.Permission;

public class ExemptExecutor extends BaseCommandExecutor<MineBans> {
	
	public ExemptExecutor(MineBans plugin){
		super(plugin);
	}
	
	@CommandHandler(names = {"exempt", "e", "be"}, description = "Used to manage the exempt list.", usage = "<option> <player_name>")
	@CommandTabCompletion({"add|remove", "<player>"})
	public void exempt(CommandSender sender, String label, String[] args){
		if (!Permission.ADMIN_EXEMPT.has(sender)){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You do not have permission to use this command."));
			return;
		}
		
		if (args.length != 2){
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Usage: /" + label + " <option> <player_name>"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Options:"));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   add - Adds the player to the exempt list."));
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "   remove - Removed the player from the exempt list."));
			return;
		}
		
		String option = args[0];
		String playerName = args[1];
		String issuedBy = sender.getName();
		
		if (issuedBy.equals("CONSOLE")){
			issuedBy = "console";
		}
		
		if (option.equalsIgnoreCase("add") || option.equalsIgnoreCase("a")){
			if (plugin.banManager.isExempt(playerName)){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " is already on the exempt list."));
				return;
			}
			
			plugin.banManager.exemptPlayer(playerName, issuedBy, (sender instanceof Player));
			
			sender.sendMessage(plugin.formatMessage(NotificationManager.parseNotification(plugin.config.getString(Config.MESSAGE_EXEMPT_SERVER), playerName, issuedBy, null, 0)));
		}else if (option.equalsIgnoreCase("remove") || option.equalsIgnoreCase("r")){
			if (!plugin.banManager.isExempt(playerName)){
				sender.sendMessage(plugin.formatMessage(ChatColor.RED + playerName + " is not on the exempt list."));
				return;
			}
			
			plugin.banManager.unExemptPlayer(playerName, issuedBy, (sender instanceof Player));
			
			sender.sendMessage(plugin.formatMessage(NotificationManager.parseNotification(plugin.config.getString(Config.MESSAGE_UNEXEMPT_SERVER), playerName, issuedBy, null, 0)));
		}else{
			sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Invalid option, try /" + label + " for a list of options."));
		}
	}
	
}
