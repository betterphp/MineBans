package com.minebans.util;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;

import com.minebans.MineBans;

public class MineBansLogger {
	
	private MineBans plugin;
	private Logger logger;
	
	public MineBansLogger(MineBans plugin){
		this.plugin = plugin;
		this.logger = Logger.getLogger("Minecraft");
	}
	
	private String buildString(String msg){
		PluginDescriptionFile pdf = plugin.getDescription();
		return "[" + pdf.getName() + " v" + pdf.getVersion()+ "]: " + msg;
	}
	
	public void info(String msg){
		this.logger.info(this.buildString(msg));
	}
	
	public void warn(String msg){
		this.logger.warning(this.buildString(msg));
	}
	
	public void fatal(String msg){
		this.logger.severe(this.buildString(msg));
	}
	
}
