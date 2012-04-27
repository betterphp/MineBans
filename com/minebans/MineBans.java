package com.minebans;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.minebans.api.APIInterface;
import com.minebans.api.SystemStatusData;
import com.minebans.bans.BanReason;
import com.minebans.commands.BanExecutor;
import com.minebans.commands.MineBansExecutor;
import com.minebans.commands.KickExecutor;
import com.minebans.commands.UnbanExecutor;
import com.minebans.commands.ExemptExecutor;
import com.minebans.joindatalisteners.GroupBanListener;
import com.minebans.joindatalisteners.KnownCompromisedListener;
import com.minebans.joindatalisteners.PlayerBannedListener;
import com.minebans.joindatalisteners.PublicProxyListener;
import com.minebans.joindatalisteners.TooManyBansListener;
import com.minebans.pluginInterfaces.AntiSpamInterface;
import com.minebans.pluginInterfaces.ExploitInterface;
import com.minebans.pluginInterfaces.LoggingInterface;
import com.minebans.pluginapi.MineBansPluginAPI;
import com.minebans.util.PluginConfig;
import com.minebans.util.PluginLogger;

public class MineBans extends JavaPlugin {
	
	private PluginDescriptionFile pdFile;
	public PluginLogger log;
	
	public Server server;
	public PluginManager pluginManager;
	public BukkitScheduler scheduler;
	
	public PluginConfig config;
	
	public LoggingInterface loggingPlugin;
	public ExploitInterface exploitPlugin;
	public AntiSpamInterface antiSpamPlugin;
	
	public BanManager banManager;
	public EvidenceManager evidenceManager;
	public NotificationManager notificationManager;
	
	public APIInterface api;
	
	public ArrayList<String> seenPlayers;
	
	public void onEnable(){
		this.pdFile = this.getDescription();
		this.log = new PluginLogger(this);
		
		this.server = this.getServer();
		this.pluginManager = this.server.getPluginManager();
		this.scheduler = this.server.getScheduler();
		
		if (this.server.getOnlineMode() == false){
			this.log.fatal("Your server must be in online mode.");
			this.setEnabled(false);
			return;
		}
		
		String pluginFolder = this.getDataFolder().getAbsolutePath();
		
		(new File(pluginFolder)).mkdirs();
		
		this.config = new PluginConfig(new File(pluginFolder + File.separator + "config.yml"), this.log);
		
		this.loggingPlugin = new LoggingInterface(this);
		this.exploitPlugin = new ExploitInterface(this);
		this.antiSpamPlugin = new AntiSpamInterface(this);
		
		this.banManager = new BanManager(this);
		this.evidenceManager = new EvidenceManager(this);
		this.notificationManager = new NotificationManager(this);
		
		this.api = new APIInterface(this);
		
		this.seenPlayers = new ArrayList<String>();
		
		this.pluginManager.registerEvents(new PlayerLoginListener(this), this);
		
		this.pluginManager.registerEvents(new PlayerBannedListener(this), this);
		
		if (this.config.getBoolean(MineBansConfig.BLOCK_PROXIES)){
			this.pluginManager.registerEvents(new PublicProxyListener(this), this);
		}
		
		if (this.config.getBoolean(MineBansConfig.BLOCK_COMPROMISED_ACCOUNTS)){
			this.pluginManager.registerEvents(new KnownCompromisedListener(), this);
		}
		
		if (this.config.getBoolean(MineBansConfig.USE_GROUP_BANS)){
			this.pluginManager.registerEvents(new GroupBanListener(), this);
		}
		
		for (BanReason banReason : BanReason.getAll()){
			if (this.config.getBoolean(MineBansConfig.getReasonEnabled(banReason))){
				this.pluginManager.registerEvents(new TooManyBansListener(this), this);
				break;
			}
		}
		
		for (MineBansPermission permission : MineBansPermission.values()){
			this.pluginManager.addPermission(new Permission(permission.getNode(), permission.getDescription(), permission.getDefault()));
		}
		
		this.getCommand("ban").setExecutor(new BanExecutor(this));
		this.getCommand("unban").setExecutor(new UnbanExecutor(this));
		this.getCommand("kick").setExecutor(new KickExecutor(this));
		this.getCommand("exempt").setExecutor(new ExemptExecutor(this));
		this.getCommand("minebans").setExecutor(new MineBansExecutor(this));
		
		this.log.info("Enabled successfully, checking API server communication.");
		
		long startTime = System.currentTimeMillis();
		SystemStatusData status = this.api.getAPIStatus("CONSOLE");
		long ping = System.currentTimeMillis() - startTime;
		
		if (status == null){
			this.log.warn("Failed to contact the API");
		}else if (ping > 500){
			this.log.warn("The API took longer than 500ms to reply, this is not a serious problem but due to a technical limitation,");
			this.log.warn("the check with the API has to make the entire server wait until the request completes. If this takes longer");
			this.log.warn("than 500ms it is assumed that the player is allowed to join and a check with a longer delay is scheduled.");
			this.log.warn("This is done to prevent player joins causing noticeable server lag. The player may be online for a few seconds");
			this.log.warn("while the API request is still waiting, this is normal and nothing to worry about :)");
			this.log.warn("Your API Responce Time: " + ping);
		}else{
			this.log.info("The API server responded to your request successfully.");
		}
	}
	
	public void onDisable(){
		if (this.api != null){
			this.api.stopThread();
		}
		
		this.log.info("Disabled.");
	}
	
	public String getVersion(){
		return this.pdFile.getVersion();
	}
	
	public MineBansPluginAPI getPluginAPI(Plugin plugin){
		return MineBansPluginAPI.getHandle(this, plugin);
	}
	
	public String formatMessage(String message, boolean colour, boolean version){
		StringBuilder line = new StringBuilder();
		
		if (colour){
			line.append(ChatColor.BLUE);
		}
		
		line.append("[");
		line.append(this.pdFile.getName());
		
		if (version){
			line.append(" v");
			line.append(this.pdFile.getVersion());
		}
		
		line.append("] ");
		line.append(message);
		
		return line.toString();
	}
	
	public String formatMessage(String message, boolean colour){
		return this.formatMessage(message, colour, !colour);
	}
	
	public String formatMessage(String message){
		return this.formatMessage(message, true, false);
	}
	
}
