package com.solexgames.arvendium.command;

import com.solexgames.arvendium.util.command.BaseCommand;
import com.solexgames.arvendium.util.command.Command;
import com.solexgames.arvendium.util.command.CommandArgs;
import org.bukkit.ChatColor;

public class ArvendiumCommand extends BaseCommand {

    @Command(name = "arvendium", aliases = {"perm", "rank"}, permission = "arvendium.rank.import")
    public void onCommand(CommandArgs command) {
            command.getPlayer().sendMessage(ChatColor.RED + "Usage: /arvendium <import|debug>");
        }
    }

