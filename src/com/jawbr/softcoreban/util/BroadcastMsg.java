package com.jawbr.softcoreban.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class BroadcastMsg {

	public static void consoleMessage(String msg) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + msg);
	}
	
	public static void broadcastMessage(String msg) {
		Bukkit.broadcastMessage(ChatColor.RED + msg);
	}
}
