package com.minebans.pluginInterfaces.nocheatplus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.minebans.bans.BanReason;

import fr.neatmonster.nocheatplus.checks.blockbreak.BlockBreakData;
import fr.neatmonster.nocheatplus.checks.blockinteract.BlockInteractData;
import fr.neatmonster.nocheatplus.checks.blockplace.BlockPlaceData;
import fr.neatmonster.nocheatplus.checks.fight.FightData;
import fr.neatmonster.nocheatplus.checks.inventory.InventoryData;
import fr.neatmonster.nocheatplus.checks.moving.MovingData;

public class NoCheatPlusCombinedData {
	
	private HashMap<BanReason, List<Double>> data;
	
	public NoCheatPlusCombinedData(Player player){
		BlockBreakData blockBreakData = BlockBreakData.getData(player);
		BlockInteractData blockInteractdata = BlockInteractData.getData(player);
		BlockPlaceData blockPlaceData = BlockPlaceData.getData(player);
		FightData fightData = FightData.getData(player);
		InventoryData inventoryData = InventoryData.getData(player);
		MovingData movingData = MovingData.getData(player);
		
		this.data.put(BanReason.FLY, Arrays.asList(movingData.survivalFlyVL));
		this.data.put(BanReason.SPEED, Arrays.asList(movingData.morePacketsVL, movingData.morePacketsVehicleVL, movingData.survivalFlyVL, movingData.creativeFlyVL));
		this.data.put(BanReason.BLOCK_REACH, Arrays.asList(blockBreakData.directionVL, blockBreakData.reachVL, blockPlaceData.directionVL, blockPlaceData.reachVL, blockInteractdata.directionVL, blockInteractdata.reachVL));
		this.data.put(BanReason.NOFALL, Arrays.asList(movingData.noFallVL));
		this.data.put(BanReason.NOSWING, Arrays.asList(blockBreakData.noSwingVL, blockPlaceData.noSwingVL, fightData.noSwingVL, blockInteractdata.noSwingVL));
		this.data.put(BanReason.PVP_CHEAT, Arrays.asList(fightData.angleVL, fightData.criticalVL, fightData.directionVL, fightData.godModeVL, fightData.instantHealVL, fightData.knockbackVL, fightData.noSwingVL, fightData.reachVL, fightData.speedVL, inventoryData.instantBowVL));
		this.data.put(BanReason.ITEM_DROP, Arrays.asList(inventoryData.dropVL));
	}
	
	public Double getMaxforReason(BanReason reason){
		return Collections.max(this.data.get(reason));
	}
	
}
