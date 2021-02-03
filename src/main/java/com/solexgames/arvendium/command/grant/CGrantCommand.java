package com.solexgames.arvendium.command.grant;

import com.google.gson.JsonObject;
import com.solexgames.arvendium.grant.Grant;
import com.solexgames.arvendium.jedis.JedisSubscriberAction;
import com.solexgames.arvendium.profile.Profile;
import com.solexgames.arvendium.rank.Rank;
import com.solexgames.arvendium.util.DateUtil;
import com.solexgames.arvendium.util.StringUtil;
import com.solexgames.arvendium.util.command.BaseCommand;
import com.solexgames.arvendium.util.command.Command;
import com.solexgames.arvendium.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class CGrantCommand extends BaseCommand {

    @Command(name = "cgrant", aliases = {"consolegrant", "arvendium.cgrant", "arvendium.consolegrant"})
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        if (sender instanceof ConsoleCommandSender) {
            String[] args = commandArgs.getArgs();

            if ((args.length == 0) || (args.length == 1) || (args.length == 2)) {
                sender.sendMessage(ChatColor.RED + "Usage: /grant <player> <rank> <duration> <reason>.");
            }

            UUID uuid;
            String playerName;
            long duration;

            if (args.length >= 3) {
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

                if (rank != null) {
                    if (args[2].equalsIgnoreCase("perm") || args[2].equalsIgnoreCase("permanent")) {
                        duration = 2147483647L;
                        return;
                    } else {
                        try {
                            duration = System.currentTimeMillis() - DateUtil.parseDateDiff(args[2], false);
                        } catch (Exception exception) {
                            sender.sendMessage(ChatColor.RED + "That's not a valid duration.");
                            return;
                        }
                    }

                    Profile profile = Profile.getByUuid(uuid);
                    String reason = StringUtil.buildMessage(args, 3);

                    if (profile != null) {
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

                        Grant newGrant = new Grant(null, rank, System.currentTimeMillis(), duration, reason, true);

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
                            grant2.addProperty("reason", reason);

                            payload.add("grant", grant2);
                            object.add("payload", payload);

                            this.main.getJedisPublisher().write(object.toString());
                        } else {
                            target.sendMessage(ChatColor.GREEN + "Your rank has been set to " + newGrant.getRank().getData().getColorPrefix() + newGrant.getRank().getData().getName() + ChatColor.GREEN + ".");
                            sender.sendMessage(ChatColor.GREEN + "Set " + target.getDisplayName() + ChatColor.GREEN + "'s rank to " + newGrant.getRank().getData().getColorPrefix() + newGrant.getRank().getData().getName() + ChatColor.GREEN + ".");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "That player's profile is not available right now.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to find rank.");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command is for console only.");
        }
    }
}
