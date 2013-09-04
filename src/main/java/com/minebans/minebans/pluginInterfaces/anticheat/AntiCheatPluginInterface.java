package com.minebans.minebans.pluginInterfaces.anticheat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.h31ix.anticheat.api.AnticheatAPI;
import net.h31ix.anticheat.manage.CheckType;
import net.h31ix.anticheat.manage.User;

import com.minebans.minebans.MineBans;
import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.pluginInterfaces.ExploitPluginInterface;

public class AntiCheatPluginInterface extends ExploitPluginInterface {
	
	private MineBans plugin;
	
	private HashMap<BanReason, List<CheckType>> reasonKeyMap;
	
	public AntiCheatPluginInterface(MineBans plugin){
		this.plugin = plugin;
		
		this.reasonKeyMap = new HashMap<BanReason, List<CheckType>>();
		
		this.reasonKeyMap.put(BanReason.FLY, Arrays.asList(CheckType.ZOMBE_FLY, CheckType.FLY, CheckType.WATER_WALK, CheckType.SPIDER));
		this.reasonKeyMap.put(BanReason.SPEED, Arrays.asList(CheckType.SPRINT, CheckType.SPEED, CheckType.SNEAK));
		this.reasonKeyMap.put(BanReason.BLOCK_REACH, Arrays.asList(CheckType.LONG_REACH));
		this.reasonKeyMap.put(BanReason.NOFALL, Arrays.asList(CheckType.NOFALL));
		this.reasonKeyMap.put(BanReason.NOSWING, Arrays.asList(CheckType.NO_SWING));
		this.reasonKeyMap.put(BanReason.PVP_CHEAT, Arrays.asList(CheckType.FAST_BOW, CheckType.FAST_EAT, CheckType.FAST_HEAL, CheckType.FORCEFIELD));
		this.reasonKeyMap.put(BanReason.ITEM_DROP, Arrays.asList(CheckType.ITEM_SPAM, CheckType.FAST_INVENTORY));
	}
	
	@Override
	public boolean pluginEnabled(){
		return plugin.getServer().getPluginManager().isPluginEnabled(this.getPluginName());
	}
	
	@Override
	public String getPluginName(){
		return "AntiCheat";
	}
	
	@Override
	public boolean checkConfig(){
		return true;
	}
	
	@Override
	public long getMaxViolationLevel(String playerName, BanReason reason){
		User user = AnticheatAPI.getManager().getUserManager().getUser(playerName);
		
		if (user == null){
			return 0;
		}
		
		long current;
		long max = 0L;
		
		for (CheckType type : this.reasonKeyMap.get(reason)){
			current = type.getUses(playerName);
			
			if (current > max){
				max = current;
			}
		}
		
		return max;
	}
	
}
