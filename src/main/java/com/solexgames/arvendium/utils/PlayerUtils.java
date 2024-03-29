package com.solexgames.arvendium.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerUtils
{
    public static Set<String> getConvertedUuidSet(Set<UUID> uuids) {
        Set<String> toReturn = new HashSet<String>();
        for (UUID uuid : uuids) {
            toReturn.add(uuid.toString());
        }
        return toReturn;
    }

    public static List<Player> getOnlinePlayers() {
        ArrayList<Player> ret = new ArrayList<Player>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            ret.add(player);
        }
        return ret;
    }
}
