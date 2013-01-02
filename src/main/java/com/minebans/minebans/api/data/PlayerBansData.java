package com.minebans.minebans.api.data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import uk.co.jacekk.bukkit.baseplugin.v7.util.ListUtils;

import com.google.gson.JsonObject;
import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.bans.BanSeverity;

public class PlayerBansData extends APIData {
	
	private class BanSummaryData {
		
		private long total = 0L;
		private long last_24 = 0L;
		private long removed = 0L;
		private long group_bans = 0L;
		
	}
	
	private class TotalBansData {
		
		private HashMap<Integer, HashMap<Integer, Long>> total_bans;
		
	}
	
	private BanSummaryData summary;
	private HashMap<BanReason, HashMap<BanSeverity, Long>> totalBans;
	
	private PlayerBansData(){
		this.summary = new BanSummaryData();
		this.totalBans = new HashMap<BanReason, HashMap<BanSeverity, Long>>();
	}
	
	public static PlayerBansData fromString(String response){
		PlayerBansData data = new PlayerBansData();
		
		JsonObject object = parser.parse(response).getAsJsonObject();
		JsonObject playerInfo = object.get("player_info").getAsJsonObject();
		
		data.summary = gson.fromJson(playerInfo.get("ban_summary"), BanSummaryData.class);
		
		TotalBansData totalData = gson.fromJson(playerInfo, TotalBansData.class);
		
		for (Entry<Integer, HashMap<Integer, Long>> entry : totalData.total_bans.entrySet()){
			HashMap<Integer, Long> totals = entry.getValue();
			HashMap<BanSeverity, Long> banTotals = new HashMap<BanSeverity, Long>(); 
			
			Long low = (totals.containsKey(BanSeverity.LOW.getID())) ? totals.get(BanSeverity.LOW.getID()) : 0L;
			Long medium = (totals.containsKey(BanSeverity.MEDIUM.getID())) ? totals.get(BanSeverity.MEDIUM.getID()) : 0L;
			Long high = (totals.containsKey(BanSeverity.HIGH.getID())) ? totals.get(BanSeverity.HIGH.getID()) : 0L;
			Long unconfirmed = (totals.containsKey(BanSeverity.UNCONFIRMED.getID())) ? totals.get(BanSeverity.UNCONFIRMED.getID()) : 0L;
			Long confirmed = low + medium + high;
			
			banTotals.put(BanSeverity.LOW, low);
			banTotals.put(BanSeverity.MEDIUM, medium);
			banTotals.put(BanSeverity.HIGH, high);
			banTotals.put(BanSeverity.UNCONFIRMED, unconfirmed);
			banTotals.put(BanSeverity.CONFIRMED, confirmed);
			
			data.totalBans.put(BanReason.getFromID(entry.getKey()), banTotals);
		}
		
		return data;
	}
	
	/**
	 * @return The total number of bans a player has
	 */
	public Long getTotal(){
		return this.summary.total;
	}
	
	/**
	 * @return The number of bans a player has received in the last 24 hours.
	 */
	public Long getLast24(){
		return this.summary.last_24;
	}
	
	/**
	 * @return The numeber of bans that the player has had that have since been removed.
	 */
	public Long getRemoved(){
		return this.summary.removed;
	}
	
	/**
	 * @return The number of bans the player has from servers with the same owner as this one.
	 */
	public Long getTotalGroupBans(){
		return this.summary.group_bans;
	}
	
	/**
	 * @return The full ban data for the player, this is how many bans they have for each severity under each ban reason.
	 */
	public HashMap<BanReason, HashMap<BanSeverity, Long>> getBans(){
		return this.totalBans;
	}
	
	/**
	 * @return All of the bans reasons that the player has bans for.
	 */
	public Set<BanReason> getBanReasons(){
		return this.totalBans.keySet();
	}
	
	/**
	 * @return The number of different reasons that the player has been banned for.
	 */
	public Integer getTotalRulesBroken(){
		return this.totalBans.size();
	}
	
	/**
	 * Fetches the number of bans the player has for the specific reason and severity.
	 * 
	 * @param reason	The BanReason.
	 * @param severity	The BanSeverity.
	 * @return			The number of bans.
	 */
	public Long get(BanReason reason, BanSeverity severity){
		HashMap<BanSeverity, Long> data = this.totalBans.get(reason);
		
		if (data == null || !data.containsKey(severity)){
			return 0L;
		}
		
		return data.get(severity);
	}
	
	/**
	 * Gets the number of bans the player has for the specific reason and severity.
	 * 
	 * @param reasonId	The ID of the BanReason.
	 * @param severity	The BanSeverity.
	 * @return			The number of bans.
	 */
	public Long get(Integer reasonId, BanSeverity severity){
		return this.get(BanReason.getFromID(reasonId), severity);
	}
	
	/**
	 * Gets the total number of bans that a player has for a specific reason.
	 * 
	 * @param reason	The BanReason.
	 * @return			The total number of bans.
	 */
	public Long getTotal(BanReason reason){
		if (!this.totalBans.containsKey(reason)){
			return 0L;
		}
		
		return ListUtils.sumLongs(this.totalBans.get(reason).values());
	}
	
	/**
	 * Gets the total number of bans that a player has for a specific reason.
	 * 
	 * @param reasonId	The ID of the BanReason.
	 * @return			The total number of bans.
	 */
	public Long getTotal(Integer reasonId){
		return getTotal(BanReason.getFromID(reasonId));
	}
	
	/**
	 * Gets the total number of bans that a player has for a specific severity.
	 * 
	 * @param severity	The BanSeverity.
	 * @return			The total number of bans.
	 */
	public Long getTotal(BanSeverity severity){
		Long total = 0L;
		
		for (HashMap<BanSeverity, Long> data : this.totalBans.values()){
			if (data.containsKey(severity)){
				total += data.get(severity);
			}
		}
		
		return total;
	}
	
}
