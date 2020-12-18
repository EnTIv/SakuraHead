package com.entiv.sakurahead;

import com.destroystokyo.paper.Title;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

import java.util.Collection;

public class EntityListener implements Listener {

    Main plugin = Main.getInstance();

    @EventHandler
    void onEntityDead(EntityDeathEvent event) {

        LivingEntity entity = event.getEntity();
        EntityType entityType = entity.getType();

        Player killer = entity.getKiller();

        if (killer == null || entityType == EntityType.ARMOR_STAND) return;

        double playerChange = plugin.getConfig().getDouble("Player.Change");

        if (entityType.equals(EntityType.PLAYER) && playerChange > Math.random()) {
            givePlayerSkull(killer, (Player) entity);
            return;
        }

        Skull entitySkull = Main.getInstance().getSkull(entityType);

        if (entitySkull.getChange() >= Math.random()) {

            ItemStack skull = entitySkull.getItemStack();
            event.getDrops().add(skull);

            String dropMobHead = plugin.getConfig().getString("Message.DropMobHead");

            plugin.getServer().broadcastMessage(Message.toColor(Message.replace(dropMobHead, "%player%", killer.getName(), "%target%", Message.withoutColor(entitySkull.type))));
            sendTitle(killer);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onBreakBlock(BlockBreakEvent event) {

        if (event.isCancelled()) return;

        Collection<ItemStack> drops = event.getBlock().getDrops();

        for (ItemStack itemStack : drops) {

            if (!itemStack.getType().equals(Material.SKULL_ITEM)) continue;

            NBTItem nbtItem = new NBTItem(itemStack);
            String value = nbtItem.getCompound("SkullOwner").getCompound("Properties").getCompoundList("textures").get(0).getString("Value");

            if (value == null) continue;

            String entityName = getEntityName(value);

            if (entityName == null) return;
            Skull skull = Main.getInstance().getSkull(entityName);

            event.setDropItems(false);
            Location location = event.getBlock().getLocation();

            ItemStack skullItemStack = skull.getItemStack();
            location.getWorld().dropItem(location, skullItemStack);
        }
    }

    private String getEntityName(String value) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("SkullType");
        if (section == null) return null;
        for (String entityName : section.getKeys(false)) {
            String configValue = section.getString(entityName.concat(".Value"));
            if (value.equals(configValue)) return entityName;
        }
        return null;
    }

    private void givePlayerSkull(Player killer, Player killed) {

        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();

        itemMeta.setDisplayName(plugin.getConfig().getString("Player.DisplayName").replace("%killed%", killed.getName()));
        itemMeta.setOwningPlayer(killed);
        itemStack.setItemMeta(itemMeta);

        killer.getInventory().addItem(itemStack);
        String dropPlayerHead = plugin.getConfig().getString("Message.DropPlayerHead").replace("%player%", killer.getName()).replace("%target%", killed.getName());

        Message.sendAllPlayers(dropPlayerHead);
        sendTitle(killer);
    }

    private void sendTitle(Player player) {
        if (Bukkit.getVersion().contains("Paper")) {
            String title = Message.toColor(plugin.getConfig().getString("Message.Title"));
            String subTitle = Message.toColor(plugin.getConfig().getString("Message.SubTitle"));

            player.sendTitle(new Title(title, subTitle,20,20,20));
        }

    }

}
