package dev.tbm00.spigot.command64.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import dev.tbm00.spigot.command64.ConfigHandler;
import dev.tbm00.spigot.command64.CommandRunner;
import dev.tbm00.spigot.command64.model.CustomCmdEntry;
import dev.tbm00.spigot.command64.model.ItemCmdEntry;
import dev.tbm00.spigot.command64.model.RewardCmdEntry;
import dev.tbm00.spigot.command64.reward.QueueManager;
import net.md_5.bungee.api.chat.TextComponent;

public class CmdCommand implements TabExecutor {
    private final JavaPlugin javaPlugin;
    private final CommandRunner cmdRunner;
    private final ConfigHandler configHandler;
    private final QueueManager queueManager;
    private final String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "cmd" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;

    public CmdCommand(JavaPlugin javaPlugin, CommandRunner cmdRunner, ConfigHandler configHandler, QueueManager queueManager) {
        this.javaPlugin = javaPlugin;
        this.cmdRunner = cmdRunner;
        this.configHandler = configHandler;
        this.queueManager = queueManager;
    }

    public boolean onCommand(CommandSender sender, Command consoleCommand, String label, String[] args) {
        if (sender.hasPermission("command64.help") && args.length == 0) {
            handleHelpCommand(sender);
            return true;
        }

        if (args.length < 1 || args.length > 4) return false;

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "help":
                if (sender.hasPermission("command64.help"))
                    return handleHelpCommand(sender);
                break;
            case "give":
                if (configHandler.isItemEnabled() && args.length >= 2)
                    return handleGiveCommand(sender, args);
                break;
            case "reward":
                if (configHandler.isRewardsEnabled() && sender.hasPermission("command64.enqueuerewards") && args.length > 2)
                    return handleQueueCommand(sender, args);
                break;
            default:
                if (configHandler.isCustomEnabled())
                    return handleCustomCommand(sender, args);
                break;
        }
        return false;
    }

    private boolean handleHelpCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "--- " + ChatColor.RED + "Command64 Admin Commands" + ChatColor.DARK_RED + " ---\n"
            + ChatColor.WHITE + "/cmd help" + ChatColor.GRAY + " Display this command list\n"
            + ChatColor.WHITE + "/cmd give <itemKey> [player]" + ChatColor.GRAY + " Spawn a custom item\n"
            + ChatColor.WHITE + "/cmd <customCommand> [argument]" + ChatColor.GRAY + " Run custom command as Console w/ optional argument\n"
            + ChatColor.WHITE + "/cmd -d <tickDelay> <customCommand> [argument]" + ChatColor.GRAY + " Run delayed custom command as Console w/ optional argument\n"
            + ChatColor.WHITE + "/cmd reward <rewardName> <player> [argument]" + ChatColor.GRAY + " Add reward command to a player's reward queue w/ optional argument\n"
            );
        return true;
    }
    
    private boolean handleQueueCommand(CommandSender sender, String[] args) {
        for (RewardCmdEntry entry : configHandler.getRewardCmdEntries()) {
            if (args[1].equalsIgnoreCase(entry.getName())) {
                String playerName = args[2];
                String argument = (args[3]!=null && !args[3].isBlank()) ? args[3] : null;
                if (queueManager.enqueueReward(playerName, entry.getName(), argument)) {
                    if (argument==null) {
                        sender.sendMessage(prefix + ChatColor.GREEN + "You enqueued the " + entry.getName() + " reward for " + playerName);
                        javaPlugin.getLogger().info(sender.getName() + " has enqueued the " + entry.getName() + " reward for " + playerName);
                    } else {
                        sender.sendMessage(prefix + ChatColor.GREEN + "You enqueued the " + entry.getName() +":"+ argument + " reward for " + playerName);
                        javaPlugin.getLogger().info(sender.getName() + " has enqueued the " + entry.getName() +":"+ argument + " reward for " + playerName);
                    }
                    
                    Player player = javaPlugin.getServer().getPlayer(playerName);
                    if (player != null && player.isOnline()) {
                        player.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.getNewRewardMessage())));
                    }
                    
                    return true;
                } else return false;
            }
        }
        return false;
    }

    private boolean handleCustomCommand(CommandSender sender, String[] args) {
        for (CustomCmdEntry entry : configHandler.getCustomCmdEntries()) {
            if (sender.hasPermission(entry.getPerm()) != entry.getPermValue()) 
                continue;
            else if (args[0].equalsIgnoreCase("-d") && args[2].equalsIgnoreCase(entry.getPlayerCommand())) {
                if (cmdRunner.runDelayedCommand(entry.getConsoleCommands(), sender, entry, args))
                    return true;
                else return false;
            } else if (args[0].equalsIgnoreCase(entry.getPlayerCommand())) {
                String argument = args.length > 1 ? args[1] : null;
                if (cmdRunner.runCustomCommand(entry.getConsoleCommands(), sender, argument))
                    return true;
                else return false;
            }
        }
        return false;
    }
    
    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        Player player;
    
        if (args.length < 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + ChatColor.RED + "This command can only be run by a player!");
                return false;
            }
            player = (Player) sender;
        } else {
            player = javaPlugin.getServer().getPlayer(args[2]);
            if (player == null) {
                sender.sendMessage(prefix + ChatColor.RED + "Could not find target player!");
                return false;
            }
        }
    
        for (ItemCmdEntry entry : configHandler.getItemCmdEntries()) {
            if (args[1].equals(entry.getKeyString()))
                if ((sender.hasPermission(entry.getGivePerm()) == entry.getGivePermValue()) || sender instanceof ConsoleCommandSender) {
                    giveItemToPlayer(player, entry);
                    return true;
                }
        }
        return false;
    }

    private void giveItemToPlayer(Player player, ItemCmdEntry entry) {
        ItemStack item = new ItemStack(Material.valueOf(entry.getItem()));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', entry.getName()));
            meta.setLore(entry.getLore().stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
            if (entry.getGlowing()) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addEnchant(org.bukkit.enchantments.Enchantment.ARROW_DAMAGE, 1, true);
            }
            meta.getPersistentDataContainer().set(new NamespacedKey(javaPlugin, entry.getKeyString()), PersistentDataType.STRING, "true");
            item.setItemMeta(meta);
        }
        player.getInventory().addItem(item);
        player.sendMessage(prefix + ChatColor.GREEN + "You have been given the " + entry.getKeyString());
        javaPlugin.getLogger().info(player.getName() + " has been given the " + entry.getKeyString());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command consoleCommand, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        
        if (args.length == 1) {
            list.clear();
            for (CustomCmdEntry n : configHandler.getCustomCmdEntries()) {
                if (n != null && sender.hasPermission(n.getPerm()) && n.getPlayerCommand().startsWith(args[0])) {
                    list.add(n.getPlayerCommand());
                }
            }
            
            int i = 0;
            for (ItemCmdEntry n : configHandler.getItemCmdEntries()) {
                if (n != null && sender.hasPermission(n.getGivePerm()) && "give".startsWith(args[0])) {
                    i = i + 1;
                }
            } if (i >= 1) list.add("give");
            
            int j = 0;
            for (CustomCmdEntry m : configHandler.getCustomCmdEntries()) {
                if (m != null && sender.hasPermission(m.getPerm()) && "-d".startsWith(args[0])) {
                    j = j + 1;
                }
            } if (j >= 1) list.add("-d");
            
            if (sender.hasPermission("command64.help")) {
                list.add("help");
            }
            if (sender.hasPermission("command64.enqueuerewards")) {
                list.add("reward");
            }
        }
        
        if (args.length == 2) {
            list.clear();
            if (args[0].toString().equals("give")) {
                for (ItemCmdEntry n : configHandler.getItemCmdEntries()) {
                    if (n != null && sender.hasPermission(n.getGivePerm()) && n.getKeyString().startsWith(args[1])) {
                        list.add(n.getKeyString());
                    }
                }
            } else if (args[0].toString().equals("reward") && sender.hasPermission("enqueuerewards")) {
                    for (RewardCmdEntry n : configHandler.getRewardCmdEntries()) {
                        if (n != null && n.getName().startsWith(args[1])) {
                            list.add(n.getName());
                        }
                    }
            } else if (args[0].toString().equals("-d")) {
                list.add("<#>");
            } else if (!args[0].toString().equals("help")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                }
            }
        }
        
        if (args.length == 3) {
            list.clear();
            if (args[0].toString().equals("-d")) {
                for (CustomCmdEntry n : configHandler.getCustomCmdEntries()) {
                    if (n != null && sender.hasPermission(n.getPerm()) && n.getPlayerCommand().startsWith(args[2])) {
                        list.add(n.getPlayerCommand());
                    }
                }
            } else if (args[0].toString().equals("give") || args[0].toString().equals("reward")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                }
            }
        }
        
        if (args.length == 4) {
            list.clear();
            if (args[0].toString().equals("-d")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                }
            }
        }
        
        return list;
    }
}
