package com.minebans.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import com.minebans.MineBansConfig;

public class PluginConfig {
	
	private YamlConfiguration config;
	private LinkedHashMap<String, Object> configDefaults;
	
	public PluginConfig(File configFile, PluginLogger log){
		this.config = new YamlConfiguration();
		this.configDefaults = MineBansConfig.getAll();
		
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
				log.info("The " + configFile.getName() + " file has been updated.");
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public boolean containsKey(PluginConfigKey configKey){
		return this.configDefaults.containsKey(configKey.getKey());
	}
	
	public int getInt(PluginConfigKey configKey){
		if (this.configDefaults.containsKey(configKey.getKey()) == false){
			return 0;
		}
		
		return this.config.getInt(configKey.getKey(), (Integer) this.configDefaults.get(configKey.getKey()));
	}
	
	public long getLong(PluginConfigKey configKey){
		if (this.configDefaults.containsKey(configKey.getKey()) == false){
			return 0L;
		}
		
		return this.config.getLong(configKey.getKey(), new Long((Integer) this.configDefaults.get(configKey.getKey())));
	}
	
	public boolean getBoolean(PluginConfigKey configKey){
		if (this.configDefaults.containsKey(configKey.getKey()) == false){
			return false;
		}
		
		return this.config.getBoolean(configKey.getKey(), (Boolean) this.configDefaults.get(configKey.getKey()));
	}
	
	public String getString(PluginConfigKey configKey){
		if (this.configDefaults.containsKey(configKey.getKey()) == false){
			return "";
		}
		
		return this.config.getString(configKey.getKey(), (String) this.configDefaults.get(configKey.getKey()));
	}
	
}