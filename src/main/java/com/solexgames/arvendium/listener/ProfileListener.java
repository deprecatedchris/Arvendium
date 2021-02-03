package com.solexgames.arvendium.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ProfileListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandProcess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (event.getMessage().toLowerCase().startsWith("//calc")
				|| event.getMessage().toLowerCase().startsWith("//eval")
				|| event.getMessage().toLowerCase().startsWith("//solve")) {
			player.sendMessage(ChatColor.RED + "No permission.");
			event.setCancelled(true);
		}
	}
}
