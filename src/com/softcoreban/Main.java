package com.softcoreban;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

//import com.softcoreban.calendar.DetectDate;
import com.softcoreban.listeners.DeathListener;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("SoftcoreBan plugin has been enabled");
		
		// Register death event
		PluginManager pm = getServer().getPluginManager();
		DeathListener listener = new DeathListener(this);
		pm.registerEvents(listener, this);
		
		//DetectDate listenerDD = new DetectDate(this);
		//pm.registerEvents(listenerDD, this);
	}
	
	@Override
	public void onDisable() {
		getLogger().info("SoftcoreBan plugin has been disabled");
	}
	
}
