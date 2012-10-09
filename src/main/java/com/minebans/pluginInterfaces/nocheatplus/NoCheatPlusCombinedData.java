package com.minebans.pluginInterfaces.nocheatplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.minebans.bans.BanReason;

import fr.neatmonster.nocheatplus.checks.blockbreak.BlockBreakData;
import fr.neatmonster.nocheatplus.checks.blockinteract.BlockInteractData;
import fr.neatmonster.nocheatplus.checks.blockplace.BlockPlaceData;
import fr.neatmonster.nocheatplus.checks.fight.FightData;
import fr.neatmonster.nocheatplus.checks.inventory.InventoryData;
import fr.neatmonster.nocheatplus.checks.moving.MovingData;

public class NoCheatPlusCombinedData {
	
	private HashMap<BanReason, ArrayList<Double>> data;
	
	public NoCheatPlusCombinedData(Player player){
		BlockBreakData blockBreakData = BlockBreakData.getData(player);
		BlockInteractData blockInteractdata = BlockInteractData.getData(player);
		BlockPlaceData blockPlaceData = BlockPlaceData.getData(player);
		FightData fightData = FightData.getData(player);
		InventoryData inventoryData = InventoryData.getData(player);
		MovingData movingData = MovingData.getData(player);
		
		ArrayList<Double> flyData = new ArrayList<Double>();
		ArrayList<Double> speedData = new ArrayList<Double>();
		ArrayList<Double> reachData = new ArrayList<Double>();
		ArrayList<Double> nofallData = new ArrayList<Double>();
		ArrayList<Double> noswingData = new ArrayList<Double>();
		ArrayList<Double> pvpData = new ArrayList<Double>();
		ArrayList<Double> dropData = new ArrayList<Double>();
		
		if (blockBreakData != null){
			reachData.add(blockBreakData.directionVL);
			reachData.add(blockBreakData.reachVL);
			
			noswingData.add(blockBreakData.noSwingVL);
		}
		
		if (blockInteractdata != null){
			reachData.add(blockInteractdata.directionVL);
			reachData.add(blockInteractdata.reachVL);
		}
		
		if (blockPlaceData != null){
			reachData.add(blockPlaceData.directionVL);
			reachData.add(blockPlaceData.reachVL);
			
			noswingData.add(blockPlaceData.noSwingVL);
		}
		
		if (fightData != null){
			pvpData.add(fightData.angleVL);
			pvpData.add(fightData.criticalVL);
			pvpData.add(fightData.directionVL);
			pvpData.add(fightData.godModeVL);
			pvpData.add(fightData.knockbackVL);
			pvpData.add(fightData.noSwingVL);
			pvpData.add(fightData.reachVL);
			pvpData.add(fightData.speedVL);
			
			noswingData.add(fightData.noSwingVL);
		}
		
		if (inventoryData != null){
			pvpData.add(inventoryData.instantBowVL);
			
			dropData.add(inventoryData.dropVL);
		}
		
		if (movingData != null){
			flyData.add(movingData.survivalFlyVL);
			
			speedData.add(movingData.morePacketsVL);
			speedData.add(movingData.morePacketsVehicleVL);
			speedData.add(movingData.survivalFlyVL);
			speedData.add(movingData.creativeFlyVL);
			
			nofallData.add(movingData.noFallVL);
		}
		
		this.data = new HashMap<BanReason, ArrayList<Double>>();
		
		this.data.put(BanReason.FLY, flyData);
		this.data.put(BanReason.SPEED, speedData);
		this.data.put(BanReason.BLOCK_REACH, reachData);
		this.data.put(BanReason.NOFALL, nofallData);
		this.data.put(BanReason.NOSWING, noswingData);
		this.data.put(BanReason.PVP_CHEAT, pvpData);
		this.data.put(BanReason.ITEM_DROP, dropData);
	}
	
	public double getMaxforReason(BanReason reason){
		ArrayList<Double> values = this.data.get(reason);
		
		return (!values.isEmpty()) ? Collections.max(values) : 0D;
	}
	
}