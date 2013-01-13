package com.minebans.minebans.api.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minebans.minebans.bans.BanReason;

public class OpenAppealsData extends APIData {
	
	public class AppealData {
		
		private String player_name;
		private Integer ban_reason_id;
		private BanReason ban_reason;
		
		public String getPlayerName(){
			return this.player_name;
		}
		
		public BanReason getBanReason(){
			return this.ban_reason;
		}
		
	}
	
	private ArrayList<AppealData> appeals;
	
	private OpenAppealsData(){
		this.appeals = new ArrayList<AppealData>();
	}
	
	public static OpenAppealsData fromString(String response){
		OpenAppealsData appealsData = new OpenAppealsData();
		
		JsonObject object = parser.parse(response).getAsJsonObject();
		
		for (Entry<String, JsonElement> entry : object.get("disputes").getAsJsonObject().entrySet()){
			AppealData data = gson.fromJson(entry.getValue(), AppealData.class);
			
			data.ban_reason = BanReason.getFromID(data.ban_reason_id);
			
			appealsData.appeals.add(data);
		}
		
		return appealsData;
	}
	
	public List<AppealData> getAppeals(){
		return this.appeals;
	}
	
}
