package com.minebans.api;

import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.minebans.bans.BanReason;
import com.minebans.bans.BanSeverity;
import com.minebans.util.ListUtils;

public class PlayerBanData {
	
	private HashMap<String, Long> summary;
	private HashMap<BanReason, HashMap<BanSeverity, Long>> bans;
	
	public PlayerBanData(JSONObject response){
		this.summary = new HashMap<String, Long>();
		this.bans = new HashMap<BanReason, HashMap<BanSeverity, Long>>();
		
		JSONObject playerInfo = (JSONObject) response.get("player_info");
		JSONObject totalBans = (JSONObject) playerInfo.get("total_bans");
		
		JSONObject banReasonInfo;
		HashMap<BanSeverity, Long> banReasonData;
		
		Long confirmed, unconfirmed, low, medium, high;
		
		for (Object banReasonId : totalBans.keySet()){
			banReasonInfo = (JSONObject) totalBans.get(banReasonId);
			banReasonData = new HashMap<BanSeverity, Long>();
			
			low = (banReasonInfo.containsKey(BanSeverity.LOW.getID().toString())) ? (Long)banReasonInfo.get(BanSeverity.LOW.getID().toString()) : 0L;
			medium = (banReasonInfo.containsKey(BanSeverity.MEDIUM.getID().toString())) ? (Long)banReasonInfo.get(BanSeverity.MEDIUM.getID().toString()) : 0L;
			high = (banReasonInfo.containsKey(BanSeverity.HIGH.getID().toString())) ? (Long)banReasonInfo.get(BanSeverity.HIGH.getID().toString()) : 0L;
			
			unconfirmed = (banReasonInfo.containsKey(BanSeverity.UNCONFIRMED.getID().toString())) ? (Long)banReasonInfo.get(BanSeverity.UNCONFIRMED.getID().toString()) : 0L;
			confirmed = low + medium + high;
			
			banReasonData.put(BanSeverity.TOTAL, confirmed + unconfirmed);
			banReasonData.put(BanSeverity.CONFIRMED, confirmed);
			banReasonData.put(BanSeverity.UNCONFIRMED, unconfirmed);
			banReasonData.put(BanSeverity.LOW, low);
			banReasonData.put(BanSeverity.MEDIUM, medium);
			banReasonData.put(BanSeverity.HIGH, high);
			
			this.bans.put(BanReason.getFromID(Integer.parseInt(banReasonId.toString())), banReasonData);
		}
		
		if (playerInfo.containsKey("ban_summary")){
			JSONObject banSummary = (JSONObject) playerInfo.get("ban_summary");
			
			this.summary.put("total", (banSummary.containsKey("total")) ? (Long) banSummary.get("total") : 0L);
			this.summary.put("last_24", (banSummary.containsKey("last_24")) ? (Long) banSummary.get("last_24") : 0L);
			this.summary.put("removed", (banSummary.containsKey("removed")) ? (Long) banSummary.get("removed") : 0L);
		}else{
			this.summary.put("total", 0L);
			this.summary.put("last_24", 0L);
			this.summary.put("removed", 0L);
		}
	}
	
	public PlayerBanData(String response) throws ParseException {
		this((JSONObject) (new JSONParser()).parse(response));
	}
	
	public HashMap<String, Long> getSummary(){
		return this.summary;
	}
	
	public Long getTotal(){
		return this.summary.get("total");
	}
	
	public Long getLast24(){
		return this.summary.get("last_24");
	}
	
	public Long getRemoved(){
		return this.summary.get("removed");
	}
	
	public HashMap<BanReason, HashMap<BanSeverity, Long>> getBans(){
		return this.bans;
	}
	
	public Set<BanReason> getBanReasons(){
		return this.bans.keySet();
	}
	
	public Integer getTotalRulesBroken(){
		return this.bans.size();
	}
	
	public Long get(BanReason reason, BanSeverity severity){
		return this.bans.get(reason).get(severity);
	}
	
	public Long get(Integer reasonId, BanSeverity severity){
		return this.bans.get(BanReason.getFromID(reasonId)).get(severity);
	}
	
	public Long getTotal(Integer reasonId){
		return ListUtils.sumLongs(this.bans.get(BanReason.getFromID(reasonId)).values());
	}
	
	public Long getTotal(BanReason reason){
		return ListUtils.sumLongs(this.bans.get(reason).values());
	}
	
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
