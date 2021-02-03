package com.solexgames.arvendium.handler;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.solexgames.arvendium.ArvendiumPlugin;
import com.solexgames.arvendium.grant.Grant;
import com.solexgames.arvendium.profile.Profile;
import com.solexgames.arvendium.rank.Rank;
import com.solexgames.arvendium.rank.RankData;
import com.solexgames.arvendium.util.DateUtil;
import com.solexgames.arvendium.util.ItemUtil;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Setter
public class RankHandler {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

    private final ArvendiumPlugin main;

    public RankHandler(ArvendiumPlugin main) {
        this.main = main;
        this.load();
        new BukkitRunnable() {
            public void run() {
                for (Profile profile : Profile.getProfiles()) {
                    for (Grant grant : profile.getGrants()) {
                        if (grant.isExpired() && grant.isActive()) {
                            grant.setActive(false);
                            profile.setupAttachment();
                            Player player = Bukkit.getPlayer(profile.getUuid());
                            if (player == null) {
                                continue;
                            }
                            player.sendMessage(ChatColor.GREEN + "Your rank has been set to " + profile.getActiveGrant().getRank().getData().getColorPrefix() + profile.getActiveGrant().getRank().getData().getName() + ChatColor.GREEN + ".");
                        }
                    }
                }
            }
        }.runTaskTimer(main, 20L, 20L);
    }

    public Inventory getGrantsInventory(Profile profile, String name, int page) {
        int total = (int) Math.ceil(profile.getGrants().size() / 9.0);
        if (total == 0) {
            total = 1;
        }

        Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.DARK_GRAY + "Grants - " + page + "/" + total);

        inventory.setItem(0, new ItemUtil(Material.CARPET).durability(7).name(ChatColor.RED + "Previous Page").build());
        inventory.setItem(8, new ItemUtil(Material.CARPET).durability(7).name(ChatColor.RED + "Next Page").build());
        inventory.setItem(4, new ItemUtil(Material.PAPER).name(ChatColor.RED + "Page " + page + "/" + total).lore(Collections.singletonList(ChatColor.YELLOW + "Player: " + ChatColor.RED + name)).build());

        ArrayList<Grant> toLoop = new ArrayList<>(profile.getGrants());
        Collections.reverse(toLoop);

        toLoop.removeIf(grant -> grant.getRank().getData().isDefaultRank());
        for (Grant grant2 : toLoop) {
            if (toLoop.indexOf(grant2) >= page * 9 - 9 && toLoop.indexOf(grant2) < page * 9) {
                String end = "";
                if (grant2.getDuration() != 2147483647L) {
                    if (grant2.isExpired()) {
                        end = "Expired";
                    } else {
                        Calendar from = Calendar.getInstance();
                        Calendar to = Calendar.getInstance();
                        from.setTime(new Date(System.currentTimeMillis()));
                        to.setTime(new Date(grant2.getDateAdded() + grant2.getDuration()));
                        end = DateUtil.formatDateDiff(from, to);
                    }
                }
                String issuerName;
                if (grant2.getIssuer() == null) {
                    issuerName = "Console";
                } else {
                    issuerName = Profile.getByUuid(grant2.getIssuer()).getName();
                }
                inventory.setItem(9 + toLoop.indexOf(grant2) % 9, new ItemUtil(Material.WOOL).durability((grant2.isActive() && !grant2.isExpired()) ? 5 : 14).name(ChatColor.YELLOW + RankHandler.DATE_FORMAT.format(new Date(grant2.getDateAdded()))).lore(Arrays.asList("&7&m------------------------------", "&eBy: &c" + issuerName, "&eReason: &c" + grant2.getReason(), "&eRank: &c" + grant2.getRank().getData().getName(), "&7&m------------------------------", (grant2.getDuration() == 2147483647L) ? "&eThis is a permanent grant." : ("&eExpires in: &c" + end), "&7&m------------------------------")).build());
            }
        }
        return inventory;
    }

    public void load() {
        Block<Document> printDocumentBlock = document -> {
            RankData rankData = new RankData(document.getString("name"));
            rankData.setPrefix(document.getString("prefix"));
            rankData.setSuffix(document.getString("suffix"));
            rankData.setWeight(document.getInteger("weight"));
            rankData.setDefaultRank(document.getBoolean("default"));
            Object inheritance = document.get("inheritance");
            Object permissions = document.get("permissions");
            List<UUID> inheritanceList = new ArrayList<>();
            for (String id : inheritance.toString().replace("[", "").replace("]", "").replace(" ", "").split(",")) {
                if (!id.isEmpty()) {
                    inheritanceList.add(UUID.fromString(id));
                }
            }
            List<String> permissionsList = new ArrayList<>();
            for (String id2 : permissions.toString().replace("[", "").replace("]", "").replace(" ", "").split(",")) {
                if (!id2.isEmpty()) {
                    permissionsList.add(id2);
                }
            }
            new Rank(UUID.fromString(document.getString("uuid")), inheritanceList, permissionsList, rankData);
        };
        this.main.getCoreDatabase().getRanks().find().forEach(printDocumentBlock);
    }

    public void save() {
        for (Rank rank : Rank.getRanks()) {
            Document document = new Document();
            document.put("uuid", rank.getUuid().toString());
            List<String> inheritance = new ArrayList<>();
            for (UUID uuid : rank.getInheritance()) {
                inheritance.add(uuid.toString());
            }
            document.put("inheritance", inheritance);
            document.put("permissions", rank.getPermissions());
            document.put("name", rank.getData().getName());
            document.put("prefix", rank.getData().getPrefix());
            document.put("suffix", rank.getData().getSuffix());
            document.put("weight", rank.getData().getWeight());
            document.put("default", rank.getData().isDefaultRank());
            this.main.getCoreDatabase().getRanks().replaceOne(Filters.eq("uuid", rank.getUuid().toString()), document, new UpdateOptions().upsert(true));
        }
    }

    public Map.Entry<UUID, String> getExternalUuid(String name) throws IOException, ParseException {
        Document document = ArvendiumPlugin.getInstance().getCoreDatabase().getProfiles().find(Filters.eq("recentName", name)).first();
        if (document != null && document.containsKey("recentName")) {
            return new AbstractMap.SimpleEntry<>(UUID.fromString(document.getString("uuid")), document.getString("recentName"));
        }
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(reader.readLine());
        UUID uuid = UUID.fromString(String.valueOf(obj.get("id")).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        name = String.valueOf(obj.get("name"));
        reader.close();
        return new AbstractMap.SimpleEntry<>(uuid, name);
    }
}
