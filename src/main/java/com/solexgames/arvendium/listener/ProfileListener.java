package com.solexgames.arvendium.listener;

import com.solexgames.arvendium.util.CC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import java.util.Arrays;
import java.util.List;

public class ProfileListener implements Listener {

	private final List<String> BLOCKED_COMMANDS = Arrays.asList(
			"/me",
			"/bukkit:me",
			"/minecraft:me",
			"//calc",
			"//eval",
			"//solve"
	);

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandProcess(PlayerCommandPreprocessEvent event) {
		if (BLOCKED_COMMANDS.stream().anyMatch(command -> event.getMessage().startsWith(command))) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(CC.RED + "You can't execute this command.");
		}
	}
}
