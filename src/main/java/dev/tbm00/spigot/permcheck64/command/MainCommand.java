package dev.tbm00.spigot.permcheck64.command;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class MainCommand implements TabExecutor {
    private JavaPlugin javaPlugin;
    private final String[] subCommands = new String[]{"reload"};

    public MainCommand(JavaPlugin javaPlugin ) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = javaPlugin.getConfig().getString("prefix").replace("&", "§");

        if (!sender.hasPermission("permcheck64.reload")) {
            sender.sendMessage(prefix + ChatColor.RED + "No permission!");
            return false;
        }
        
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(prefix + ChatColor.RED + "Unknown subcommand!");
            return false;
        }

        javaPlugin.reloadConfig();
        prefix = javaPlugin.getConfig().getString("prefix").replace("&", "§");
        
        sender.sendMessage(prefix + ChatColor.GREEN + "Configuration reloaded!");
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("permcheck64.reload")) return null;
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.clear();
            for (String n : subCommands) {
                if (n!=null && n.startsWith(args[0])) {
                    list.add(n);
                }
            }
        }
        return list;
    }
}
