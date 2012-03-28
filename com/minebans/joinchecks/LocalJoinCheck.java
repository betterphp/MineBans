package com.minebans.joinchecks;

public abstract class LocalJoinCheck {
	
	public abstract boolean shouldPreventConnection(String playerName, String playerAddress);
	
}