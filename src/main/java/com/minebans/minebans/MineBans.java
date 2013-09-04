package com.minebans.minebans;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;

import uk.co.jacekk.bukkit.baseplugin.BasePlugin;
import uk.co.jacekk.bukkit.baseplugin.command.CommandRegistrationException;
import uk.co.jacekk.bukkit.baseplugin.config.PluginConfig;
import uk.co.jacekk.bukkit.baseplugin.update.BukkitDevUpdateChecker;

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
	public static final boolean DEBUG_MODE = false;
	
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
	
	@Override
	public void onEnable(){
		super.onEnable(true);
		
		INSTANCE = this;
		
		if (DEBUG_MODE){
			this.log.warn("========================= WARNING ==========================");
			this.log.warn(" Debug mode active, do not use this on a production server!");
			this.log.warn("============================================================");
		}
		
		this.config = new PluginConfig(new File(this.baseDirPath + File.separator + "config.yml"), Config.class, this.log);
		
		if (!this.config.getBoolean(Config.BUNGEE_CORD_MODE_ENABLED) && !this.getServer().getOnlineMode()){
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
		
		this.getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerIPListener(this), this);
		this.getServer().getPluginManager().registerEvents(new RequestVerificationListener(this), this);
		
		this.getServer().getPluginManager().registerEvents(new PlayerBannedListener(this), this);
		
		if (this.config.getBoolean(Config.PROXY_BLOCK) || this.config.getBoolean(Config.PROXY_NOTIFY)){
			this.getServer().getPluginManager().registerEvents(new PublicProxyListener(this), this);
		}
		
		if (this.config.getBoolean(Config.BLOCK_COMPROMISED_ACCOUNTS)){
			this.getServer().getPluginManager().registerEvents(new KnownCompromisedListener(this), this);
		}
		
		if (this.config.getBoolean(Config.USE_GROUP_BANS)){
			this.getServer().getPluginManager().registerEvents(new GroupBanListener(this), this);
		}
		
		for (BanReason banReason : BanReason.getAll()){
			if (this.config.getBoolean(Config.getReasonEnabled(banReason))){
				this.getServer().getPluginManager().registerEvents(new TooManyBansListener(this), this);
				break;
			}
		}
		
		this.getPermissionManager().registerPermissions(Permission.class);
		
		try{
			this.getCommandManager().registerCommandExecutor(new BanExecutor(this));
			this.getCommandManager().registerCommandExecutor(new KickExecutor(this));
			this.getCommandManager().registerCommandExecutor(new ExemptExecutor(this));
			this.getCommandManager().registerCommandExecutor(new MineBansExecutor(this));
		}catch (CommandRegistrationException e){
			this.log.fatal(e.getMessage());
			this.log.fatal("Some commands may not work, check for plugins with conflicting commands");
		}
		
		this.getServer().getScheduler().runTaskLater(this, new Runnable(){
			
			public void run(){
				try{
					ServerListPingEvent testPing = new ServerListPingEvent(InetAddress.getLocalHost(), "MineBans Test", 0, 20);
					
					MineBans.this.getServer().getPluginManager().callEvent(testPing);
					
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
		
		final long startTime = System.currentTimeMillis();
		
		(new StatusRequest(MineBans.this, "CONSOLE")).process(new StatusCallback(this){
			
			@Override
			public void onSuccess(StatusData data){
				long ping = data.getResponceTime() - startTime;
				
				if (ping > 5000){
					plugin.log.warn("The API took longer than 5 seconds to reply, players may experience slow logins.");
				}
				
				plugin.log.info("The API responded in " + ping + "ms");
			}
			
			@Override
			public void onFailure(Exception exception){
				plugin.log.warn("The API failed to respond: " + exception.getMessage());
				plugin.log.warn("Checking for known problems...");
				
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
	
	@Override
	public void onDisable(){
		if (this.api != null){
			this.api.stopThread();
		}
		
		INSTANCE = null;
	}
	
	public String getVersion(){
		return this.description.getVersion();
	}
	
	public MineBansPluginAPI getPluginAPI(Plugin plugin){
		return MineBansPluginAPI.getHandle(this, plugin);
	}
	
}
