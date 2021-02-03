package com.solexgames.arvendium.util;

import org.bukkit.*;
import java.util.*;
import java.util.stream.*;

public class Color {
    public static String translate(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<String> translate(List<String> input) {
        return input.stream().map(Color::translate).collect(Collectors.toList());
    }
}