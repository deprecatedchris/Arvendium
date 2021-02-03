package com.solexgames.arvendium.command;

import com.google.gson.JsonObject;
import com.solexgames.arvendium.jedis.JedisSubscriberAction;
import com.solexgames.arvendium.rank.Rank;
import com.solexgames.arvendium.rank.RankData;
import com.solexgames.arvendium.util.command.BaseCommand;
import com.solexgames.arvendium.util.command.Command;
import com.solexgames.arvendium.util.command.CommandArgs;
import com.solexgames.arvendium.util.file.ConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImportCommand extends BaseCommand {

    @Command(name = "arvendium.import", aliases = {"importranks"}, permission = "arvendium.rank.import")
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();

        command.getPlayer().sendMessage(ChatColor.GREEN + "Processing...");
        this.handleImport(command);
        command.getPlayer().sendMessage(ChatColor.GREEN + "Finished...");
    }

    private void handleImport(CommandArgs command) {
        Rank.getRanks().clear();

        this.main.getCoreDatabase().getProfiles().drop();
        this.main.getCoreDatabase().getRanks().drop();

        ConfigFile config = this.main.getRanksFile();

        for (String key : config.getConfiguration().getKeys(false)) {

            String name = config.getString(key + ".NAME");
            String prefix = config.getString(key + ".PREFIX", "&f", false);
            String suffix = config.getString(key + ".SUFFIX", "&f", false);
            int weight = config.getInt(key + ".WEIGHT");
            boolean defaultRank = config.getBoolean(key + ".DEFAULT");
            List<String> permissions = config.getStringListOrDefault(key + ".PERMISSIONS", new ArrayList<>());

            RankData data = new RankData(name);

            data.setPrefix(prefix);
            data.setSuffix(suffix);
            data.setDefaultRank(defaultRank);
            data.setWeight(weight);

            new Rank(UUID.randomUUID(), new ArrayList<>(), permissions, data);
        }

        for (String key : config.getConfiguration().getKeys(false)) {
            Rank rank = Rank.getByName(config.getString(key + ".NAME"));
            if (rank != null) {
                for (String name2 : config.getStringListOrDefault(key + ".INHERITANCE", new ArrayList<>())) {
                    Rank other = Rank.getByName(config.getString(name2 + ".NAME"));
                    if (other != null) {
                        rank.getInheritance().add(other.getUuid());
                    }
                }
            }
        }
        this.main.getRankHandler().save();

        new BukkitRunnable() {
            public void run() {
                JsonObject object = new JsonObject();
                object.addProperty("action", JedisSubscriberAction.IMPORT_RANKS.name());
                JsonObject payload = new JsonObject();
                payload.addProperty("player", command.getSender().getName());
                object.add("payload", payload);
                ImportCommand.this.main.getJedisPublisher().write(object.toString());
            }
        }.runTaskLater(this.main, 40L);
    }
}
