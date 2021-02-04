package com.solexgames.arvendium.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.solexgames.arvendium.Arvendium;
import com.solexgames.arvendium.profile.grant.Grant;
import com.solexgames.arvendium.profile.rank.Rank;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Setter
public class Profile {

    @Getter
    private static Set<Profile> profiles = new HashSet<>();
    @Setter
    private static Arvendium main = Arvendium.getInstance();

    private UUID uuid;
    private List<String> permissions;
    private List<Grant> grants;
    private Player player;
    private String name;
    private boolean loaded;
    private PermissionAttachment attachment;

    private final Date lastJoined = new Date();
    private String lastJoin;

    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mma");

    public Profile(UUID uuid, List<String> permissions, List<Grant> grants) {
        this.uuid = uuid;
        this.permissions = permissions;
        this.grants = grants;
        this.player = Bukkit.getPlayer(uuid);
        if (this.player != null) {
            this.name = this.player.getName();
            this.attachment = this.player.addAttachment(Profile.main);
        }
        else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer != null) {
                this.name = offlinePlayer.getName();
            }
            this.attachment = null;
        }
        Profile.profiles.add(this);
    }

    public Grant getActiveGrant() {
        Grant toReturn = null;
        for (Grant grant : this.grants) {
            if (grant.isActive() && !grant.getRank().getData().isDefaultRank()) {
                toReturn = grant;
            }
        }
        if (toReturn == null) {
            toReturn = new Grant(null, Objects.requireNonNull(Rank.getDefaultRank()), System.currentTimeMillis(), 2147483647L, "Default Rank", true);
        }
        return toReturn;
    }

    public void asyncLoad() {
        Bukkit.getScheduler().runTaskAsynchronously(Profile.main, this::load);
    }

    public Profile load() {
        Document document = Profile.main.getCoreDatabaseManager().getProfiles().find(Filters.eq("uuid", this.uuid.toString())).first();
        if (document != null) {
            for (JsonElement element : new JsonParser().parse(document.getString("grants")).getAsJsonArray()) {
                JsonObject keyGrant = element.getAsJsonObject();
                UUID issuer = null;
                if (keyGrant.get("issuer") != null) {
                    issuer = UUID.fromString(keyGrant.get("issuer").getAsString());
                }
                long dateAdded = keyGrant.get("dateAdded").getAsLong();
                long duration = keyGrant.get("duration").getAsLong();
                String reason = keyGrant.get("reason").getAsString();
                boolean active = keyGrant.get("active").getAsBoolean();
                Rank rank;
                try {
                    rank = Rank.getByUuid(UUID.fromString(keyGrant.get("rank").getAsString()));
                } catch (Exception ex) {
                    rank = Rank.getByName(keyGrant.get("rank").getAsString());
                    if (rank == null) {
                        throw new IllegalArgumentException("Invalid rank parameter");
                    }
                }
                if (rank != null) {
                    this.grants.add(new Grant(issuer, rank, dateAdded, duration, reason, active));
                }
            }

            if (document.containsKey("lastJoined")) {
                this.lastJoin = format.format(new Date());
            }
            if (document.containsKey("recentName")) {
                this.name = document.getString("recentName");
            }
            List<String> permissionsList = new ArrayList<>();
            for (String id : document.get("permissions").toString().replace("[", "").replace("]", "").replace(" ", "").split(",")) {
                if (!id.isEmpty()) {
                    permissionsList.add(id);
                }
            }
            this.permissions.addAll(permissionsList);
        }
        boolean hasDefaultRank = false;
        for (Grant grant : this.grants) {
            if (grant.getRank().getData().isDefaultRank()) {
                hasDefaultRank = true;
                break;
            }
        }
        if (!hasDefaultRank) {
            this.grants.add(new Grant(null, Objects.requireNonNull(Rank.getDefaultRank()), System.currentTimeMillis(), 2147483647L, "Default Rank", true));
        }
        this.loaded = true;
        this.setupAttachment();
        return this;
    }

    public void setupAttachment() {
        if (this.attachment != null) {
            Player player = Bukkit.getPlayer(this.uuid);
            if (player != null) {
                Grant grant = this.getActiveGrant();
                player.setDisplayName(ChatColor.translateAlternateColorCodes('&', grant.getRank().getData().getColorPrefix() + player.getName()));
            }
            for (String permission : this.attachment.getPermissions().keySet()) {
                this.attachment.unsetPermission(permission);
            }
            for (Grant grant2 : this.grants) {
                if (grant2 == null) {
                    continue;
                }
                if (grant2.isExpired()) {
                    continue;
                }
                for (String permission2 : grant2.getRank().getPermissions()) {
                    this.attachment.setPermission(permission2.replace("-", ""), !permission2.startsWith("-"));
                }
                for (UUID uuid : grant2.getRank().getInheritance()) {
                    Rank rank = Rank.getByUuid(uuid);
                    if (rank != null) {
                        for (String permission3 : rank.getPermissions()) {
                            this.attachment.setPermission(permission3.replace("-", ""), !permission3.startsWith("-"));
                        }
                    }
                }
            }
            for (String permission4 : this.permissions) {
                this.attachment.setPermission(permission4.replace("-", ""), !permission4.startsWith("-"));
            }
            if (player != null) {
                player.recalculatePermissions();
            }
        } else {
            Player player = Bukkit.getPlayer(this.uuid);
            if (player != null) {
                this.attachment = player.addAttachment(Profile.main);
                this.load();
            }
        }
    }

    public void remove() {
        Profile.profiles.remove(this);
    }

    public void save() {
        Document profileDocument = new Document();
        JsonArray grantsDocument = new JsonArray();
        profileDocument.put("uuid", this.uuid.toString());
        if (this.name != null) {
            profileDocument.put("recentName", this.name);
            profileDocument.put("lastJoined", this.lastJoin);
            profileDocument.put("recentNameLowercase", this.name.toLowerCase());
        }
        for (Grant grant : this.grants) {
            JsonObject grantDocument = new JsonObject();
            if (grant.getRank() == null) {
                continue;
            }
            if (grant.getRank().getData().isDefaultRank()) {
                continue;
            }
            if (grant.getIssuer() != null) {
                grantDocument.addProperty("issuer", grant.getIssuer().toString());
            }
            grantDocument.addProperty("dateAdded", grant.getDateAdded());
            grantDocument.addProperty("duration", grant.getDuration());
            grantDocument.addProperty("reason", grant.getReason());
            grantDocument.addProperty("active", grant.isActive() && !grant.isExpired());
            grantDocument.addProperty("rank", grant.getRank().getUuid().toString());
            grantDocument.addProperty("rankName", grant.getRank().getData().getName());
            grantsDocument.add(grantDocument);
        }
        profileDocument.put("grants", grantsDocument.toString());
        profileDocument.put("permissions", this.permissions);
        Profile.main.getCoreDatabaseManager().getProfiles().replaceOne(Filters.eq("uuid", this.uuid.toString()), profileDocument, new UpdateOptions().upsert(true));
    }

    public static Profile getByUuid(UUID uuid) {
        for (Profile profile : Profile.profiles) {
            if (profile.getUuid().equals(uuid)) {
                return profile;
            }
        }
        return getExternalByUuid(uuid);
    }

    private static Profile getExternalByUuid(UUID uuid) {
        Profile profile = new Profile(uuid, new ArrayList<>(), new ArrayList<>()).load();
        profile.remove();
        return profile;
    }

    public static Profile getExternalByName(String name) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            Profile profile = getByUuid(player.getUniqueId());
            if (profile != null && profile.getName() != null && profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        UUID uuid;
        String realName;
        try {
            Map.Entry<UUID, String> data = Profile.main.getRankHandler().getExternalUuid(name);
            uuid = data.getKey();
            realName = data.getValue();
        }
        catch (IOException | ParseException ex2) {
            return null;
        }
        Profile profile2 = new Profile(uuid, new ArrayList<>(), new ArrayList<>()).load();
        if (profile2.getName() == null || !profile2.getName().equals(realName)) {
            profile2.setName(realName);
        }
        profile2.remove();
        return profile2;
    }

    static {
        Profile.profiles = new HashSet<>();
        Profile.main = Arvendium.getInstance();
    }
}
