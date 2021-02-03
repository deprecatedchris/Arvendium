package com.solexgames.arvendium.util;

import org.bukkit.ChatColor;

import java.util.Arrays;

public class StringUtil {

    public static String buildMessage(String[] args, int start) {
        return start >= args.length ? "" : ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }
}
