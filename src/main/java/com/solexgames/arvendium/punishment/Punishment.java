package com.solexgames.arvendium.punishment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Punishment {

    private String reason;
    private PunishmentType type;
    private long duration, adddedAt, removedAt;

    /**
     * Punishment Constructor.
     *
     * @param reason the reason of the punishment.
     * @param duration the duration of the punishment.
     * @param adddedAt the date the punishment was addedAt.
     * @param removedAt the date the punishment was removedAt.
     * @param type the type of punishment.
     */

    public Punishment(String reason, long duration, long adddedAt, long removedAt, PunishmentType type) {
        this.reason = reason;
        this.duration = duration;
        this.adddedAt = adddedAt;
        this.removedAt = removedAt;
        this.type = type;
    }
}
