package com.minebans.pluginInterfaces;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;

import cc.co.evenprime.bukkit.nocheat.NoCheat;
import cc.co.evenprime.bukkit.nocheat.config.ConfPaths;
import cc.co.evenprime.bukkit.nocheat.config.NoCheatConfiguration;

import com.minebans.MineBans;
import com.minebans.MineBansConfig;
import com.minebans.bans.BanReason;

public class NoCheatPluginInterface extends ExploitPluginInterface {
	
	private MineBans plugin;
	private NoCheat nocheat;
	private NoCheatDataCache data;
	private HashMap<Integer, List<String>> reasonKeyMap;
	
	public NoCheatPluginInterface(MineBans plugin){
		this.plugin = plugin;
		this.nocheat = (NoCheat) plugin.pluginManager.getPlugin("NoCheat");
		this.data = new NoCheatDataCache(plugin, this.nocheat);
		this.reasonKeyMap = new HashMap<Integer, List<String>>();
		
		plugin.scheduler.scheduleSyncRepeatingTask(plugin, this.data, 1200, 1200);
		
		this.reasonKeyMap.put(BanReason.FLY.getID(), Arrays.asList("moving.flying"));
		this.reasonKeyMap.put(BanReason.SPEED.getID(), Arrays.asList("moving.running", "moving.morepackets", "moving.sneaking", "moving.swimming"));
		this.reasonKeyMap.put(BanReason.BLOCK_REACH.getID(), Arrays.asList("blockbreak.reach", "blockplace.reach", "blockbreak.direction", "blockplace.direction"));
		this.reasonKeyMap.put(BanReason.NOFALL.getID(), Arrays.asList("moving.nofal"));
		this.reasonKeyMap.put(BanReason.NOSWING.getID(), Arrays.asList("fight.noswing", "blockbreak.noswing"));
		this.reasonKeyMap.put(BanReason.PVP_CHEAT.getID(), Arrays.asList("fight.noswing", "fight.reach", "fight.direction", "fight.speed", "inventory.instantbow", "fight.godmode"));
		this.reasonKeyMap.put(BanReason.ITEM_DROP.getID(), Arrays.asList("inventory.drop"));
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
				if (noCheatConfig.getBoolean(ConfPaths.MOVING_RUNFLY_CHECK) == false){
					plugin.log.warn("To provide the best data NoCheat should to be set to check running/flying for all worlds.");
				}
				
				if (noCheatConfig.getBoolean(ConfPaths.MOVING_MOREPACKETS_CHECK) == false){
					plugin.log.warn("To provide the best data NoCheat should be set to check for the player sending more move packets than normal in all worlds.");
				}
				
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.NOFALL))){
					if (noCheatConfig.getBoolean(ConfPaths.MOVING_RUNFLY_CHECKNOFALL) == false){
						plugin.log.warn("To provide the best data NoCheat should be set to check for nofall in all worlds.");
					}
				}
				
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.FLY))){
					if (noCheatConfig.getBoolean(ConfPaths.MOVING_RUNFLY_FLYING_ALLOWALWAYS)){
						plugin.log.warn("To provide the best data NoCheat should to be set to disallow flying.");
					}
					
					if (noCheatConfig.getBoolean(ConfPaths.MOVING_RUNFLY_FLYING_ALLOWINCREATIVE) == false){
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
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.NOFALL))){
				if (noCheatConfig.getBoolean(ConfPaths.BLOCKBREAK_NOSWING_CHECK) == false){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for block no-swing hacks in all worlds.");
				}
				
				if (noCheatConfig.getBoolean(ConfPaths.FIGHT_NOSWING_CHECK) == false){
					plugin.log.warn("To provide the best data NoCheat should to be set to check for PVP no-swing hacks in all worlds.");
				}
			}
			
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.PVP_CHEAT)) || plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.BLOCK_REACH))){
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.BLOCK_REACH))){
					if (noCheatConfig.getBoolean(ConfPaths.BLOCKBREAK_DIRECTION_CHECK) == false || noCheatConfig.getBoolean(ConfPaths.BLOCKBREAK_REACH_CHECK)){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for block break reach and direction in all worlds.");
					}
				}
				
				if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(BanReason.PVP_CHEAT))){
					if (noCheatConfig.getBoolean(ConfPaths.FIGHT_DIRECTION_CHECK) == false){
						plugin.log.warn("To provide the best data NoCheat should to be set to check PVP directions for all worlds.");
					}
					
					if (noCheatConfig.getBoolean(ConfPaths.FIGHT_REACH_CHECK) == false){
						plugin.log.warn("To provide the best data NoCheat should to be set to check PVP reach for all worlds.");
					}
					
					if (noCheatConfig.getBoolean(ConfPaths.FIGHT_NOSWING_CHECK) == false){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for PVP no-swing hacks in all worlds.");
					}
					
					if (noCheatConfig.getBoolean(ConfPaths.FIGHT_SPEED_CHECK) == false){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for PVP speed hacks in all worlds.");
					}
					
					if (noCheatConfig.getBoolean(ConfPaths.INVENTORY_INSTANTBOW_CHECK) == false){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for instant bow hacks in all worlds.");
					}
					
					if (noCheatConfig.getBoolean(ConfPaths.INVENTORY_INSTANTEAT_CHECK) == false){
						plugin.log.warn("To provide the best data NoCheat should to be set to check for instant eat hacks in all worlds.");
					}
					
					if (noCheatConfig.getBoolean(ConfPaths.FIGHT_GODMODE_CHECK) == false){
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
				if (noCheatConfig.getBoolean(ConfPaths.INVENTORY_DROP_CHECK) == false){
					plugin.log.warn("To provide the best data NoCheat should to be set to check player inventory drops for all worlds.");
				}
				
				if (noCheatConfig.getInt(ConfPaths.INVENTORY_DROP_TIMEFRAME) < 20){
					plugin.log.fatal("NoCheat must to be set to use an inventory drop time no less than 400.");
					return false;
				}
				
				if (noCheatConfig.getInt(ConfPaths.INVENTORY_DROP_LIMIT) < 100){
					plugin.log.fatal("NoCheat must to be set to use an inventory drop limit no less than 15.");
					return false;
				}
			}
		}
		
		return true;
	}
	
	public long getMaxViolationLevel(String playerName, BanReason reason){
		long current;
		long max = 0L;
		
		for (String key : this.reasonKeyMap.get(reason.getID())){
			current = this.data.getMaxViolationLevel(playerName, key);
			
			if (current > max){
				max = current;
			}
		}
		
		return max;
	}
	
}
