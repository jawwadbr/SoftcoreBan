package com.jawbr.softcoreban.listeners;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.jawbr.softcoreban.Main;
import com.jawbr.softcoreban.util.Capitalize;
import com.jawbr.softcoreban.util.DeathBanMsgCustom;
import com.jawbr.softcoreban.util.BroadcastMsg;

public class DeathListener implements Listener{
	
	private Main plugin = Main.getPlugin(Main.class);
	private FileConfiguration configFile = plugin.getConfig();
	private int dayOfweek;
	
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
		SimpleDateFormat sdf = new SimpleDateFormat(configFile.getString("date-format"));
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
		
		/*
		 * Get the unban hour in minutes and convert it to millis. 
		 * For some reason converting minutes direct to millis makes some discrepancy on the value
		 * So because of that first converting everything to seconds and then millis
		 */
		long finalSecondsToMillis = (((hour*3600)+(minutes*60)+seconds)*1000);
		
		int cfgUnbanHour = (configFile.getInt("unbanHour")*60)*1000;
		
		return (getCurrentDate()-finalSecondsToMillis)+cfgUnbanHour;
	}
	
	public long getUnbanTime() {
		
		long date = getBanFinalHour();
		
		int limitBanDays = configFile.getInt("limitBanDays");
		
		int currentWeek = dayOfweek;
		int daysUntilTarget = 0;
		
		while(currentWeek != 5) {
			if(currentWeek >= 7)
				currentWeek = 0;
			currentWeek++;
			daysUntilTarget++;
		}
		
		// If statement to define limit ban days
		if(daysUntilTarget > limitBanDays)
			daysUntilTarget = limitBanDays;
		
		return date + (daysUntilTarget*86400000); // 86400000 =  1 day
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
		
		// If is weekend player won't be banned
		dayOfweek = LocalDate.now().getDayOfWeek().getValue();
		if(dayOfweek >= 5) { 
			BroadcastMsg.consoleMessage("[SoftcoreBan] Today is "+ Capitalize.capitalize(getDateOfWeek()) + ". " + player.getName() + " will not be banned.");
		} else {
			
			// Scheduler to prevent player item dupe when they die. The ban will apply after 20 ticks/1 sec.
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					long date = getUnbanTime();
					Date result = new Date(date);
					
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "ban " + player.getName() + " [SoftcoreBan] "+DeathBanMsgCustom.getBanDeathReason(player, event));
					BroadcastMsg.broadcastMessage("[SoftcoreBan] "+event.getDeathMessage()+". Ban will be removed on " + getBanReadableTime(result));
					
					setExpiration(player);
				}
			}, 1);
			
		}
	}
	
}
