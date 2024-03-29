package com.solexgames.arvendium.profile.rank;

import com.solexgames.arvendium.Arvendium;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Rank {

    @Getter private static List<Rank> ranks = new ArrayList<>();
    @Getter private static Arvendium main = Arvendium.getInstance();

    private List<UUID> inheritance;
    private List<String> permissions;

    private UUID uuid;
    private RankData data;

    private int weight;

    /**
     * Rank Constructor.
     *
     * @param uuid the uuid.
     * @param inheritance the inheritance.
     * @param permissions the permissions.
     * @param data the data.
     */

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
