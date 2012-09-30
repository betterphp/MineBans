package com.minebans;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import uk.co.jacekk.bukkit.baseplugin.v2.permissions.PluginPermission;

public enum Permission implements PluginPermission {
	
	ALERT_ON_JOIN(		"minebans.alert.onjoin",		PermissionDefault.OP,		"Players with this permission will be shown a players ban summary when a player joins join."),
	ALERT_ON_API_FAIL(	"minebans.alert.onapifail",		PermissionDefault.OP,		"Players with this permission will be notified if a player joins before the API responds with their data."),
	ALERT_ON_BAN(		"minebans.alert.onban",			PermissionDefault.TRUE,		"Players with this permission will be notified when a player is banned."),
	ALERT_ON_UNBAN(		"minebans.alert.onunban",		PermissionDefault.TRUE,		"Players with this permission will be notified when a player is unbanned."),
	ALERT_ON_KICK(		"minebans.alert.onkick",		PermissionDefault.TRUE,		"Players with this permission will be notified when a player is kicked."),
	ALERT_ON_EXEMPT(	"minebans.alert.onexempt",		PermissionDefault.TRUE,		"Players with this permission will be notified when a player is added to the exempt list."),
	ALERT_ON_UNEXEMPT(	"minebans.alert.onunexempt",	PermissionDefault.TRUE,		"Players with this permission will be notified when a player is removed from the exempt list."),
	ALERT_ON_UPDATE(	"minebans.alert.onupdate",		PermissionDefault.OP,		"Players with this permission will be notified when a new version of the plugin is available."),
	
	ADMIN_STATUS(		"minebans.admin.status",		PermissionDefault.OP,		"Allows the player to check the status of the API."),
	ADMIN_UPDATE(		"minebans.admin.update",		PermissionDefault.OP,		"Allows the player to check for new versions of the plugin."),
	ADMIN_BAN(			"minebans.admin.ban",			PermissionDefault.OP,		"Allows the use of the /ban and /unban commands."),
	ADMIN_BAN_LOCAL(	"minebans.admin.ban.local",		PermissionDefault.FALSE,	"Allows the player to issue local bans."),
	ADMIN_BAN_GLOBAL(	"minebans.admin.ban.global",	PermissionDefault.FALSE,	"Allows the player to issue global bans."),
	ADMIN_BAN_TEMP(		"minebans.admin.ban.temp",		PermissionDefault.FALSE,	"Allows the player to issue temporary bans."),
	ADMIN_BAN_COMMAND(	"minebans.admin.bancommand",	PermissionDefault.OP,		"Allows the player to execute the configured commands when they ban a player"),
	ADMIN_KICK(			"minebans.admin.kick",			PermissionDefault.OP,		"Allows the use of the /kick command."),
	ADMIN_EXEMPT(		"minebans.admin.exempt",		PermissionDefault.OP,		"Allows the use of the /exempt commands."),
	ADMIN_LOOKUP(		"minebans.admin.lookup",		PermissionDefault.OP,		"Allows the player to lookup the bans another player has."),
	ADMIN_LISTTEMP(		"minebans.admin.listtemp",		PermissionDefault.OP,		"Allows the player to list all of the temporary bans."),
	ADMIN_IMPORT(		"minebans.admin.import",		PermissionDefault.OP,		"Allows the player to import existing bans.");
	
	private String node;
	private PermissionDefault defaultValue;
	private String description;
	
	private Permission(String node, PermissionDefault defaultValue, String description){
		this.node = node;
		this.defaultValue = defaultValue;
		this.description = description;
	}
	
	public List<Player> getPlayersWith(){
		ArrayList<Player> players = new ArrayList<Player>();
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()){
			if (this.has(player)){
				players.add(player);
			}
		}
		
		return players;
	}
	
	public boolean has(CommandSender sender){
		return sender.hasPermission(this.node);
	}
	
	public String getNode(){
		return this.node;
	}
	
	public PermissionDefault getDefault(){
		return this.defaultValue;
	}
	
	public String getDescription(){
		return this.description;
	}
	
}
