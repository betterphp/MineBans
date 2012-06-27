package com.minebans.pluginInterfaces.nocheat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;

import cc.co.evenprime.bukkit.nocheat.NoCheat;
import cc.co.evenprime.bukkit.nocheat.config.ConfPaths;
import cc.co.evenprime.bukkit.nocheat.config.NoCheatConfiguration;
import cc.co.evenprime.bukkit.nocheat.data.Statistics;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;
import com.minebans.bans.BanReason;
import com.minebans.pluginInterfaces.ExploitPluginInterface;

public class NoCheatPluginInterface extends ExploitPluginInterface {
	
	private MineBans plugin;
	private NoCheat nocheat;
	private NoCheatDataCache data;
	private HashMap<BanReason, List<Statistics.Id>> reasonKeyMap;
	
	public NoCheatPluginInterface(MineBans plugin){
		this.plugin = plugin;
		this.nocheat = (NoCheat) plugin.pluginManager.getPlugin("NoCheat");
		this.data = new NoCheatDataCache(plugin, this.nocheat);
		this.reasonKeyMap = new HashMap<BanReason, List<Statistics.Id>>();
		
		plugin.scheduler.scheduleSyncRepeatingTask(plugin, this.data, 1200, 1200);
		
		this.reasonKeyMap.put(BanReason.FLY, Arrays.asList(Statistics.Id.MOV_FLYING));
		this.reasonKeyMap.put(BanReason.SPEED, Arrays.asList(Statistics.Id.MOV_RUNNING, Statistics.Id.MOV_MOREPACKETS, Statistics.Id.MOV_SNEAKING, Statistics.Id.MOV_SWIMMING));
		this.reasonKeyMap.put(BanReason.BLOCK_REACH, Arrays.asList(Statistics.Id.BB_REACH, Statistics.Id.BP_REACH, Statistics.Id.BB_DIRECTION, Statistics.Id.BP_DIRECTION));
		this.reasonKeyMap.put(BanReason.NOFALL, Arrays.asList(Statistics.Id.MOV_NOFALL));
		this.reasonKeyMap.put(BanReason.NOSWING, Arrays.asList(Statistics.Id.FI_NOSWING, Statistics.Id.BB_NOSWING));
		this.reasonKeyMap.put(BanReason.PVP_CHEAT, Arrays.asList(Statistics.Id.FI_NOSWING, Statistics.Id.FI_REACH, Statistics.Id.FI_DIRECTION, Statistics.Id.FI_GODMODE, Statistics.Id.INV_BOW, Statistics.Id.INV_EAT, Statistics.Id.FI_SPEED));
		this.reasonKeyMap.put(BanReason.ITEM_DROP, Arrays.asList(Statistics.Id.INV_DROP));
	}
	
	public boolean pluginEnabled(){
		return (this.plugin != null);
	}
	
	public String getPluginName(){
		return "NoCheat";
	}
	
	public boolean checkConfig(){
		for (World world : plugin.server.getWorlds()){
			plugin.log.info("Checking NoCheat config for '" + world.getName() + "'");
			
			NoCheatConfiguration noCheatConfig = this.nocheat.getConfig(world).getConfiguration();
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.FLY)) || plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.SPEED)) || plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.NOFALL))){
				if (!noCheatConfig.getBoolean(ConfPaths.MOVING_RUNFLY_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check running/flying for all worlds.");
				}
				
				if (!noCheatConfig.getBoolean(ConfPaths.MOVING_MOREPACKETS_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should be set to check for the player sending more move packets than normal in all worlds.");
				}
				
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.NOFALL))){
					if (!noCheatConfig.getBoolean(ConfPaths.MOVING_RUNFLY_CHECKNOFALL)){
						plugin.log.warn("To provide the best data NoCheat should be set to check for nofall in all worlds.");
					}
				}
				
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.FLY))){
					if (noCheatConfig.getBoolean(ConfPaths.MOVING_RUNFLY_FLYING_ALLOWALWAYS)){
						plugin.log.warn("To provide the best data NoCheat should to be set to disallow flying.");
					}
					
					if (!noCheatConfig.getBoolean(ConfPaths.MOVING_RUNFLY_FLYING_ALLOWINCREATIVE)){
						plugin.log.warn("To provide the best data NoCheat should to be set to allow flying in creative mode.");
					}
					
					if (noCheatConfig.getInt(ConfPaths.MOVING_RUNFLY_FLYING_SPEEDLIMITHORIZONTAL) < 60){
						plugin.log.fatal("NoCheat must to be set to use a horizontal flying speed limit no less than 60.");
						return false;
					}
					
					if (noCheatConfig.getInt(ConfPaths.MOVING_RUNFLY_FLYING_SPEEDLIMITVERTICAL) < 100){
						plugin.log.fatal("NoCheat must to be set to use a vertical flying speed limit no less than 100.");
						return false;
					}
				}
			}
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.NOSWING))){
				if (!noCheatConfig.getBoolean(ConfPaths.BLOCKBREAK_NOSWING_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for block no-swing hacks in all worlds.");
				}
				
				if (!noCheatConfig.getBoolean(ConfPaths.FIGHT_NOSWING_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for PVP no-swing hacks in all worlds.");
				}
			}
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.PVP_CHEAT)) || plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.BLOCK_REACH))){
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.BLOCK_REACH))){
					if (!noCheatConfig.getBoolean(ConfPaths.BLOCKBREAK_DIRECTION_CHECK) || !noCheatConfig.getBoolean(ConfPaths.BLOCKBREAK_REACH_CHECK)){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for block break reach and direction in all worlds.");
					}
				}
				
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.PVP_CHEAT))){
					if (!noCheatConfig.getBoolean(ConfPaths.FIGHT_DIRECTION_CHECK)){
						plugin.log.warn("To provide the best data NoCheat should to be set to check PVP directions for all worlds.");
					}
					
					if (!noCheatConfig.getBoolean(ConfPaths.FIGHT_REACH_CHECK)){
						plugin.log.warn("To provide the best data NoCheat should to be set to check PVP reach for all worlds.");
					}
					
					if (!noCheatConfig.getBoolean(ConfPaths.FIGHT_NOSWING_CHECK)){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for PVP no-swing hacks in all worlds.");
					}
					
					if (!noCheatConfig.getBoolean(ConfPaths.FIGHT_SPEED_CHECK)){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for PVP speed hacks in all worlds.");
					}
					
					if (!noCheatConfig.getBoolean(ConfPaths.INVENTORY_INSTANTBOW_CHECK)){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for instant bow hacks in all worlds.");
					}
					
					if (!noCheatConfig.getBoolean(ConfPaths.INVENTORY_INSTANTEAT_CHECK)){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for instant eat hacks in all worlds.");
					}
					
					if (!noCheatConfig.getBoolean(ConfPaths.FIGHT_GODMODE_CHECK)){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for godmode hacks in all worlds.");
					}
					
					if (noCheatConfig.getInt(ConfPaths.FIGHT_DIRECTION_PRECISION) < 75){
						plugin.log.fatal("NoCheat must to be set to use a fight direction precision no less than 75.");
						return false;
					}
					
					if (noCheatConfig.getInt(ConfPaths.FIGHT_REACH_LIMIT) < 400){
						plugin.log.fatal("NoCheat must to be set to use a fight reach limit no less than 400.");
						return false;
					}
					
					if (noCheatConfig.getInt(ConfPaths.FIGHT_SPEED_ATTACKLIMIT) < 15){
						plugin.log.fatal("NoCheat must to be set to use a fight speed limit no less than 15.");
						return false;
					}
				}
			}
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.ITEM_DROP))){
				if (!noCheatConfig.getBoolean(ConfPaths.INVENTORY_DROP_CHECK)){
					plugin.log.warn("To provide the best data NoCheat should to be set to check player inventory drops for all worlds.");
				}
				
				if (noCheatConfig.getInt(ConfPaths.INVENTORY_DROP_TIMEFRAME) < 20){
					plugin.log.fatal("NoCheat must to be set to use an inventory drop time no less than 400.");
					return false;
				}
				
				if (noCheatConfig.getInt(ConfPaths.INVENTORY_DROP_LIMIT) < 100){
					plugin.log.fatal("NoCheat must to be set to use an inventory drop limit no less than 100.");
					return false;
				}
			}
		}
		
		return true;
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
