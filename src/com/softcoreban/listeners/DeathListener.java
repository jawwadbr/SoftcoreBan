package com.softcoreban.listeners;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.softcoreban.Main;

public class DeathListener implements Listener{
	
	private Main plugin = Main.getPlugin(Main.class);
	
	public DeathListener(Main plugin) {
		
	}
	
	///////////////////////
	
	public String getDateOfWeek() {
		return LocalDate.now().getDayOfWeek().name();
	}
	
	public long getCurrentDate() {
		return System.currentTimeMillis();
	}
	
	public String getBanReadableTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		Date result = date;
		String strDate = sdf.format(result);
		return strDate;
	}
	
	public String getBanHour() {
		long date = getCurrentDate();
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		Date result = new Date(date);
		String strDate = sdf.format(result);
		return strDate;
	}
	
	public String getBanMinutes() {
		long date = getCurrentDate();
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		Date result = new Date(date);
		String strDate = sdf.format(result);
		return strDate;
	}
	
	public String getBanSeconds() {
		long date = getCurrentDate();
		SimpleDateFormat sdf = new SimpleDateFormat("ss");
		Date result = new Date(date);
		String strDate = sdf.format(result);
		return strDate;
	}
	
	public long getBanFinalHour() {
		int hour = Integer.parseInt(getBanHour());
		int minutes = Integer.parseInt(getBanMinutes());
		int seconds = Integer.parseInt(getBanSeconds());
		
		long finalSeconds = (((hour*3600)+(minutes*60)+seconds)*1000);
		
		return (getCurrentDate()-finalSeconds)+21600000;
	}
	
	public long getUnbanTime() {
		// get current time and final to know when to unban
		long date = getBanFinalHour();
		
		switch (getDateOfWeek().toLowerCase()) {
		case "monday": {
			date = date + 345600000; // 4 days
			
			return date;
		}
		case "tuesday": {
			date = date + 259200000; // 3 days
			return date;
		}
		case "wednesday": {
			date = date + 172800000; // 2 days
			return date;
		}
		case "thursday": {
			date = date + 86400000; // 1 days
			return date;
		}
		case "friday": {
			date = date + 604800000; // 7 days
			return date;
		}
		case "saturday": {
			date = date + 518400000; // 6 days
			return date;
		}
		case "sunday": {
			date = date + 518400000; // 5 days
			return date;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + getDateOfWeek().toLowerCase());
		}

	}
	
	///////////////////////
	
	public Date getExpiration(Player player) {
		BanList banlist = Bukkit.getBanList(Type.NAME);
		Date BanExpiration = new Date();
		for (BanEntry entry : banlist.getBanEntries()) {
			String name = entry.getTarget();
			if (name.equals(player.getName())) {
				BanExpiration = entry.getExpiration();
			}
		}
		return BanExpiration;
	}
	
	public void setExpiration(Player player) {
		long initialDate = getUnbanTime();
		Date date = new Date(initialDate);
		
		BanList banList = Bukkit.getBanList(Type.NAME);
		for (BanEntry entry : banList.getBanEntries()) {
			String name = entry.getTarget();
			if (name.equals(player.getName())) {
				entry.setExpiration(date);
				entry.save();
			}
		}
	}
	
	///////////////////////
	
	//Death Event Handler
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		
		long date = getUnbanTime();
		Date result = new Date(date);
		
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "ban " + player.getName() + " [SoftcoreBan]");
		Bukkit.broadcastMessage(player.getDisplayName()+" Softcore banned and will be unbanned on " + getBanReadableTime(result));
		
		setExpiration(player);
	}
	
}
