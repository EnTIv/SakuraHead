package com.entiv.sakurahead;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTListCompound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

class Skull {

    String type;
    final String displayName;

    final String texturesValue;

    final double change;

    final List<String> lore;

    private final Date data = new Date();

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Skull(double change, String type, List<String> lore, String texturesValue) {

        lore.replaceAll(s -> s.replace("%time%", simpleDateFormat.format(this.data)));

        this.lore = lore;
        this.type = type;

        this.displayName = Message.toColor(type);
        this.texturesValue = texturesValue;
        this.change = change;
    }

    double getChange() {
        return this.change;
    }

    ItemStack getItemStack() {

        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        NBTItem nbtItem = new NBTItem(head);

        NBTCompound skull = nbtItem.addCompound("SkullOwner");
        skull.setString("Id", "ca9fc57f-9b89-4c75-90ad-7afa3b0ebc03");

        NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Value", texturesValue);

        ItemStack itemStack = nbtItem.getItem();

        return new ItemBuilder(itemStack).name(displayName).lore(lore).build();
    }

    ItemStack getItemStack(int amount) {

        ItemStack head = new ItemStack(Material.SKULL_ITEM, amount, (short) 3);
        NBTItem nbtItem = new NBTItem(head);

        NBTCompound skull = nbtItem.addCompound("SkullOwner");
        skull.setString("Id", String.valueOf(UUID.randomUUID()));
        NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Value", texturesValue);
        ItemStack itemStack = nbtItem.getItem();

        return new ItemBuilder(itemStack).name(displayName).lore(lore).build();
    }
}













