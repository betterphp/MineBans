package com.minebans;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.minebans.api.APIInterface;
import com.minebans.bans.BanManager;
import com.minebans.commands.BanExecutor;
import com.minebans.commands.MineBansExecutor;
import com.minebans.commands.KickExecutor;
import com.minebans.commands.UnbanExecutor;
import com.minebans.commands.WhiteListExecutor;
import com.minebans.evidence.EvidenceManager;
import com.minebans.pluginInterfaces.ExploitInterface;
import com.minebans.pluginInterfaces.LoggingInterface;
import com.minebans.pluginapi.MineBansPluginAPI;
import com.minebans.util.MineBansConfig;
import com.minebans.util.MineBansLogger;

public class MineBans extends JavaPlugin {
	
	private PluginDescriptionFile pdFile;
	public MineBansLogger log;
	
	public Server server;
	public PluginManager pluginManager;
	public BukkitScheduler scheduler;
	
	public MineBansConfig config;
	
	public LoggingInterface loggingPlugin;
	public ExploitInterface exploitPlugin;
	
	public BanManager banManager;
	public EvidenceManager evidenceManager;
	
	public APIInterface api;
	
	public void onEnable(){
		this.pdFile = this.getDescription();
		this.log = new MineBansLogger(this);
		
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
		
		this.config = new MineBansConfig(new File(pluginFolder + File.separator + "config.yml"), this);
		
		this.loggingPlugin = new LoggingInterface(this);
		this.exploitPlugin = new ExploitInterface(this);
		
		if (this.loggingPlugin.foundLoggingPlugin() == false){
			this.setEnabled(false);
			return;
		}
		
		this.banManager = new BanManager(this);
		this.evidenceManager = new EvidenceManager(this);
		
		this.api = new APIInterface(this);
		
		this.pluginManager.registerEvents(new PlayerLoginListener(this), this);
		
		this.getCommand("ban").setExecutor(new BanExecutor(this));
		this.getCommand("unban").setExecutor(new UnbanExecutor(this));
		this.getCommand("kick").setExecutor(new KickExecutor(this));
		this.getCommand("whitelist").setExecutor(new WhiteListExecutor(this));
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
		return new MineBansPluginAPI(this, plugin);
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
		if (colour){
			return this.formatMessage(message, true, false);
		}else{
			return this.formatMessage(message, false, true);
		}
	}
	
	public String formatMessage(String message){
		return this.formatMessage(message, true, false);
	}
	
}
