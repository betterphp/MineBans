package com.minebans.minebans.pluginInterfaces.nocheatplus;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.minebans.minebans.Config;
import com.minebans.minebans.MineBans;
import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.pluginInterfaces.ExploitPluginInterface;

import fr.neatmonster.nocheatplus.NoCheatPlus;
import fr.neatmonster.nocheatplus.config.ConfPaths;

public class NoCheatPlusPluginInterface extends ExploitPluginInterface {
	
	private MineBans plugin;
	private NoCheatPlus noCheatPlus;
	private NoCheatPlusDataCache data;
	
	public NoCheatPlusPluginInterface(MineBans plugin){
		this.plugin = plugin;
		this.noCheatPlus = (NoCheatPlus) plugin.pluginManager.getPlugin("NoCheatPlus");
		this.data = new NoCheatPlusDataCache(plugin);
		
		plugin.scheduler.scheduleSyncRepeatingTask(plugin, this.data, 1200L, 1200L);
	}
	
	@Override
	public boolean pluginEnabled(){
		return (this.noCheatPlus != null);
	}
	
	@Override
	public String getPluginName(){
		return "NoCheatPlus";
	}
	
	@Override
	public boolean checkConfig(){
		YamlConfiguration config = new YamlConfiguration();
		
		try{
			config.load(new File(this.noCheatPlus.getDataFolder().getAbsoluteFile() + File.separator + "config.yml"));
			
			if (plugin.config.getBoolean(Config.getReasonEnabled(BanReason.FLY)) || plugin.config.getBoolean(Config.getReasonEnabled(BanReason.SPEED))){
				if (!config.getBoolean(ConfPaths.MOVING_SURVIVALFLY_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should to be set to check flying.");
				}
				
				if (config.getBoolean(ConfPaths.MOVING_CREATIVEFLY_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should to be set to allow flying in creative mode.");
				}
				
				if (!config.getBoolean(ConfPaths.MOVING_MOREPACKETS_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should be set to check for the player sending more move packets than normal.");
				}
				
				if (!config.getBoolean(ConfPaths.MOVING_MOREPACKETSVEHICLE_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should be set to check for the player sending more move packets than normal when in vehicles.");
				}
			}
			
			if (plugin.config.getBoolean(Config.getReasonEnabled(BanReason.NOFALL))){
				if (!config.getBoolean(ConfPaths.MOVING_NOFALL_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should be set to check for nofall.");
				}
			}
			
			if (plugin.config.getBoolean(Config.getReasonEnabled(BanReason.NOSWING))){
				if (!config.getBoolean(ConfPaths.BLOCKBREAK_NOSWING_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should to be set to check for block no-swing hacks.");
				}
				
				if (!config.getBoolean(ConfPaths.FIGHT_NOSWING_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should to be set to check for PVP no-swing hacks.");
				}
			}
			
			if (plugin.config.getBoolean(Config.getReasonEnabled(BanReason.BLOCK_REACH))){
				if (!config.getBoolean(ConfPaths.BLOCKBREAK_DIRECTION_CHECK) || !config.getBoolean(ConfPaths.BLOCKBREAK_REACH_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for block break reach and direction.");
				}
			}
			
			if (plugin.config.getBoolean(Config.getReasonEnabled(BanReason.PVP_CHEAT))){
				if (!config.getBoolean(ConfPaths.FIGHT_DIRECTION_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check PVP directions.");
				}
				
				if (!config.getBoolean(ConfPaths.FIGHT_REACH_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check PVP reach.");
				}
				
				if (!config.getBoolean(ConfPaths.FIGHT_NOSWING_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for PVP no-swing hacks.");
				}
				
				if (!config.getBoolean(ConfPaths.FIGHT_SPEED_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for PVP speed hacks.");
				}
				
				if (!config.getBoolean(ConfPaths.FIGHT_KNOCKBACK_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for knockback hacks.");
				}
				
				if (!config.getBoolean(ConfPaths.INVENTORY_INSTANTBOW_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for instant bow hacks.");
				}
				
				if (!config.getBoolean(ConfPaths.INVENTORY_INSTANTEAT_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for instant eat hacks.");
				}
				
				if (!config.getBoolean(ConfPaths.FIGHT_GODMODE_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for godmode hacks.");
				}
			}
			
			if (plugin.config.getBoolean(Config.getReasonEnabled(BanReason.ITEM_DROP))){
				if (!config.getBoolean(ConfPaths.INVENTORY_DROP_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check player inventory drops.");
				}
				
				if (config.getInt(ConfPaths.INVENTORY_DROP_TIMEFRAME) < 20){
					plugin.log.fatal("NoCheat must to be set to use an inventory drop time no less than 400.");
					return false;
				}
				
				if (config.getInt(ConfPaths.INVENTORY_DROP_LIMIT) < 100){
					plugin.log.fatal("NoCheat must to be set to use an inventory drop limit no less than 100.");
					return false;
				}
			}
			
			return true;
		}catch (Exception e){
			return false;
		}
	}
	
	@Override
	public long getMaxViolationLevel(String playerName, BanReason reason){
		return Math.round(this.data.getMaxViolationLevel(playerName, reason));
	}
	
}