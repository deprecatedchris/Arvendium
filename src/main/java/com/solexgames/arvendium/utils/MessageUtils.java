package com.solexgames.arvendium.utils;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum MessageUtils {

    PLAYER_NOT_ONLINE(ChatColor.DARK_AQUA + "The player you specified is not online");

    private String message;
    MessageUtils(String message) { this.message = message; }
}