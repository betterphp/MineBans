package com.minebans;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public enum MineBansPermission {
	
	ALERT_ON_JOIN(		"minebans.alert.onjoin",		PermissionDefault.OP,		"Players with this permission will be shown a players ban summary when the join."),
	ALERT_ON_API_FAIL(	"minebans.alert.onapifail",		PermissionDefault.OP,		"Players with this permission will be notified if a player joins before the API responds with their data."),
	ALERT_ON_BAN(		"minebans.alert.onban",			PermissionDefault.TRUE,		"Players with this permission will be notified when a player is banned."),
	ALERT_ON_UNBAN(		"minebans.alert.onunban",		PermissionDefault.TRUE,		"Players with this permission will be notified when a player is unbanned."),
	ALERT_ON_KICK(		"minebans.alert.onkick",		PermissionDefault.TRUE,		"Players with this permission will be notified when a player is kicked."),
	ALERT_ON_EXEMPT(	"minebans.alert.onexempt",		PermissionDefault.TRUE,		"Players with this permission will be notified when a player is added to the exempt list."),
	ALERT_ON_UNEXEMPT(	"minebans.alert.onunexempt",	PermissionDefault.TRUE,		"Players with this permission will be notified when a player is removed from the exempt list."),
	
	ADMIN_STATUS(		"minebans.admin.status",		PermissionDefault.OP,		"Allows the player to check the status of the API."),
	ADMIN_BAN(			"minebans.admin.ban",			PermissionDefault.OP,		"Allows the use of the /ban and /unban commands."),
	ADMIN_KICK(			"minebans.admin.kick",			PermissionDefault.OP,		"Allows the use of the /kick command."),
	ADMIN_EXEMPT(		"minebans.admin.exempt",		PermissionDefault.OP,		"Allows the use of the /exempt commands."),
	ADMIN_LOOKUP(		"minebans.admin.lookup",		PermissionDefault.OP,		"Allows the player to lookup the bans another player has."),
	ADMIN_LISTTEMP(		"minebans.admin.listtemp",		PermissionDefault.OP,		"Allows the player to list all of the temporary bans.");
	
	private String node;
	private PermissionDefault defaultValue;
	private String description;
	
	private MineBansPermission(String node, PermissionDefault defaultValue, String description){
		this.node = node;
		this.defaultValue = defaultValue;
		this.description = description;
	}
	
	public List<Player> getPlayersWithPermission(){
		ArrayList<Player> players = new ArrayList<Player>();
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()){
			if (this.playerHasPermission(player)){
				players.add(player);
			}
		}
		
		return players;
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
	
	public Boolean playerHasPermission(CommandSender sender){
		return sender.hasPermission(this.node);
	}
	
}
