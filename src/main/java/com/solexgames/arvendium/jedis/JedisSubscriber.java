package com.solexgames.arvendium.jedis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.solexgames.arvendium.ArvendiumPlugin;
import com.solexgames.arvendium.grant.Grant;
import com.solexgames.arvendium.profile.Profile;
import com.solexgames.arvendium.rank.Rank;
import com.solexgames.arvendium.rank.RankData;
import com.solexgames.arvendium.util.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

@Getter
@Setter
public class JedisSubscriber {

    private JedisPubSub jedisPubSub;
    private Jedis jedis;
    private ArvendiumPlugin main;

    public JedisSubscriber(ArvendiumPlugin main) {
        this.main = main;
        this.jedis = new Jedis("redis-17008.c8.us-east-1-2.ec2.cloud.redislabs.com", 17008);
        this.jedis.auth("WAFEkJpejtb96EtBWmahGWC1ElIRpjv7");
        this.subscribe();
    }

    public void subscribe() {
        this.jedisPubSub = this.get();
        new Thread(() -> JedisSubscriber.this.jedis.subscribe(JedisSubscriber.this.jedisPubSub, "permissions")).start();
    }

    private JedisPubSub get() {
        return new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (channel.equalsIgnoreCase("permissions")) {
                    JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                    JedisSubscriberAction action = JedisSubscriberAction.valueOf(object.get("action").getAsString());
                    JsonObject payload = object.get("payload").getAsJsonObject();
                    if (action == JedisSubscriberAction.DELETE_PLAYER_PERMISSION) {
                        Player player = Bukkit.getPlayer(UUID.fromString(payload.get("uuid").getAsString()));
                        if (player != null) {
                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            if (profile != null) {
                                String permission = payload.get("permission").getAsString();
                                profile.getPermissions().remove(permission);
                                profile.setupAttachment();
                            }
                        }
                        return;
                    }
                    if (action == JedisSubscriberAction.ADD_PLAYER_PERMISSION) {
                        Player player = Bukkit.getPlayer(UUID.fromString(payload.get("uuid").getAsString()));
                        if (player != null) {
                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            if (profile != null) {
                                String permission = payload.get("permission").getAsString();
                                profile.getPermissions().add(permission);
                                profile.setupAttachment();
                            }
                        }
                        return;
                    }
                    if (action == JedisSubscriberAction.ADD_RANK_PERMISSION) {
                        Rank rank;
                        try {
                            rank = Rank.getByUuid(UUID.fromString(payload.get("rank").getAsString()));
                        }
                        catch (Exception ex) {
                            rank = Rank.getByName(payload.get("rank").getAsString());
                            if (rank == null) {
                                throw new IllegalArgumentException("Invalid rank parameter");
                            }
                        }
                        if (rank != null) {
                            String permission2 = payload.get("permission").getAsString();
                            rank.getPermissions().add(permission2);
                            Player player2 = Bukkit.getPlayer(payload.get("player").getAsString());
                            if (player2 != null) {
                                player2.sendMessage(ChatColor.GREEN + "Permission '" + permission2 + "' successfully added to rank named '" + rank.getData().getName() + "'.");
                            }
                            for (Profile profile2 : Profile.getProfiles()) {
                                if (profile2.getActiveGrant().getRank().getUuid().equals(rank.getUuid())) {
                                    profile2.setupAttachment();
                                }
                            }
                        }
                        return;
                    }
                    if (action == JedisSubscriberAction.DELETE_RANK_PERMISSION) {
                        Rank rank;
                        try {
                            rank = Rank.getByUuid(UUID.fromString(payload.get("rank").getAsString()));
                        }
                        catch (Exception ex) {
                            rank = Rank.getByName(payload.get("rank").getAsString());
                            if (rank == null) {
                                throw new IllegalArgumentException("Invalid rank parameter");
                            }
                        }
                        if (rank != null) {
                            String permission2 = payload.get("permission").getAsString();
                            rank.getPermissions().remove(permission2);
                            Player player2 = Bukkit.getPlayer(payload.get("player").getAsString());
                            if (player2 != null) {
                                player2.sendMessage(ChatColor.GREEN + "Permission '" + permission2 + "' successfully removed from rank named '" + rank.getData().getName() + "'.");
                            }
                            for (Profile profile2 : Profile.getProfiles()) {
                                if (profile2.getActiveGrant().getRank().getUuid().equals(rank.getUuid())) {
                                    profile2.setupAttachment();
                                }
                            }
                        }
                        return;
                    }
                    if (action == JedisSubscriberAction.DELETE_GRANT) {
                        UUID uuid = UUID.fromString(payload.get("uuid").getAsString());
                        Player player3 = Bukkit.getPlayer(uuid);
                        if (player3 != null) {
                            Profile profile3 = Profile.getByUuid(player3.getUniqueId());
                            if (!profile3.getActiveGrant().getRank().getData().isDefaultRank()) {
                                profile3.getActiveGrant().setActive(false);
                                Rank rank2 = Rank.getDefaultRank();
                                if (rank2 != null) {
                                    player3.sendMessage(ChatColor.GREEN + "Your rank has been set to " + rank2.getData().getColorPrefix() + rank2.getData().getName() + ChatColor.GREEN + ".");
                                }
                            }
                        }
                        return;
                    }
                    if (action == JedisSubscriberAction.ADD_GRANT) {
                        JsonObject grant = payload.get("grant").getAsJsonObject();
                        UUID uuid2 = UUID.fromString(payload.get("uuid").getAsString());
                        Player player2 = Bukkit.getPlayer(uuid2);
                        if (player2 != null) {
                            Profile profile4 = Profile.getByUuid(player2.getUniqueId());
                            Rank rank3;
                            try {
                                rank3 = Rank.getByUuid(UUID.fromString(grant.get("rank").getAsString()));
                            }
                            catch (Exception ex2) {
                                rank3 = Rank.getByName(grant.get("rank").getAsString());
                                if (rank3 == null) {
                                    throw new IllegalArgumentException("Invalid rank parameter");
                                }
                            }
                            if (rank3 != null) {
                                UUID issuer = grant.has("issuer") ? UUID.fromString(grant.get("issuer").getAsString()) : null;
                                for (Grant other : profile4.getGrants()) {
                                    if (!other.getRank().getData().isDefaultRank() && !other.isExpired()) {
                                        other.setActive(false);
                                    }
                                }
                                Grant newGrant = new Grant(issuer, rank3, grant.get("datedAdded").getAsLong(), grant.get("duration").getAsLong(), grant.get("reason").getAsString(), true);
                                profile4.getGrants().add(newGrant);
                                player2.sendMessage(ChatColor.GREEN + "Your rank has been set to " + newGrant.getRank().getData().getColorPrefix() + newGrant.getRank().getData().getName() + ChatColor.GREEN + ".");
                            }
                        }
                        return;
                    }
                    if (action == JedisSubscriberAction.IMPORT_RANKS) {
                        Rank.getRanks().clear();
                        Iterator<Profile> profileIterator = Profile.getProfiles().iterator();
                        while (profileIterator.hasNext()) {
                            Profile profile = profileIterator.next();
                            Player player2 = profile.getPlayer();
                            if (player2 != null && profile.getAttachment() != null) {
                                player2.removeAttachment(profile.getAttachment());
                            }
                            profileIterator.remove();
                        }
                        for (Player online : PlayerUtil.getOnlinePlayers()) {
                            new Profile(online.getUniqueId(), new ArrayList<>(), new ArrayList<>());
                        }
                        Player player3 = Bukkit.getPlayer(payload.get("player").getAsString());
                        if (player3 != null) {
                            player3.sendMessage(ChatColor.GREEN + "Ranks successfully imported!");
                        }
                        JedisSubscriber.this.main.getRankHandler().load();
                        return;
                    }
                    if (action == JedisSubscriberAction.ADD_RANK) {
                        String name = payload.get("name").getAsString();
                        Rank rank4 = new Rank(UUID.randomUUID(), new ArrayList<>(), new ArrayList<>(), new RankData(name));
                        Player player2 = Bukkit.getPlayer(payload.get("player").getAsString());
                        if (player2 != null) {
                            player2.sendMessage(ChatColor.GREEN + "Rank named '" + rank4.getData().getName() + "' successfully created.");
                        }
                        return;
                    }
                    if (action == JedisSubscriberAction.DELETE_RANK) {
                        Rank rank = Rank.getByName(payload.get("rank").getAsString());
                        if (rank != null) {
                            Player player3 = Bukkit.getPlayer(payload.get("player").getAsString());
                            if (player3 != null) {
                                player3.sendMessage(ChatColor.GREEN + "Rank named '" + rank.getData().getName() + "' successfully deleted.");
                            }
                            Rank.getRanks().remove(rank);
                        }
                    }
                    if (action == JedisSubscriberAction.SET_RANK_PREFIX) {
                        Rank rank = Rank.getByName(payload.get("rank").getAsString());
                        if (rank != null) {
                            Player player3 = Bukkit.getPlayer(payload.get("player").getAsString());
                            rank.getData().setPrefix(payload.get("prefix").getAsString());
                            if (player3 != null) {
                                player3.sendMessage(ChatColor.GREEN + "Rank named '" + rank.getData().getName() + "' prefix successfully changed.");
                            }
                        }
                    }
                    if (action == JedisSubscriberAction.SET_RANK_SUFFIX) {
                        Rank rank = Rank.getByName(payload.get("rank").getAsString());
                        if (rank != null) {
                            Player player3 = Bukkit.getPlayer(payload.get("player").getAsString());
                            rank.getData().setSuffix(payload.get("suffix").getAsString());
                            if (player3 != null) {
                                player3.sendMessage(ChatColor.GREEN + "Rank named '" + rank.getData().getName() + "' suffix successfully changed.");
                            }
                        }
                    }
                }
            }
        };
    }

    public JedisPubSub getJedisPubSub() {
        return this.jedisPubSub;
    }

    public Jedis getJedis() {
        return this.jedis;
    }
}
