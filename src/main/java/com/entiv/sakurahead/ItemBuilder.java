package com.entiv.sakurahead;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private final ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = new ItemStack(itemStack);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(material, 1);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder name(String displayName) {

        if (displayName == null) return this;

        itemMeta.setDisplayName(Message.toColor(displayName));
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder lore(String... lore) {

        itemMeta.setLore(Arrays.asList(Message.toColor(lore)));
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder lore(List<String> lore) {

        itemMeta.setLore(Message.toColor(lore));
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder addLore(String... lore) {

        List<String> itemLore = itemMeta.getLore();
        if (itemLore == null) return this;

        itemLore.addAll(Arrays.asList(Message.toColor(lore)));

        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {

        List<String> itemLore = itemMeta.getLore();
        if (itemLore == null) return this;

        itemLore.addAll(Message.toColor(lore));

        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addFlag(ItemFlag itemFlag) {
        itemMeta.addItemFlags(itemFlag);
        itemStack.setItemMeta(itemMeta);
        return this;
    }


    public ItemBuilder setUnbreakable() {

        itemMeta.setUnbreakable(true);

        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}