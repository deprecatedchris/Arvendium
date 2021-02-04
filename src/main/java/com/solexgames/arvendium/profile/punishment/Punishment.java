package com.solexgames.arvendium.profile.punishment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Punishment {

    private String reason;
    private PunishmentType type;
    private long duration, adddedAt, removedAt;

    /**
     * Constructs a punishment.
     * @param reason The reason of the punishment.
     * @param duration The duration of the punishment.
     * @param adddedAt The date the punishment was addedAt.
     * @param removedAt The date the punishment was removedAt.
     * @param type The type of punishment.
     */
    public Punishment(String reason, long duration, long adddedAt, long removedAt, PunishmentType type) {
        this.reason = reason;
        this.duration = duration;
        this.adddedAt = adddedAt;
        this.removedAt = removedAt;
        this.type = type;
    }
}