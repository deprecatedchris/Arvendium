package com.solexgames.arvendium.rank;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

@Getter
@Setter
public class RankData {

    private String name;
    private String prefix;
    private String suffix;
    private boolean defaultRank;

    private int weight;

    public RankData(String name, String prefix, String suffix, boolean defaultRank) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.defaultRank = defaultRank;
    }

    public RankData(String name) {
        this(name, "&f", "&f", false);
    }

    public String getColorPrefix() {
        if (this.prefix.isEmpty()) {
            return "";
        }
        char code = 'f';
        char magic = 'f';
        int count = 0;
        for (String string : this.prefix.split("&")) {
            if (!string.isEmpty() && ChatColor.getByChar(string.toCharArray()[0]) != null) {
                if (count == 0) {
                    code = string.toCharArray()[0];
                }
                else {
                    magic = string.toCharArray()[0];
                }
                ++count;
            }
        }
        ChatColor color = ChatColor.getByChar(code);
        ChatColor magicColor = ChatColor.getByChar(magic);
        return (count == 1) ? color.toString() : (color.toString() + magicColor.toString());
    }
}
