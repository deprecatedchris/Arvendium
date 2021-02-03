package com.solexgames.arvendium.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;

public class WoolUtil
{
    private static ArrayList<ChatColor> woolColors;

    public static int convertChatColorToWoolData(ChatColor color) {
        if (color == ChatColor.DARK_RED) {
            color = ChatColor.RED;
        }
        return WoolUtil.woolColors.indexOf(color);
    }

    static {
        woolColors = new ArrayList<ChatColor>(Arrays.asList(ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.BLUE, ChatColor.BLACK, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.BLACK));
    }
}
