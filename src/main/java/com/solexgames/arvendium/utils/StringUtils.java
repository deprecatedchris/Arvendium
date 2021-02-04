package com.solexgames.arvendium.utils;

import org.bukkit.ChatColor;

import java.util.Arrays;

public class StringUtils {

    public static String buildMessage(String[] args, int start) {
        return start >= args.length ? "" : ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }
}
