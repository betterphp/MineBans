package com.minebans;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum MineBansPermission {
	
	ALERT_ON_JOIN("minebans.alert.onjoin"),
	ALERT_ON_API_FAIL("minebans.alert.onapifail"),
	ALERT_ON_BAN("minebans.alert.onban"),
	ALERT_ON_UNBAN("minebans.alert.onunban"),
	ALERT_ON_KICK("minebans.alert.onkick"),
	ALERT_ON_EXEMPT("minebans.alert.onexempt"),
	ALERT_ON_UNEXEMPT("minebans.alert.onunexempt"),
	
	ADMIN_BAN("minebans.admin.ban"),
	ADMIN_KICK("minebans.admin.kick"),
	ADMIN_EXEMPT("minebans.admin.exempt"),
	ADMIN_LOOKUP("minebans.admin.lookup");
	
	private String node;
	
	private MineBansPermission(String node){
		this.node = node;
	}
	
	public List<Player> getPlayersWithPermission(){
		ArrayList<Player> players = new ArrayList<Player>();
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()){
			if (this.playerHasPermission(player)){
				players.add(player);
			}
		}
		
		return players;
	}
	
	public String getNode(){
		return this.node;
	}
	
	public Boolean playerHasPermission(CommandSender sender){
		return sender.hasPermission(this.node);
	}
	
}
