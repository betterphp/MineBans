package com.minebans.minebans;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;

import uk.co.jacekk.bukkit.baseplugin.v9_1.BasePlugin;
import uk.co.jacekk.bukkit.baseplugin.v9_1.config.PluginConfig;
import uk.co.jacekk.bukkit.baseplugin.v9_1.update.BukkitDevUpdateChecker;

import com.minebans.minebans.api.APIInterface;
import com.minebans.minebans.api.callback.StatusCallback;
import com.minebans.minebans.api.callback.StatusMessageCallback;
import com.minebans.minebans.api.data.StatusData;
import com.minebans.minebans.api.data.StatusMessageData;
import com.minebans.minebans.api.request.StatusMessageRequest;
import com.minebans.minebans.api.request.StatusRequest;
import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.commands.BanExecutor;
import com.minebans.minebans.commands.ExemptExecutor;
import com.minebans.minebans.commands.KickExecutor;
import com.minebans.minebans.commands.MineBansExecutor;
import com.minebans.minebans.joindatalisteners.GroupBanListener;
import com.minebans.minebans.joindatalisteners.KnownCompromisedListener;
import com.minebans.minebans.joindatalisteners.PlayerBannedListener;
import com.minebans.minebans.joindatalisteners.PublicProxyListener;
import com.minebans.minebans.joindatalisteners.TooManyBansListener;
import com.minebans.minebans.pluginInterfaces.AntiSpamInterface;
import com.minebans.minebans.pluginInterfaces.ExploitInterface;
import com.minebans.minebans.pluginInterfaces.LoggingInterface;
import com.minebans.minebans.pluginapi.MineBansPluginAPI;

public class MineBans extends BasePlugin {
	
	public static MineBans INSTANCE;
	public static final boolean DEBUG_MODE = true;
	
	public LoggingInterface loggingPlugin;
	public ExploitInterface exploitPlugin;
	public AntiSpamInterface antiSpamPlugin;
	
	public BanManager banManager;
	public EvidenceManager evidenceManager;
	
	public APIInterface api;
	public BukkitDevUpdateChecker updateChecker;
	
	public ArrayList<String> seenPlayers;
	public HashMap<String, ArrayList<String>> banCommands;
	
	// String IP, List<String> playerNames
	public HashMap<String, ArrayList<String>> playerIPs;
	public HashMap<String, ArrayList<String>> bannedIPs;
	
	public void onEnable(){
		super.onEnable(true);
		
		INSTANCE = this;
		
		if (DEBUG_MODE){
			this.log.warn("========================= WARNING ==========================");
			this.log.warn(" Debug mode active, do not use this on a production server!");
			this.log.warn("============================================================");
		}
		
		this.config = new PluginConfig(new File(this.baseDirPath + File.separator + "config.yml"), Config.class, this.log);
		
		if (!this.config.getBoolean(Config.BUNGEE_CORD_MODE) && !this.server.getOnlineMode()){
			this.log.warn("======================== WARNING ========================");
			this.log.warn(" Your server must have online-mode=true to use MineBans!");
			this.log.warn("=========================================================");
			this.setEnabled(false);
			return;
		}
		
		this.loggingPlugin = new LoggingInterface(this);
		this.exploitPlugin = new ExploitInterface(this);
		this.antiSpamPlugin = new AntiSpamInterface(this);
		
		this.banManager = new BanManager(this);
		this.evidenceManager = new EvidenceManager(this);
		
		this.api = new APIInterface(this);
		this.updateChecker = new BukkitDevUpdateChecker(this, "http://dev.bukkit.org/server-mods/minebans/files.rss");
		
		this.seenPlayers = new ArrayList<String>();
		this.banCommands = new HashMap<String, ArrayList<String>>();
		
		this.playerIPs = new HashMap<String, ArrayList<String>>();
		this.bannedIPs = new HashMap<String, ArrayList<String>>();
		
		this.pluginManager.registerEvents(new PlayerLoginListener(this), this);
		this.pluginManager.registerEvents(new PlayerJoinListener(this), this);
		this.pluginManager.registerEvents(new PlayerIPListener(this), this);
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
		
		this.permissionManager.registerPermissions(Permission.class);
		
		this.commandManager.registerCommandExecutor(new BanExecutor(this));
		this.commandManager.registerCommandExecutor(new KickExecutor(this));
		this.commandManager.registerCommandExecutor(new ExemptExecutor(this));
		this.commandManager.registerCommandExecutor(new MineBansExecutor(this));
		
		this.scheduler.runTaskLater(this, new Runnable(){
			
			public void run(){
				try{
					ServerListPingEvent testPing = new ServerListPingEvent(InetAddress.getLocalHost(), "MineBans Test", 0, 20);
					
					MineBans.this.pluginManager.callEvent(testPing);
					
					if (!testPing.getMotd().equals("MineBans Test")){
						MineBans.this.log.warn("============================= WARNING ==============================");
						MineBans.this.log.warn("   Another plugin has changed the MOTD, this may cause conflicts!");
						MineBans.this.log.warn(" If you get E10 from MineBans please try disabling any MOTD plugins.");
						MineBans.this.log.warn("====================================================================");
					}
				}catch (UnknownHostException e){
					e.printStackTrace();
				}
			}
			
		}, 20L);
		
		if (!this.config.getBoolean(Config.BUNGEE_CORD_MODE)){
			final long startTime = System.currentTimeMillis();
			
			(new StatusRequest(MineBans.this, "CONSOLE")).process(new StatusCallback(MineBans.this){
				
				@Override
				public void onSuccess(StatusData data){
					long ping = data.getResponceTime() - startTime;
					
					if (ping > 5000){
						MineBans.this.log.warn("The API took longer than 5 seconds to reply, players may experience slow logins.");
					}
					
					MineBans.this.log.info("The API responded in " + ping + "ms");
				}
				
				@Override
				public void onFailure(Exception exception){
					plugin.log.warn("The API failed to respond, checking for known problems...");
					
					(new StatusMessageRequest(MineBans.this)).process(new StatusMessageCallback(plugin){
						
						@Override
						public void onSuccess(StatusMessageData data){
							plugin.log.warn("Status: " + data.getMessage());
						}
						
						@Override
						public void onFailure(Exception exception){
							plugin.log.warn("We use Dropbox to provide the status announcements, for some reason it did not respond within 12 seconds.");
						}
						
					});
				}
				
			});
		}
	}
	
	public void onDisable(){
		if (this.api != null){
			this.api.stopThread();
		}
		
		INSTANCE = null;
		
		this.log.info("Disabled.");
	}
	
	public String getVersion(){
		return this.description.getVersion();
	}
	
	public MineBansPluginAPI getPluginAPI(Plugin plugin){
		return MineBansPluginAPI.getHandle(this, plugin);
	}
	
}
