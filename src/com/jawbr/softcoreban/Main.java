package com.jawbr.softcoreban;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.jawbr.softcoreban.listeners.DeathListener;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("SoftcoreBan plugin has been enabled");
		
		// Register death event
		PluginManager pm = getServer().getPluginManager();
		DeathListener listener = new DeathListener(this);
		pm.registerEvents(listener, this);
		
		// Gen and load config file
		saveDefaultConfig();
		
	}
	
	@Override
	public void onDisable() {
		getLogger().info("SoftcoreBan plugin has been disabled");
	}
	
}
