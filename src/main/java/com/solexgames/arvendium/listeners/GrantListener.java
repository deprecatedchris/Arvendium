package com.solexgames.arvendium.listeners;

import com.google.gson.JsonObject;
import com.solexgames.arvendium.Arvendium;
import com.solexgames.arvendium.profile.grant.Grant;
import com.solexgames.arvendium.managers.jedis.JedisSubscriberAction;
import com.solexgames.arvendium.profile.Profile;
import com.solexgames.arvendium.profile.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GrantListener implements Listener {

    private static Arvendium main;

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            String title = event.getInventory().getTitle();
            String displayName = itemStack.getItemMeta().getDisplayName();
            if (title.contains(ChatColor.GRAY + "Grants") && player.hasPermission("arvendium.rank.view")) {
                event.setCancelled(true);
                int page = Integer.parseInt(title.substring(title.lastIndexOf("/") - 1, title.lastIndexOf("/")));
                int total = Integer.parseInt(title.substring(title.lastIndexOf("/") + 1, title.lastIndexOf("/") + 2));
                String playerName = ChatColor.stripColor(event.getInventory().getItem(4).getItemMeta().getLore().get(0).substring(event.getInventory().getItem(4).getItemMeta().getLore().get(0).indexOf(" "), event.getInventory().getItem(4).getItemMeta().getLore().get(0).length())).trim();
                Profile profile = Profile.getExternalByName(playerName);
                if (event.getRawSlot() == 9 && profile != null && itemStack.getDurability() == 5) {
                    Grant activeGrant = profile.getActiveGrant();
                    activeGrant.setActive(false);
                    profile.save();
                    Player profilePlayer = Bukkit.getPlayer(profile.getUuid());
                    if (profilePlayer == null) {
                        JsonObject object = new JsonObject();
                        object.addProperty("action", JedisSubscriberAction.DELETE_GRANT.name());
                        JsonObject payload = new JsonObject();
                        payload.addProperty("uuid", profile.getUuid().toString());
                        object.add("payload", payload);
                        GrantListener.main.getJedisPubSub().write(object.toString());
                    }
                    else {
                        Rank rank = Rank.getDefaultRank();
                        if (rank != null) {
                            profilePlayer.sendMessage(ChatColor.GREEN + "Your rank has been set to " + rank.getData().getColorPrefix() + rank.getData().getName() + ChatColor.GREEN + ".");
                            player.sendMessage(ChatColor.GREEN + "Finished grant!");
                            profile.setupAttachment();
                        }
                    }
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Grant successfully disabled.");
                    return;
                }
                if (displayName.contains("Next Page")) {
                    if (page + 1 > total) {
                        player.sendMessage(ChatColor.RED + "There are no more pages.");
                        return;
                    }
                    player.openInventory(GrantListener.main.getRankHandler().getGrantsInventory(profile, playerName, page + 1));
                }
                else if (displayName.contains("Previous Page")) {
                    if (page == 1) {
                        player.sendMessage(ChatColor.RED + "You're on the first page.");
                        return;
                    }
                    player.openInventory(GrantListener.main.getRankHandler().getGrantsInventory(profile, playerName, page - 1));
                }
            }
        }
    }

    static {
        GrantListener.main = Arvendium.getInstance();
    }
}
