package com.solexgames.arvendium.rank;

import com.solexgames.arvendium.ArvendiumPlugin;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Rank {

    @Getter
    private static List<Rank> ranks = new ArrayList<>();
    @Getter
    private static ArvendiumPlugin main = ArvendiumPlugin.getInstance();

    private List<UUID> inheritance;
    private List<String> permissions;

    private UUID uuid;
    private RankData data;

    private int weight;

    public Rank(UUID uuid, List<UUID> inheritance, List<String> permissions, RankData data) {
        this.uuid = uuid;
        this.inheritance = inheritance;
        this.permissions = permissions;
        this.data = data;
        this.weight = data.getWeight();
        Rank.ranks.add(this);
    }

    public static Rank getDefaultRank() {
        for (Rank rank : Rank.ranks) {
            if (rank.getData().isDefaultRank()) {
                return rank;
            }
        }
        return null;
    }

    public static Rank getByName(String name) {
        for (Rank rank : Rank.ranks) {
            if (rank.getData().getName().replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) {
                return rank;
            }
        }
        return null;
    }

    public static Rank getByUuid(UUID uuid) {
        for (Rank rank : Rank.ranks) {
            if (rank.getUuid().equals(uuid)) {
                return rank;
            }
        }
        return null;
    }
}
