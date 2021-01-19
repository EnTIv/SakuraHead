package com.entiv.sakurahead;

import com.destroystokyo.paper.Title;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;

public class EntityListener implements Listener {

    Main plugin = Main.getInstance();

    @EventHandler
    void onEntityDead(EntityDeathEvent event) {

        LivingEntity entity = event.getEntity();
        EntityType entityType = entity.getType();

        Player killer = entity.getKiller();

        if (killer == null || entityType == EntityType.ARMOR_STAND) return;

        if (entityType.equals(EntityType.PLAYER)) {

            double playerChange = plugin.getConfig().getDouble("Player.Change");

            if (playerChange > Math.random()) {
                givePlayerSkull(killer, event);
                sendTitle(killer);
            }

        } else {

            Skull entitySkull = Main.getInstance().getSkull(entityType);

            if (entitySkull.getChange() >= Math.random()) {
                giveMobSkull(entitySkull, event);
                sendTitle(killer);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onBreakBlock(BlockBreakEvent event) {

        String version = Bukkit.getVersion();
        Plugin nbtapi = Bukkit.getPluginManager().getPlugin("NBTAPI");

        if (event.isCancelled()) return;
        if (nbtapi == null) return;
        if (version.contains("Thermos")) return;
        if (version.contains("arclight")) return;

        Collection<ItemStack> drops = event.getBlock().getDrops();

        for (ItemStack itemStack : drops) {

            if (!itemStack.getType().equals(Material.SKULL_ITEM)) continue;

            NBTItem nbtItem = new NBTItem(itemStack);

            NBTCompound skullOwner = nbtItem.getCompound("SkullOwner");
            if (skullOwner == null) return;

            String value = skullOwner.getCompound("Properties").getCompoundList("textures").get(0).getString("Value");
            if (value == null) continue;

            String entityName = getEntityNameFromValue(value);
            if (entityName == null) return;
            Skull skull = Main.getInstance().getSkull(entityName);

            if (version.contains("CatServer")) {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
            } else {
                event.setDropItems(false);
            }

            Location location = event.getBlock().getLocation();

            ItemStack skullItemStack = skull.getItemStack();
            location.getWorld().dropItem(location, skullItemStack);
        }
    }

    private String getEntityNameFromValue(String value) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("SkullType");
        if (section == null) return null;
        for (String entityName : section.getKeys(false)) {
            String configValue = section.getString(entityName.concat(".Value"));
            if (value.equals(configValue)) return entityName;
        }
        return null;
    }

    private void givePlayerSkull(Player killer, EntityDeathEvent event) {

        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
        Player killed = (Player) event.getEntity();

        String display = plugin.getConfig().getString("Player.DisplayName");
        itemMeta.setDisplayName(Message.toColor(display.replace("%killed%", killed.getName())));
        itemMeta.setOwningPlayer(killed);
        itemStack.setItemMeta(itemMeta);

        killer.getInventory().addItem(itemStack);

        boolean dropInventory = Main.getInstance().getConfig().getBoolean("DropInventory");

        if (dropInventory) {
            MainCommand.giveSkull(killer, itemStack);
        } else {
            event.getDrops().add(itemStack);
        }

        String dropPlayerHead = plugin.getConfig().getString("Message.DropPlayerHead").replace("%player%", killer.getName()).replace("%target%", killed.getName());
        Message.sendAllPlayers(dropPlayerHead);
    }

    private void giveMobSkull(Skull entitySkull, EntityDeathEvent event) {

        ItemStack itemStack = entitySkull.getItemStack();
        Player killer = event.getEntity().getKiller();
        String dropMobHead = plugin.getConfig().getString("Message.DropMobHead");

        sendTitle(killer);
        boolean dropInventory = Main.getInstance().getConfig().getBoolean("DropInventory");

        if (dropInventory) {

            MainCommand.giveSkull(killer, itemStack);

        } else {
            event.getDrops().add(itemStack);
        }

        String message = Message.toColor(Message.replace(dropMobHead, "%player%", killer.getName(), "%target%", Message.withoutColor(entitySkull.type)));
        plugin.getServer().broadcastMessage(message.replaceFirst("的头", ""));
    }

    private void sendTitle(Player player) {
        if (Bukkit.getVersion().contains("Paper")) {
            String title = Message.toColor(plugin.getConfig().getString("Message.Title"));
            String subTitle = Message.toColor(plugin.getConfig().getString("Message.SubTitle"));

            player.sendTitle(new Title(title, subTitle, 20, 20, 20));
        }

    }

}
