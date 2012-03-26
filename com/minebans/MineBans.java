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
import com.minebans.bans.BanManager;
import com.minebans.bans.NotificationManager;
import com.minebans.commands.BanExecutor;
import com.minebans.commands.MineBansExecutor;
import com.minebans.commands.KickExecutor;
import com.minebans.commands.UnbanExecutor;
import com.minebans.commands.ExemptExecutor;
import com.minebans.evidence.EvidenceManager;
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
		
		this.banManager = new BanManager(this);
		this.evidenceManager = new EvidenceManager(this);
		this.notificationManager = new NotificationManager(this);
		
		this.api = new APIInterface(this);
		
		this.seenPlayers = new ArrayList<String>();
		
		this.pluginManager.registerEvents(new PlayerLoginListener(this), this);
		
		for (MineBansPermission permission : MineBansPermission.values()){
			pluginManager.addPermission(new Permission(permission.getNode(), permission.getDescription(), permission.getDefault()));
		}
		
		this.getCommand("ban").setExecutor(new BanExecutor(this));
		this.getCommand("unban").setExecutor(new UnbanExecutor(this));
		this.getCommand("kick").setExecutor(new KickExecutor(this));
		this.getCommand("exempt").setExecutor(new ExemptExecutor(this));
		this.getCommand("minebans").setExecutor(new MineBansExecutor(this));
		
		this.log.info("Enabled.");
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
