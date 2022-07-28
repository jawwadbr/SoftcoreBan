package com.jawbr.softcoreban.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jawbr.softcoreban.Main;

public class Author implements CommandExecutor{

	private Main plugin = Main.getPlugin(Main.class);
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arg) {
		
		if(command.getName().equalsIgnoreCase("softcore-author")) {
			// If sender is the Console Server
			if(!(sender instanceof Player)) {
				plugin.getLogger().info("Author: "+ plugin.getDescription().getAuthors());
				return true;
			}
			// If is Player
			Player player = (Player) sender;
			
			player.sendMessage("[SoftcoreBan] Author: "+ChatColor.DARK_RED+plugin.getDescription().getAuthors());
		}
		
		return true;
	}
	
	
}
