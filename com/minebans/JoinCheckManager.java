package com.minebans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.minebans.bans.BanReason;
import com.minebans.joinactions.AppealUnbanAction;
import com.minebans.joinactions.BanDataJoinAction;
import com.minebans.joinactions.InfoDataJoinAction;
import com.minebans.joinchecks.BanDataJoinCheck;
import com.minebans.joinchecks.ConnectionDeniedReason;
import com.minebans.joinchecks.GloballyBannedCheck;
import com.minebans.joinchecks.GroupBanCheck;
import com.minebans.joinchecks.InfoDataJoinCheck;
import com.minebans.joinchecks.KnownCompromisedCheck;
import com.minebans.joinchecks.LocalJoinCheck;
import com.minebans.joinchecks.LocallyBannedCheck;
import com.minebans.joinchecks.PublicProxyCheck;
import com.minebans.joinchecks.TempBannedCheck;
import com.minebans.joinchecks.TooManyBansCheck;

public class JoinCheckManager {
	
	private HashMap<ConnectionDeniedReason, LocalJoinCheck> localChecksMap;
	private HashMap<ConnectionDeniedReason, InfoDataJoinCheck> infoDataChecksMap;
	private HashMap<ConnectionDeniedReason, BanDataJoinCheck> banDataChecksMap;
	
	private ArrayList<InfoDataJoinAction> infoDataActionList;
	private ArrayList<BanDataJoinAction> banDataActionList;
	
	public JoinCheckManager(MineBans plugin){
		this.localChecksMap = new HashMap<ConnectionDeniedReason, LocalJoinCheck>();
		this.infoDataChecksMap = new HashMap<ConnectionDeniedReason, InfoDataJoinCheck>();
		this.banDataChecksMap = new HashMap<ConnectionDeniedReason, BanDataJoinCheck>();
		
		this.infoDataActionList = new ArrayList<InfoDataJoinAction>();
		
		this.localChecksMap.put(ConnectionDeniedReason.GLOBALLY_BANNED, new GloballyBannedCheck(plugin));
		this.localChecksMap.put(ConnectionDeniedReason.LOCALLY_BANNED, new LocallyBannedCheck(plugin));
		this.localChecksMap.put(ConnectionDeniedReason.TEMP_BANNED, new TempBannedCheck(plugin));
		
		if (plugin.config.getBoolean(MineBansConfig.BLOCK_PROXIES)){
			this.localChecksMap.put(ConnectionDeniedReason.PUBLIC_PROXY, new PublicProxyCheck(plugin));
		}
		
		if (plugin.config.getBoolean(MineBansConfig.BLOCK_COMPROMISED_ACCOUNTS)){
			this.infoDataChecksMap.put(ConnectionDeniedReason.KNOWN_COMPROMISED, new KnownCompromisedCheck());
		}
		
		if (plugin.config.getBoolean(MineBansConfig.USE_GROUP_BANS)){
			this.banDataChecksMap.put(ConnectionDeniedReason.GROUP_BAN, new GroupBanCheck());
		}
		
		for (BanReason banReason : BanReason.getAll()){
			if (plugin.config.getBoolean(MineBansConfig.getReasonEnabled(banReason))){
				this.banDataChecksMap.put(ConnectionDeniedReason.TOO_MANY_BANS, new TooManyBansCheck(plugin));
				break;
			}
		}
		
		this.infoDataActionList.add(new AppealUnbanAction(plugin));
	}
	
	public Set<Entry<ConnectionDeniedReason, LocalJoinCheck>> getLocalChecks(){
		return new HashMap<ConnectionDeniedReason, LocalJoinCheck>(this.localChecksMap).entrySet();
	}
	
	public Set<Entry<ConnectionDeniedReason, InfoDataJoinCheck>> getInfoDataChecks(){
		return new HashMap<ConnectionDeniedReason, InfoDataJoinCheck>(this.infoDataChecksMap).entrySet();
	}
	
	public Set<Entry<ConnectionDeniedReason, BanDataJoinCheck>> getBanDataChecks(){
		return new HashMap<ConnectionDeniedReason, BanDataJoinCheck>(this.banDataChecksMap).entrySet();
	}
	
	public ArrayList<InfoDataJoinAction> getInfoDataActions(){
		return new ArrayList<InfoDataJoinAction>(this.infoDataActionList);
	}
	
	public ArrayList<BanDataJoinAction> getBanDataActions(){
		return new ArrayList<BanDataJoinAction>(this.banDataActionList);
	}
	
}
