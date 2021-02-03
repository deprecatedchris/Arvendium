package com.solexgames.arvendium.command.grant;

import com.solexgames.arvendium.profile.Profile;
import com.solexgames.arvendium.util.command.BaseCommand;
import com.solexgames.arvendium.util.command.Command;
import com.solexgames.arvendium.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class GrantsCommand extends BaseCommand {

    @Command(name = "rankhistory", aliases = { "rankhistory.view", "grants"}, permission = "arvendium.rank.view")
    @Override
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        Player sender = command.getPlayer();
        if (args.length == 0) {
            command.getSender().sendMessage(ChatColor.RED + "Usage: /grants <player>.");
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        UUID uuid;
        String name;

        if (player != null) {
            uuid = player.getUniqueId();
            name = player.getName();
        } else {
            try {
                Map.Entry<UUID, String> recipient = this.main.getRankHandler().getExternalUuid(args[0]);
                uuid = recipient.getKey();
                name = recipient.getValue();
            }
            catch (Exception e) {
                command.getSender().sendMessage(ChatColor.RED + "Failed to find player.");
                return;
            }
        }

        Profile profile = Profile.getByUuid(uuid);
        if (profile != null) {
            if (profile.getName() == null || !profile.getName().equals(name)) {
                profile.setName(name);
                profile.save();
            }
            sender.openInventory(this.main.getRankHandler().getGrantsInventory(profile, name, 1));
        }
    }
}
