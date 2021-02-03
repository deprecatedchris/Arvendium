package com.solexgames.arvendium.util;

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

public class ItemUtil implements Listener
{
    private ItemStack is;

    public ItemUtil(Material mat) {
        this.is = new ItemStack(mat);
    }

    public ItemUtil(ItemStack is) {
        this.is = is;
    }

    public ItemUtil amount(int amount) {
        this.is.setAmount(amount);
        return this;
    }

    public ItemUtil name(String name) {
        ItemMeta meta = this.is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemUtil lore(String name) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> lore = (List<String>)meta.getLore();
        if (lore == null) {
            lore = new ArrayList<String>();
        }
        lore.add(name);
        meta.setLore((List)lore);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemUtil lore(List<String> lore) {
        List<String> toSet = new ArrayList<String>();
        ItemMeta meta = this.is.getItemMeta();
        for (String string : lore) {
            toSet.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        meta.setLore((List)toSet);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemUtil durability(int durability) {
        this.is.setDurability((short)durability);
        return this;
    }

    public ItemUtil data(int data) {
        this.is.setData(new MaterialData(this.is.getType(), (byte)data));
        return this;
    }

    public ItemUtil enchantment(Enchantment enchantment, int level) {
        this.is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemUtil enchantment(Enchantment enchantment) {
        this.is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemUtil type(Material material) {
        this.is.setType(material);
        return this;
    }

    public ItemUtil clearLore() {
        ItemMeta meta = this.is.getItemMeta();
        meta.setLore((List)new ArrayList());
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemUtil clearEnchantments() {
        for (Enchantment e : this.is.getEnchantments().keySet()) {
            this.is.removeEnchantment(e);
        }
        return this;
    }

    public ItemUtil color(Color color) {
        if (this.is.getType() == Material.LEATHER_BOOTS || this.is.getType() == Material.LEATHER_CHESTPLATE || this.is.getType() == Material.LEATHER_HELMET || this.is.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta)this.is.getItemMeta();
            meta.setColor(color);
            this.is.setItemMeta((ItemMeta)meta);
            return this;
        }
        throw new IllegalArgumentException("color() only applicable for leather armor!");
    }

    public ItemStack build() {
        return this.is;
    }
}
