package com.minebans;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;

import uk.co.jacekk.bukkit.baseplugin.v1.BasePlugin;
import uk.co.jacekk.bukkit.baseplugin.v1.config.PluginConfig;
import uk.co.jacekk.bukkit.baseplugin.v1.update.BukkitDevUpdateChecker;

import com.minebans.api.APIInterface;
import com.minebans.api.data.StatusData;
import com.minebans.api.data.StatusMessageData;
import com.minebans.api.request.StatusMessageRequest;
import com.minebans.api.request.StatusRequest;
import com.minebans.bans.BanReason;
import com.minebans.commands.BanExecutor;
import com.minebans.commands.MineBansExecutor;
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

public class MineBans extends BasePlugin {
	
	public LoggingInterface loggingPlugin;
	public ExploitInterface exploitPlugin;
	public AntiSpamInterface antiSpamPlugin;
	
	public BanManager banManager;
	public EvidenceManager evidenceManager;
	public NotificationManager notificationManager;
	
	public APIInterface api;
	public BukkitDevUpdateChecker updateChecker;
	
	public ArrayList<String> seenPlayers;
	public HashMap<String, ArrayList<String>> banCommands;
	
	public void onEnable(){
		super.onEnable(true);
		
		if (!this.server.getOnlineMode()){
			this.log.fatal("Your server must be in online mode.");
			this.setEnabled(false);
			return;
		}
		
		this.config = new PluginConfig(new File(this.baseDirPath + File.separator + "config.yml"), Config.values(), this.log);
		
		this.loggingPlugin = new LoggingInterface(this);
		this.exploitPlugin = new ExploitInterface(this);
		this.antiSpamPlugin = new AntiSpamInterface(this);
		
		this.banManager = new BanManager(this);
		this.evidenceManager = new EvidenceManager(this);
		this.notificationManager = new NotificationManager(this);
		
		this.api = new APIInterface(this);
		this.updateChecker = new BukkitDevUpdateChecker(this, "http://dev.bukkit.org/server-mods/minebans/files.rss");
		
		this.seenPlayers = new ArrayList<String>();
		this.banCommands = new HashMap<String, ArrayList<String>>();
		
		this.pluginManager.registerEvents(new PlayerLoginListener(this), this);
		this.pluginManager.registerEvents(new PlayerJoinListener(this), this);
		this.pluginManager.registerEvents(new RequestVerificationListener(this), this);
		
		this.pluginManager.registerEvents(new PlayerBannedListener(this), this);
		
		if (this.config.getBoolean(Config.BLOCK_PROXIES)){
			this.pluginManager.registerEvents(new PublicProxyListener(this), this);
		}
		
		if (this.config.getBoolean(Config.BLOCK_COMPROMISED_ACCOUNTS)){
			this.pluginManager.registerEvents(new KnownCompromisedListener(this), this);
		}
		
		if (this.config.getBoolean(Config.USE_GROUP_BANS)){
			this.pluginManager.registerEvents(new GroupBanListener(this), this);
		}
		
		for (BanReason banReason : BanReason.getAll()){
			if (this.config.getBoolean(Config.getReasonEnabled(banReason))){
				this.pluginManager.registerEvents(new TooManyBansListener(this), this);
				break;
			}
		}
		
		for (Permission permission : Permission.values()){
			this.pluginManager.addPermission(new org.bukkit.permissions.Permission(permission.getNode(), permission.getDescription(), permission.getDefault()));
		}
		
		this.commandManager.registerCommandExecutor(new BanExecutor(this));
		this.commandManager.registerCommandExecutor(new ExemptExecutor(this));
		this.commandManager.registerCommandExecutor(new MineBansExecutor(this));
		
		this.scheduler.scheduleAsyncDelayedTask(this, new Runnable(){
			
			public void run(){
				MineBans.this.log.info("Checking API server communication.");
				
				long startTime = System.currentTimeMillis();
				StatusData status = new StatusRequest(MineBans.this, "CONSOLE").process();
				
				if (status == null){
					MineBans.this.log.warn("The API failed to respond, checking for known problems...");
					
					StatusMessageData data = (new StatusMessageRequest(MineBans.this)).process();
					
					if (data == null){
						MineBans.this.log.warn("We use Dropbox to provide the status announcements, for some reason it did not respond within 12 seconds.");
					}else{
						MineBans.this.log.warn("Status: " + data.getMessage());
					}
				}else{
					long ping = status.getResponceTime() - startTime;
					
					if (ping > 5000){
						MineBans.this.log.warn("The API took longer than 5 seconds to reply, players may experience slow logins.");
					}
					
					MineBans.this.log.info("The API responded in " + ping + "ms");
				}
			}
			
		}, 5L);
	}
	
	public void onDisable(){
		if (this.api != null){
			this.api.stopThread();
		}
		
		this.log.info("Disabled.");
	}
	
	public String getVersion(){
		return this.description.getVersion();
	}
	
	public MineBansPluginAPI getPluginAPI(Plugin plugin){
		return MineBansPluginAPI.getHandle(this, plugin);
	}
	
}
