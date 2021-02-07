package com.entiv.sakurahead;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTListCompound;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

class Skull {

    final String displayName;
    final String texturesValue;
    final String uuid;
    final double change;
    final List<String> lore;
    private final Date data = new Date();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String type;

    Skull(double change, String type, List<String> lore, String texturesValue, String uuid) {

        lore.replaceAll(s -> s.replace("%time%", simpleDateFormat.format(this.data)));

        this.lore = lore;
        this.type = type;

        this.displayName = Message.toColor(type);
        this.texturesValue = texturesValue;
        this.change = change;
        this.uuid = uuid;
    }

    double getChange() {
        return this.change;
    }

    ItemStack getItemStack() {
        Plugin nbtapi = Bukkit.getPluginManager().getPlugin("NBTAPI");
        String version = Bukkit.getVersion();

        if (nbtapi == null || version.contains("arclight")) {

            int versionInt = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[1]);

            if (versionInt >= 13) {
                return noNBTItem(texturesValue);
            } else {
                throw new NullPointerException("请安装 NBT-API 再尝试");
            }
        }

        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        NBTItem nbtItem = new NBTItem(head);

        NBTCompound skull = nbtItem.addCompound("SkullOwner");
        skull.setString("Id", uuid);

        NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Value", texturesValue);

        ItemStack itemStack = nbtItem.getItem();

        return new ItemBuilder(itemStack).name(displayName).lore(lore).build();
    }

    ItemStack getItemStack(int amount) {

        ItemStack itemStack = getItemStack();
        itemStack.setAmount(amount);

        return itemStack;
    }

    private ItemStack noNBTItem(String texturesValue) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        if (texturesValue == null) return head;

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", texturesValue));

        try {
            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(skullMeta, profile);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        head.setItemMeta(skullMeta);
        return head;
    }
}













