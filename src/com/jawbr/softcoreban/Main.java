package com.jawbr.softcoreban;

import org.bukkit.plugin.java.JavaPlugin;

import com.jawbr.softcoreban.commands.Author;
import com.jawbr.softcoreban.listeners.DeathListener;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("SoftcoreBan plugin has been enabled");
		
		// Register death event
		getServer().getPluginManager().registerEvents(new DeathListener(), this);
		
		// Register Commands
		getCommand("softcore-author").setExecutor(new Author());
		
		// Gen and load config file
		saveDefaultConfig();
		
	}
	
	@Override
	public void onDisable() {
		getLogger().info("SoftcoreBan plugin has been disabled");
	}
	
}
