package com.jawbr.softcoreban.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ConsoleMessage {

	public static void consoleMessage(String msg) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + msg);
	}
}
