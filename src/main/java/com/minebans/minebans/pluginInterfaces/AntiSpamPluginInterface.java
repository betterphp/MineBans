package com.minebans.minebans.pluginInterfaces;

public abstract class AntiSpamPluginInterface extends PluginInterface {
	
	public abstract int getMaxViolationLevel(String playerName);
	
}
