package com.minebans.minebans.api.data;

import java.util.HashMap;
import java.util.Set;

import uk.co.jacekk.bukkit.baseplugin.v7.util.ListUtils;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.minebans.minebans.bans.BanReason;
import com.minebans.minebans.bans.BanSeverity;

// TODO: Create ResponseData objects for each of these
// TODO: Create tests to check the parsing of API responses
public class PlayerBansData {
	
	private HashMap<String, Long> summary;
	private HashMap<BanReason, HashMap<BanSeverity, Long>> bans;
	
	public PlayerBansData(StringMap<?> response){
		this.summary = new HashMap<String, Long>();
		this.bans = new HashMap<BanReason, HashMap<BanSeverity, Long>>();
		
		StringMap<?> playerInfo = (StringMap<?>) response.get("player_info");
		StringMap<?> totalBans = (StringMap<?>) playerInfo.get("total_bans");
		
		for (Object banReasonId : totalBans.keySet()){
			StringMap<Long> banReasonInfo = (StringMap<Long>) totalBans.get(banReasonId);
			HashMap<BanSeverity, Long> banReasonData = new HashMap<BanSeverity, Long>();
			
			Long low = (banReasonInfo.containsKey(BanSeverity.LOW.getID().toString())) ? banReasonInfo.get(BanSeverity.LOW.getID().toString()) : 0L;
			Long medium = (banReasonInfo.containsKey(BanSeverity.MEDIUM.getID().toString())) ? banReasonInfo.get(BanSeverity.MEDIUM.getID().toString()) : 0L;
			Long high = (banReasonInfo.containsKey(BanSeverity.HIGH.getID().toString())) ? banReasonInfo.get(BanSeverity.HIGH.getID().toString()) : 0L;
			
			Long unconfirmed = (banReasonInfo.containsKey(BanSeverity.UNCONFIRMED.getID().toString())) ? (Long)banReasonInfo.get(BanSeverity.UNCONFIRMED.getID().toString()) : 0L;
			Long confirmed = low + medium + high;
			
			banReasonData.put(BanSeverity.TOTAL, confirmed + unconfirmed);
			banReasonData.put(BanSeverity.CONFIRMED, confirmed);
			banReasonData.put(BanSeverity.UNCONFIRMED, unconfirmed);
			banReasonData.put(BanSeverity.LOW, low);
			banReasonData.put(BanSeverity.MEDIUM, medium);
			banReasonData.put(BanSeverity.HIGH, high);
			
			this.bans.put(BanReason.getFromID(Integer.parseInt(banReasonId.toString())), banReasonData);
		}
		
		if (playerInfo.containsKey("ban_summary")){
			StringMap<Long> banSummary = (StringMap<Long>) playerInfo.get("ban_summary");
			
			this.summary.put("total", (banSummary.containsKey("total")) ? banSummary.get("total") : 0L);
			this.summary.put("last_24", (banSummary.containsKey("last_24")) ? banSummary.get("last_24") : 0L);
			this.summary.put("removed", (banSummary.containsKey("removed")) ? banSummary.get("removed") : 0L);
			this.summary.put("group_bans", (banSummary.containsKey("group_bans")) ? banSummary.get("group_bans") : 0L);
		}else{
			this.summary.put("total", 0L);
			this.summary.put("last_24", 0L);
			this.summary.put("removed", 0L);
			this.summary.put("group_bans", 0L);
		}
	}
	
	public PlayerBansData(String response){
		this((new Gson()).fromJson(response, StringMap.class));
	}
	
	/**
	 * @return	The data used to make up the join info for players.
	 */
	public HashMap<String, Long> getSummary(){
		return this.summary;
	}
	
	/**
	 * @return The total number of bans a player has
	 */
	public Long getTotal(){
		return this.summary.get("total");
	}
	
	/**
	 * @return The number of bans a player has received in the last 24 hours.
	 */
	public Long getLast24(){
		return this.summary.get("last_24");
	}
	
	/**
	 * @return The numeber of bans that the player has had that have since been removed.
	 */
	public Long getRemoved(){
		return this.summary.get("removed");
	}
	
	/**
	 * @return The number of bans the player has from servers with the same owner as this one.
	 */
	public Long getTotalGroupBans(){
		return this.summary.get("group_bans");
	}
	
	/**
	 * @return The full ban data for the player, this is how many bans they have for each severity under each ban reason.
	 */
	public HashMap<BanReason, HashMap<BanSeverity, Long>> getBans(){
		return this.bans;
	}
	
	/**
	 * @return All of the bans reasons that the player has bans for.
	 */
	public Set<BanReason> getBanReasons(){
		return this.bans.keySet();
	}
	
	/**
	 * @return The number of different reasons that the player has been banned for.
	 */
	public Integer getTotalRulesBroken(){
		return this.bans.size();
	}
	
	/**
	 * Fetches the number of bans the player has for the specific reason and severity.
	 * 
	 * @param reason	The BanReason.
	 * @param severity	The BanSeverity.
	 * @return			The number of bans.
	 */
	public Long get(BanReason reason, BanSeverity severity){
		return this.bans.get(reason).get(severity);
	}
	
	/**
	 * Gets the number of bans the player has for the specific reason and severity.
	 * 
	 * @param reasonId	The ID of the BanReason.
	 * @param severity	The BanSeverity.
	 * @return			The number of bans.
	 */
	public Long get(Integer reasonId, BanSeverity severity){
		return this.bans.get(BanReason.getFromID(reasonId)).get(severity);
	}
	
	/**
	 * Gets the total number of bans that a player has for a specific reason.
	 * 
	 * @param reason	The BanReason.
	 * @return			The total number of bans.
	 */
	public Long getTotal(BanReason reason){
		return ListUtils.sumLongs(this.bans.get(reason).values());
	}
	
	/**
	 * Gets the total number of bans that a player has for a specific reason.
	 * 
	 * @param reasonId	The ID of the BanReason.
	 * @return			The total number of bans.
	 */
	public Long getTotal(Integer reasonId){
		return ListUtils.sumLongs(this.bans.get(BanReason.getFromID(reasonId)).values());
	}
	
	/**
	 * Gets the total number of bans that a player has for a specific severity.
	 * 
	 * @param severity	The BanSeverity.
	 * @return			The total number of bans.
	 */
	public Long getTotal(BanSeverity severity){
		Long total = 0L;
		
		for (HashMap<BanSeverity, Long> data : this.bans.values()){
			if (data.containsKey(severity)){
				total += data.get(severity);
			}
		}
		
		return total;
	}
	
}
