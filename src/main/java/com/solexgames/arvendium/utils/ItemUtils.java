package com.solexgames.arvendium.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils implements Listener {

    private ItemStack is;

    public ItemUtils(Material mat) {
        this.is = new ItemStack(mat);
    }

    public ItemUtils(ItemStack is) {
        this.is = is;
    }

    public ItemUtils amount(int amount) {
        this.is.setAmount(amount);
        return this;
    }

    public ItemUtils name(String name) {
        ItemMeta meta = this.is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemUtils lore(String name) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> lore = (List<String>) meta.getLore();
        if (lore == null) {
            lore = new ArrayList<String>();
        }
        lore.add(name);
        meta.setLore((List) lore);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemUtils lore(List<String> lore) {
        List<String> toSet = new ArrayList<String>();
        ItemMeta meta = this.is.getItemMeta();
        for (String string : lore) {
            toSet.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        meta.setLore((List) toSet);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemUtils durability(int durability) {
        this.is.setDurability((short) durability);
        return this;
    }

    public ItemUtils data(int data) {
        this.is.setData(new MaterialData(this.is.getType(), (byte) data));
        return this;
    }

    public ItemUtils enchantment(Enchantment enchantment, int level) {
        this.is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemUtils enchantment(Enchantment enchantment) {
        this.is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemUtils type(Material material) {
        this.is.setType(material);
        return this;
    }

    public ItemUtils clearLore() {
        ItemMeta meta = this.is.getItemMeta();
        meta.setLore((List) new ArrayList());
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemUtils clearEnchantments() {
        for (Enchantment e : this.is.getEnchantments().keySet()) {
            this.is.removeEnchantment(e);
        }
        return this;
    }

    public ItemUtils color(Color color) {
        if (this.is.getType() == Material.LEATHER_BOOTS || this.is.getType() == Material.LEATHER_CHESTPLATE || this.is.getType() == Material.LEATHER_HELMET || this.is.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) this.is.getItemMeta();
            meta.setColor(color);
            this.is.setItemMeta((ItemMeta) meta);
            return this;
        }
        throw new IllegalArgumentException("color() only applicable for leather armor!");
    }

    public ItemStack build() {
        return this.is;
    }
}
