package com.jawbr.softcoreban.util;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathBanMsgCustom {
	
	public static String getBanDeathReason(Player player, PlayerDeathEvent event) {
		String deathReason = event.getDeathMessage();
		
		if(deathReason.contains(player.getName())) 
			deathReason = deathReason.replaceAll(player.getName(), "You");
		
		return deathReason;
	}
}
