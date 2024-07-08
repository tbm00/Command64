package dev.tbm00.spigot.blankplugin.command;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class BlankCommand implements TabExecutor {
    private final String prefix;
    private final String[] subCommands = new String[]{"blankSub"};

    public BlankCommand(FileConfiguration fileConfig) {
        this.prefix = ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + fileConfig.getString("lang.prefix") + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("blankplugin.usecommand")) {
            sender.sendMessage(prefix + ChatColor.RED + "No permission!");
            return false;
        }
        

        if (args.length == 0) {
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "blankSub":
                return handleSubCommand(sender, args);
            default:
                sender.sendMessage(prefix + ChatColor.RED + "Unknown subcommand!");
                return false;
        }
    }

    private boolean handleSubCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.sendMessage(ChatColor.GREEN + "<Blank message>");
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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
