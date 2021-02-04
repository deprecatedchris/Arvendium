package com.solexgames.arvendium.essentials;

import com.solexgames.arvendium.utils.command.BaseCommand;
import com.solexgames.arvendium.utils.command.Command;
import com.solexgames.arvendium.utils.command.CommandArgs;
import org.bukkit.ChatColor;

public class ArvendiumCommand extends BaseCommand {

    @Command(name = "arvendium", aliases = {"perm", "rank"}, permission = "arvendium.rank.import")
    public void onCommand(CommandArgs command) {
            command.getPlayer().sendMessage(ChatColor.RED + "Usage: /arvendium <import|debug>");
        }
    }

