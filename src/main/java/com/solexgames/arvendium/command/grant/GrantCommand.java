package com.solexgames.arvendium.command.grant;

import com.google.gson.JsonObject;
import com.solexgames.arvendium.grant.Grant;
import com.solexgames.arvendium.jedis.JedisSubscriberAction;
import com.solexgames.arvendium.profile.Profile;
import com.solexgames.arvendium.rank.Rank;
import com.solexgames.arvendium.util.command.BaseCommand;
import com.solexgames.arvendium.util.command.Command;
import com.solexgames.arvendium.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class GrantCommand extends BaseCommand {

    @Command(name = "grant", permission = "arvendium.rank.grant")
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /grant <player> <rank>.");
        } else {
            UUID uuid;
            String playerName;
            long duration = 2147483647L;
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                uuid = target.getUniqueId();
                playerName = target.getName();
            } else {
                try {
                    Map.Entry<UUID, String> recipient = this.main.getRankHandler().getExternalUuid(args[0]);
                    uuid = recipient.getKey();
                    playerName = recipient.getValue();
                } catch (Exception exception) {
                    sender.sendMessage(ChatColor.RED + "Failed to find player.");
                    return;
                }
            }

            Rank rank = Rank.getByName(args[1]);

            if (rank == null) {
                sender.sendMessage(ChatColor.RED + "Failed to find rank.");
                return;
            }

            Profile profile = Profile.getByUuid(uuid);

            if (profile.getActiveGrant().getRank() == rank) {
                sender.sendMessage(ChatColor.RED + "User has that grant already.");
                return;
            }

            if (profile.getName() == null || !profile.getName().equals(playerName)) {
                profile.setName(playerName);
                profile.save();
            }

            for (Grant grant : profile.getGrants()) {
                if (!grant.getRank().getData().isDefaultRank() && !grant.isExpired()) {
                    grant.setActive(false);
                }
            }

            Grant newGrant = new Grant(command.getPlayer().getUniqueId(), rank, System.currentTimeMillis(), duration, "Granted", true);

            profile.getGrants().add(newGrant);
            profile.setupAttachment();
            profile.save();

            if (target == null) {
                Profile.getProfiles().remove(profile);

                JsonObject object = new JsonObject();
                JsonObject payload = new JsonObject();
                JsonObject grant2 = new JsonObject();

                object.addProperty("action", JedisSubscriberAction.ADD_GRANT.name());
                payload.addProperty("uuid", profile.getUuid().toString());

                grant2.addProperty("rank", rank.getUuid().toString());
                grant2.addProperty("datedAdded", System.currentTimeMillis());
                grant2.addProperty("duration", duration);
                grant2.addProperty("reason", "Granted");

                payload.add("grant", grant2);
                object.add("payload", payload);

                this.main.getJedisPublisher().write(object.toString());
            } else {
                target.sendMessage(ChatColor.GREEN + "Your rank has been set to " + newGrant.getRank().getData().getColorPrefix() + newGrant.getRank().getData().getName() + ChatColor.GREEN + ".");
                sender.sendMessage(ChatColor.GREEN + "Set " + target.getDisplayName() + ChatColor.GREEN + "'s rank to " + newGrant.getRank().getData().getColorPrefix() + newGrant.getRank().getData().getName() + ChatColor.GREEN + ".");
            }
        }
    }
}
