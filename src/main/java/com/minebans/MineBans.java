package com.minebans;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;

import uk.co.jacekk.bukkit.baseplugin.v1.BasePlugin;
import uk.co.jacekk.bukkit.baseplugin.v1.config.PluginConfig;

import com.minebans.api.APIInterface;
import com.minebans.api.APIResponseCallback;
import com.minebans.api.SystemStatusData;
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
				SystemStatusData status = MineBans.this.api.getAPIStatus("CONSOLE");
				long ping = System.currentTimeMillis() - startTime;
				
				if (status == null){
					MineBans.this.log.warn("The API failed to respond, checking for known problems...");
					
					MineBans.this.api.lookupAPIStatusMessage(new APIResponseCallback(){
						
						public void onSuccess(String response){
							MineBans.this.log.warn("Status: " + response);
						}
						
						public void onFailure(Exception e){
							MineBans.this.log.warn("We use Dropbox to provide the status announcements, for some reason it did not respond within 10 seconds.");
							
							e.printStackTrace();
						}
						
					});
				}else{
					if (ping > 4000){
						MineBans.this.log.warn("The API took longer than 4 seconds to reply.");
						MineBans.this.log.warn("This is not a serious problem but players may experience longer than normal login times.");
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
