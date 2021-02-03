package com.solexgames.arvendium.grant;

import com.solexgames.arvendium.rank.Rank;
import com.solexgames.arvendium.rank.RankData;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
public class Grant {

    private UUID issuer;
    private UUID rankId;

    private long dateAdded;
    private long duration;

    private String reason;
    private boolean active;

    public Grant(UUID issuer, Rank rank, long dateAdded, long duration, String reason, boolean active) {
        this.issuer = issuer;
        this.rankId = rank.getUuid();
        this.dateAdded = dateAdded;
        this.duration = duration;
        this.reason = reason;
        this.active = active;
    }

    public Rank getRank() {
        Rank toReturn = Rank.getByUuid(this.rankId);
        if (toReturn == null) {
            this.active = false;
            toReturn = new Rank(UUID.randomUUID(), new ArrayList<>(), new ArrayList<>(), new RankData("N/A"));
            Rank.getRanks().remove(toReturn);
            return toReturn;
        }
        return toReturn;
    }

    public boolean isExpired() {
        return !this.active || System.currentTimeMillis() >= this.dateAdded + this.duration;
    }
}
