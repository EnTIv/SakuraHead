package com.entiv.sakurahead;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()) return true;

        Main plugin = Main.getInstance();

        if (args.length == 1 && args[0].equals("reload")) {

            plugin.reloadConfig();
            Message.send(sender, "&9&l樱花头颅&6&l >> &a插件重载成功!");

        } else if (args.length >= 3 && args[0].equals("give")) {

            Player player = Bukkit.getPlayer(args[1]);
            Skull skull = plugin.getSkull(args[2]);

            if (player == null) {
                Message.send(sender, "&9&l樱花头颅&6&l >> &c玩家 &b&l" + args[1] + "&c 当前不在线");
                return true;
            }

            if (skull == null) {
                Message.send(sender, "&9&l樱花头颅&6&l >> &c生物类型 &b&l" + args[2] + "&c 不存在, 请检查配置文件, 注意大小写");
                return true;
            }

            try {

                ItemStack itemStack = skull.getItemStack(Integer.parseInt(args[3]));
                player.getInventory().addItem(itemStack);

            } catch (Exception e) {

                ItemStack itemStack = skull.getItemStack();
                player.getInventory().addItem(itemStack);

            } finally {
                Message.send(sender, "&9&l樱花头颅&6&l >> &a成功将头颅 &b&l" + args[2] + "&a 给予玩家 &b&l" + player.getName() + "");
            }


        } else {

            List<String> message = new ArrayList<>();

            message.add("&6&m+------------------+&9&l 樱花头颅 &6&m+------------------+");
            message.add("");
            message.add("&d/head reload &e重载配置文件");
            message.add("&d/head give 玩家 头颅 数量 &e给予玩家指定数量的头颅");
            message.add("");

            Message.send(sender, message);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {

            List<String> tabComplete = new ArrayList<>();

            tabComplete.add("reload");
            tabComplete.add("give");

            tabComplete.removeIf(s -> !s.startsWith(args[0].toLowerCase()));

            return tabComplete;

        } else if (args.length == 3) {

            Main plugin = Main.getInstance();

            List<String> tabComplete = new ArrayList<>(plugin.getConfigurationSection().getKeys(false));

            if (!args[args.length - 1].trim().isEmpty()) {
                String match = args[args.length - 1].trim();
                tabComplete.removeIf(name -> !name.startsWith(match));
            }

            return tabComplete;

        } else if (args.length == 4) {
            return Arrays.asList("1", "2", "3");
        }
        return null;
    }
}
