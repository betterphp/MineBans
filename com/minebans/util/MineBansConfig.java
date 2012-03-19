package com.minebans.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import com.minebans.MineBans;

public class MineBansConfig {
	
	private YamlConfiguration config;
	private LinkedHashMap<String, Object> configDefaults;
	
	public MineBansConfig(File configFile, MineBans plugin){
		this.config = new YamlConfiguration();
		this.configDefaults = new LinkedHashMap<String, Object>();
		
		this.configDefaults.put("api-key", "change this to the one from your control panel");
		
		this.configDefaults.put("use-compact-join-info", false);
		
		this.configDefaults.put("block-public-proxies", true);
		this.configDefaults.put("block-known-compromised-accounts", true);
		
		this.configDefaults.put("max-bans.total.total", -1);
		this.configDefaults.put("max-bans.total.confirmed", 15);
		this.configDefaults.put("max-bans.total.unconfirmed", -1);
		this.configDefaults.put("max-bans.total.low", -1);
		this.configDefaults.put("max-bans.total.medium", -1);
		this.configDefaults.put("max-bans.total.high", 5);
		
		this.configDefaults.put("max-bans.theft.enabled", true);
		this.configDefaults.put("max-bans.theft.total", -1);
		this.configDefaults.put("max-bans.theft.confirmed", 10);
		this.configDefaults.put("max-bans.theft.unconfirmed", -1);
		this.configDefaults.put("max-bans.theft.low", -1);
		this.configDefaults.put("max-bans.theft.medium", 5);
		this.configDefaults.put("max-bans.theft.high", 5);
		
		this.configDefaults.put("max-bans.grief.enabled", true);
		this.configDefaults.put("max-bans.grief.total", -1);
		this.configDefaults.put("max-bans.grief.confirmed", 10);
		this.configDefaults.put("max-bans.grief.unconfirmed", -1);
		this.configDefaults.put("max-bans.grief.low", -1);
		this.configDefaults.put("max-bans.grief.medium", 5);
		this.configDefaults.put("max-bans.grief.high", 5);
		
		this.configDefaults.put("max-bans.x-ray.enabled", true);
		this.configDefaults.put("max-bans.x-ray.total", -1);
		this.configDefaults.put("max-bans.x-ray.confirmed", 10);
		this.configDefaults.put("max-bans.x-ray.unconfirmed", -1);
		this.configDefaults.put("max-bans.x-ray.low", -1);
		this.configDefaults.put("max-bans.x-ray.medium", 5);
		this.configDefaults.put("max-bans.x-ray.high", 5);
		
		this.configDefaults.put("max-bans.abuse.enabled", false);
		this.configDefaults.put("max-bans.abuse.total", -1);
		this.configDefaults.put("max-bans.abuse.confirmed", 10);
		this.configDefaults.put("max-bans.abuse.unconfirmed", -1);
		
		this.configDefaults.put("max-bans.advertising.enabled", false);
		this.configDefaults.put("max-bans.advertising.total", -1);
		this.configDefaults.put("max-bans.advertising.confirmed", 10);
		this.configDefaults.put("max-bans.advertising.unconfirmed", -1);
		
		this.configDefaults.put("max-bans.fly.enabled", true);
		this.configDefaults.put("max-bans.fly.total", -1);
		this.configDefaults.put("max-bans.fly.confirmed", 10);
		this.configDefaults.put("max-bans.fly.unconfirmed", -1);
		
		this.configDefaults.put("max-bans.movement-speed.enabled", true);
		this.configDefaults.put("max-bans.movement-speed.total", -1);
		this.configDefaults.put("max-bans.movement-speed.confirmed", 10);
		this.configDefaults.put("max-bans.movement-speed.unconfirmed", -1);
		
		this.configDefaults.put("max-bans.block-reach.enabled", true);
		this.configDefaults.put("max-bans.block-reach.total", -1);
		this.configDefaults.put("max-bans.block-reach.confirmed", 10);
		this.configDefaults.put("max-bans.block-reach.unconfirmed", -1);
		
		this.configDefaults.put("max-bans.nofall.enabled", true);
		this.configDefaults.put("max-bans.nofall.total", -1);
		this.configDefaults.put("max-bans.nofall.confirmed", 10);
		this.configDefaults.put("max-bans.nofall.unconfirmed", -1);
		
		this.configDefaults.put("max-bans.noswing.enabled", false);
		this.configDefaults.put("max-bans.noswing.total", -1);
		this.configDefaults.put("max-bans.noswing.confirmed", 10);
		this.configDefaults.put("max-bans.noswing.unconfirmed", -1);
		
		this.configDefaults.put("max-bans.pvp-cheats.enabled", false);
		this.configDefaults.put("max-bans.pvp-cheats.total", -1);
		this.configDefaults.put("max-bans.pvp-cheats.confirmed", 10);
		this.configDefaults.put("max-bans.pvp-cheats.unconfirmed", -1);
		
		this.configDefaults.put("max-bans.chat-spam.enabled", true);
		this.configDefaults.put("max-bans.chat-spam.total", -1);
		this.configDefaults.put("max-bans.chat-spam.confirmed", 10);
		this.configDefaults.put("max-bans.chat-spam.unconfirmed", -1);
		
		this.configDefaults.put("max-bans.item-drop.enabled", true);
		this.configDefaults.put("max-bans.item-drop.total", -1);
		this.configDefaults.put("max-bans.item-drop.confirmed", 10);
		this.configDefaults.put("max-bans.item-drop.unconfirmed", -1);
		
		if (configFile.exists()){
			try {
				this.config.load(configFile);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		boolean updateNeeded = false;
		
		for (String key : this.configDefaults.keySet()){
			if (this.config.contains(key) == false){
				this.config.set(key, this.configDefaults.get(key));
				
				updateNeeded = true;
			}
		}
		
		if (updateNeeded){
			try {
				this.config.save(configFile);
				plugin.log.info("The config.yml file has been updated.");
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public boolean containsKey(String key){
		return this.configDefaults.containsKey(key);
	}
	
	public int getInt(String key){
		if (this.configDefaults.containsKey(key) == false){
			return 0;
		}
		
		return this.config.getInt(key, (Integer) this.configDefaults.get(key));
	}
	
	public long getLong(String key){
		if (this.configDefaults.containsKey(key) == false){
			return 0L;
		}
		
		return this.config.getLong(key, new Long((Integer) this.configDefaults.get(key)));
	}
	
	public boolean getBooleansOr(String match){
		String[] parts = match.split("\\*");
		
		for (String key : this.configDefaults.keySet()){
			if (key.startsWith(parts[0]) && key.endsWith(parts[1])){
				if (this.getBoolean(key)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean getBoolean(String key){
		if (this.configDefaults.containsKey(key) == false){
			return false;
		}
		
		return this.config.getBoolean(key, (Boolean) this.configDefaults.get(key));
	}
	
	public String getString(String key){
		if (this.configDefaults.containsKey(key) == false){
			return "";
		}
		
		return this.config.getString(key, (String) this.configDefaults.get(key));
	}
	
}