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
import com.jawbr.softcoreban.util.ConsoleMessage;

public class DeathListener implements Listener{
	
	private Main plugin = Main.getPlugin(Main.class);
	private FileConfiguration configFile = plugin.getConfig();
	private int dayOfweek;
	//private String deathCause;
	
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
		SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
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
		
		int cfgUnbanHour = (configFile.getInt("unbanHour")*60)*1000;
		
		return (getCurrentDate()-finalSeconds)+cfgUnbanHour;
	}
	
	public long getUnbanTime() {
		
		long date = getBanFinalHour();
		
		int limitBanDays = configFile.getInt("limitBanDays");
		
		int currentWeek = LocalDate.now().getDayOfWeek().getValue();
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
		
		dayOfweek = LocalDate.now().getDayOfWeek().getValue();
		if(dayOfweek >= 5) 
			ConsoleMessage.consoleMessage("[SoftcoreBan] Today is "+ Capitalize.capitalize(getDateOfWeek()) + ". " + player.getName() + " will not be banned.");
		
		else {
			
			long date = getUnbanTime();
			Date result = new Date(date);
			
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "ban " + player.getName() + " [SoftcoreBan]");
			Bukkit.broadcastMessage(player.getDisplayName()+" Softcore banned and will be unbanned on " + getBanReadableTime(result));
			
			setExpiration(player);
		}
	}
	
//	@EventHandler
//	public void onEntityDeath(EntityDeathEvent event) {
//	  if (event.getEntity() instanceof Player) {
//		  
//	    Player player = (Player) event.getEntity();
//	    EntityDamageEvent deathCause = player.getLastDamageCause();
//	    
//	    if(deathCause.getCause() == DamageCause.ENTITY_ATTACK) {
//	    	
//	      Entity entity = (((EntityDamageByEntityEvent)deathCause).getDamager());
//	      
//	      if(entity instanceof Player) {
//	    	  
//	        Player killerPlayer = (Player)entity;
//	        this.deathCause = "You have been slain by " + killerPlayer.getName();
//	        //player.sendMessage("You have been slain by " + killerPlayer.getName());
//	        //player was killed by killerPlayer, do whatever
//	        
//	      }
//	      else {
//	    	  
//	        Monster killerMob = (Monster)entity;
//	        this.deathCause = "You have been slain by a " + killerMob.getType().toString();
//	        //player.sendMessage("You have been slain by a " + killerMob.getType().toString());
//	        //player was killed by killerMob, do whatever
//	        
//	      }
//	    }
//	  }
//	}
	
}
