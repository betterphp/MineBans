package com.minebans;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import uk.co.jacekk.bukkit.baseplugin.BasePlugin;
import uk.co.jacekk.bukkit.baseplugin.config.PluginConfig;

import com.minebans.api.APIInterface;
import com.minebans.api.APIResponseCallback;
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

public class MineBans extends BasePlugin {
	
	public LoggingInterface loggingPlugin;
	public ExploitInterface exploitPlugin;
	public AntiSpamInterface antiSpamPlugin;
	
	public BanManager banManager;
	public EvidenceManager evidenceManager;
	public NotificationManager notificationManager;
	
	public APIInterface api;
	
	public ArrayList<String> seenPlayers;
	
	public void onEnable(){
		super.onEnable(true);
		
		if (!this.server.getOnlineMode()){
			this.log.fatal("Your server must be in online mode.");
			this.setEnabled(false);
			return;
		}
		
		this.config = new PluginConfig(new File(this.baseDirPath + File.separator + "config.yml"), MineBansConfig.values(), this.log);
		
		this.loggingPlugin = new LoggingInterface(this);
		this.exploitPlugin = new ExploitInterface(this);
		this.antiSpamPlugin = new AntiSpamInterface(this);
		
		this.banManager = new BanManager(this);
		this.evidenceManager = new EvidenceManager(this);
		this.notificationManager = new NotificationManager(this);
		
		this.api = new APIInterface(this);
		
		this.seenPlayers = new ArrayList<String>();
		
		try{
			Class.forName("org.bukkit.event.player.AsyncPlayerPreLoginEvent");
			
			this.pluginManager.registerEvents(new PlayerLoginListenerASync(this), this);
		}catch (ClassNotFoundException e){
			// TODO: Remove this with R4.1
			this.pluginManager.registerEvents(new PlayerLoginListener(this), this);
		}
		
		this.pluginManager.registerEvents(new PlayerBannedListener(this), this);
		
		if (this.config.getBoolean(MineBansConfig.BLOCK_PROXIES)){
			this.pluginManager.registerEvents(new PublicProxyListener(this), this);
		}
		
		if (this.config.getBoolean(MineBansConfig.BLOCK_COMPROMISED_ACCOUNTS)){
			this.pluginManager.registerEvents(new KnownCompromisedListener(this), this);
		}
		
		if (this.config.getBoolean(MineBansConfig.USE_GROUP_BANS)){
			this.pluginManager.registerEvents(new GroupBanListener(this), this);
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
			this.log.warn("The API failed to respond, checking for known problems...");
			
			this.api.lookupAPIStatusMessage(new APIResponseCallback(){
				
				public void onSuccess(String response){
					MineBans.this.log.warn("Status: " + response);
				}
				
				public void onFailure(Exception e){
					e.printStackTrace();
					
					MineBans.this.log.warn("We use Dropbox to provide the status announcements, for some reason it did not respond within 10 seconds.");
					MineBans.this.log.warn("Status: Unable to get info, check your server.log");
				}
				
			});
		}else{
			if (ping > 4000){
				this.log.warn("The API took longer than 4 seconds to reply.");
				this.log.warn("This is not a serious problem but players may experience longer than normal login times.");
			}
			
			this.log.info("The API responded in " + ping + "ms");
		}
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
