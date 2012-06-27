package com.minebans.pluginInterfaces.nocheatplus;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;
import com.minebans.bans.BanReason;
import com.minebans.pluginInterfaces.ExploitPluginInterface;

import fr.neatmonster.nocheatplus.NoCheatPlus;
import fr.neatmonster.nocheatplus.config.ConfPaths;
import fr.neatmonster.nocheatplus.players.informations.Statistics;

public class NoCheatPlusPluginInterface extends ExploitPluginInterface {
	
	private MineBans plugin;
	private NoCheatPlus noCheatPlus;
	private NoCheatPlusDataCache data;
	private HashMap<BanReason, List<Statistics.Id>> reasonKeyMap;
	
	public NoCheatPlusPluginInterface(MineBans plugin){
		this.plugin = plugin;
		this.noCheatPlus = (NoCheatPlus) plugin.pluginManager.getPlugin("NoCheatPlus");
		this.data = new NoCheatPlusDataCache(plugin);
		this.reasonKeyMap = new HashMap<BanReason, List<Statistics.Id>>();
		
		plugin.scheduler.scheduleSyncRepeatingTask(plugin, this.data, 1200, 1200);
		
		this.reasonKeyMap.put(BanReason.FLY, Arrays.asList(Statistics.Id.MOV_FLYING, Statistics.Id.MOV_BEDFLYING, Statistics.Id.MOV_WATERWALK));
		this.reasonKeyMap.put(BanReason.SPEED, Arrays.asList(Statistics.Id.MOV_RUNNING, Statistics.Id.MOV_MOREPACKETS, Statistics.Id.MOV_MOREPACKETSVEHICLE, Statistics.Id.MOV_SNEAKING, Statistics.Id.MOV_SWIMMING, Statistics.Id.MOV_COBWEB));
		this.reasonKeyMap.put(BanReason.BLOCK_REACH, Arrays.asList(Statistics.Id.BB_REACH, Statistics.Id.BP_REACH, Statistics.Id.BB_DIRECTION, Statistics.Id.BP_DIRECTION));
		this.reasonKeyMap.put(BanReason.NOFALL, Arrays.asList(Statistics.Id.MOV_NOFALL));
		this.reasonKeyMap.put(BanReason.NOSWING, Arrays.asList(Statistics.Id.FI_NOSWING, Statistics.Id.BB_NOSWING));
		this.reasonKeyMap.put(BanReason.PVP_CHEAT, Arrays.asList(Statistics.Id.FI_NOSWING, Statistics.Id.FI_REACH, Statistics.Id.FI_DIRECTION, Statistics.Id.FI_ANGLE, Statistics.Id.FI_GODMODE, Statistics.Id.INV_BOW, Statistics.Id.INV_EAT, Statistics.Id.FI_INSTANTHEAL, Statistics.Id.FI_CRITICAL, Statistics.Id.FI_KNOCKBACK, Statistics.Id.FI_SPEED));
		this.reasonKeyMap.put(BanReason.ITEM_DROP, Arrays.asList(Statistics.Id.INV_DROP));
	}
	
	public boolean pluginEnabled(){
		return (this.noCheatPlus != null);
	}
	
	public String getPluginName(){
		return "NoCheatPlus";
	}
	
	public boolean checkConfig(){
		YamlConfiguration config = new YamlConfiguration();
		
		try{
			config.load(new File(this.noCheatPlus.getDataFolder().getAbsoluteFile() + File.separator + "config.yml"));
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.FLY)) || plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.SPEED))){
				if (!config.getBoolean(ConfPaths.MOVING_RUNFLY_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should to be set to check running/flying.");
				}
				
				if (config.getBoolean(ConfPaths.MOVING_RUNFLY_FLYING_ALLOWALWAYS)){
					plugin.log.warn("To provide the best data NoCheatPlus should to be set not to allow flying.");
				}
				
				if (!config.getBoolean(ConfPaths.MOVING_RUNFLY_FLYING_ALLOWINCREATIVE)){
					plugin.log.warn("To provide the best data NoCheatPlus should to be set to allow flying in creative mode.");
				}
				
				if (config.getBoolean(ConfPaths.MOVING_RUNFLY_ALLOWFASTSNEAKING)){
					plugin.log.warn("To provide the best data NoCheatPlus should be set to block fast sneaking.");
				}
				
				if (config.getInt(ConfPaths.MOVING_RUNFLY_FLYING_SPEEDLIMITHORIZONTAL) < 60){
					plugin.log.fatal("NoCheatPlus must to be set to use a horizontal flying speed limit no less than 60.");
					return false;
				}
				
				if (config.getInt(ConfPaths.MOVING_RUNFLY_FLYING_SPEEDLIMITVERTICAL) < 100){
					plugin.log.fatal("NoCheatPlus must to be set to use a vertical flying speed limit no less than 100.");
					return false;
				}
				
				if (!config.getBoolean(ConfPaths.MOVING_MOREPACKETS_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should be set to check for the player sending more move packets than normal.");
				}
				
				if (!config.getBoolean(ConfPaths.MOVING_MOREPACKETSVEHICLE_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should be set to check for the player sending more move packets than normal when in vehicles.");
				}
			}
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.NOFALL))){
				if (!config.getBoolean(ConfPaths.MOVING_RUNFLY_NOFALL_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should be set to check for nofall.");
				}
			}
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.NOSWING))){
				if (!config.getBoolean(ConfPaths.BLOCKBREAK_NOSWING_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should to be set to check for block no-swing hacks.");
				}
				
				if (!config.getBoolean(ConfPaths.FIGHT_NOSWING_CHECK)){
					plugin.log.warn("To provide the best data NoCheatPlus should to be set to check for PVP no-swing hacks.");
				}
			}
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.BLOCK_REACH))){
				if (!config.getBoolean(ConfPaths.BLOCKBREAK_DIRECTION_CHECK) || !config.getBoolean(ConfPaths.BLOCKBREAK_REACH_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for block break reach and direction.");
				}
			}
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.PVP_CHEAT))){
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
				
				if (!config.getBoolean(ConfPaths.INVENTORY_INSTANTBOW_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for instant bow hacks.");
				}
				
				if (!config.getBoolean(ConfPaths.INVENTORY_INSTANTEAT_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for instant eat hacks.");
				}
				
				if (!config.getBoolean(ConfPaths.FIGHT_GODMODE_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for godmode hacks.");
				}
				
				if (config.getInt(ConfPaths.FIGHT_DIRECTION_PRECISION) < 75){
					plugin.log.fatal("NoCheat must to be set to use a fight direction precision no less than 75.");
					return false;
				}
				
				if (config.getInt(ConfPaths.FIGHT_REACH_LIMIT) < 400){
					plugin.log.fatal("NoCheat must to be set to use a fight reach limit no less than 400.");
					return false;
				}
				
				if (config.getInt(ConfPaths.FIGHT_SPEED_ATTACKLIMIT) < 15){
					plugin.log.fatal("NoCheat must to be set to use a fight speed limit no less than 15.");
					return false;
				}
			}
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.ITEM_DROP))){
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
	
	public long getMaxViolationLevel(String playerName, BanReason reason){
		long current;
		long max = 0L;
		
		for (Statistics.Id id : this.reasonKeyMap.get(reason)){
			current = this.data.getMaxViolationLevel(playerName, id.toString());
			
			if (current > max){
				max = current;
			}
		}
		
		return max;
	}
	
}