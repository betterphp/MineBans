package com.minebans.minebans;

import org.bukkit.permissions.PermissionDefault;

import uk.co.jacekk.bukkit.baseplugin.v7.permissions.PluginPermission;

public class Permission {
	
	public static final PluginPermission ALERT_ON_JOIN		= new PluginPermission("minebans.alert.onjoin",		PermissionDefault.OP,		"Players with this permission will be shown a players ban summary when a player joins join.");
	public static final PluginPermission ALERT_ON_API_FAIL	= new PluginPermission("minebans.alert.onapifail",	PermissionDefault.OP,		"Players with this permission will be notified if a player joins before the API responds with their data.");
	public static final PluginPermission ALERT_ON_BAN		= new PluginPermission("minebans.alert.onban",		PermissionDefault.TRUE,		"Players with this permission will be notified when a player is banned.");
	public static final PluginPermission ALERT_ON_UNBAN		= new PluginPermission("minebans.alert.onunban",	PermissionDefault.TRUE,		"Players with this permission will be notified when a player is unbanned.");
	public static final PluginPermission ALERT_ON_KICK		= new PluginPermission("minebans.alert.onkick",		PermissionDefault.TRUE,		"Players with this permission will be notified when a player is kicked.");
	public static final PluginPermission ALERT_ON_EXEMPT	= new PluginPermission("minebans.alert.onexempt",	PermissionDefault.TRUE,		"Players with this permission will be notified when a player is added to the exempt list.");
	public static final PluginPermission ALERT_ON_UNEXEMPT	= new PluginPermission("minebans.alert.onunexempt",	PermissionDefault.TRUE,		"Players with this permission will be notified when a player is removed from the exempt list.");
	public static final PluginPermission ALERT_ON_UPDATE	= new PluginPermission("minebans.alert.onupdate",	PermissionDefault.OP,		"Players with this permission will be notified when a new version of the plugin is available.");
	
	public static final PluginPermission ADMIN_STATUS		= new PluginPermission("minebans.admin.status",		PermissionDefault.OP,		"Allows the player to check the status of the API.");
	public static final PluginPermission ADMIN_UPDATE		= new PluginPermission("minebans.admin.update",		PermissionDefault.OP,		"Allows the player to check for new versions of the plugin.");
	public static final PluginPermission ADMIN_BAN			= new PluginPermission("minebans.admin.ban",		PermissionDefault.OP,		"Allows the use of the /ban and /unban commands.");
	public static final PluginPermission ADMIN_BAN_LOCAL	= new PluginPermission("minebans.admin.ban.local",	PermissionDefault.FALSE,	"Allows the player to issue local bans.");
	public static final PluginPermission ADMIN_BAN_GLOBAL	= new PluginPermission("minebans.admin.ban.global",	PermissionDefault.FALSE,	"Allows the player to issue global bans.");
	public static final PluginPermission ADMIN_BAN_TEMP		= new PluginPermission("minebans.admin.ban.temp",	PermissionDefault.FALSE,	"Allows the player to issue temporary bans.");
	public static final PluginPermission ADMIN_BAN_COMMAND	= new PluginPermission("minebans.admin.bancommand",	PermissionDefault.OP,		"Allows the player to execute the configured commands when they ban a player");
	public static final PluginPermission ADMIN_KICK			= new PluginPermission("minebans.admin.kick",		PermissionDefault.OP,		"Allows the use of the /kick command.");
	public static final PluginPermission ADMIN_EXEMPT		= new PluginPermission("minebans.admin.exempt",		PermissionDefault.OP,		"Allows the use of the /exempt commands.");
	public static final PluginPermission ADMIN_LOOKUP		= new PluginPermission("minebans.admin.lookup",		PermissionDefault.OP,		"Allows the player to lookup the bans another player has.");
	public static final PluginPermission ADMIN_LISTTEMP		= new PluginPermission("minebans.admin.listtemp",	PermissionDefault.OP,		"Allows the player to list all of the temporary bans.");
	public static final PluginPermission ADMIN_IMPORT		= new PluginPermission("minebans.admin.import",		PermissionDefault.OP,		"Allows the player to import existing bans.");
	
}
