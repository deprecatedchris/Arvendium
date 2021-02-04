package com.solexgames.arvendium.profile.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PunishmentType {

    WARN("Warn"),
    KICK("Kick"),
    MUTE("Mute"),
    BAN("Ban"),
    BLACKLIST("Blacklist");

    @Getter public String display;
}