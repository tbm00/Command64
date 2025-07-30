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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import net.md_5.bungee.api.chat.TextComponent;

import dev.tbm00.spigot.command64.Command64;
import dev.tbm00.spigot.command64.ConfigHandler;
import dev.tbm00.spigot.command64.CommandRunner;
import dev.tbm00.spigot.command64.model.CustomCmdEntry;
import dev.tbm00.spigot.command64.model.ItemCmdEntry;
import dev.tbm00.spigot.command64.model.RewardCmdEntry;
import dev.tbm00.spigot.command64.reward.QueueManager;

public class CmdCommand implements TabExecutor {
    private final Command64 javaPlugin;
    private final CommandRunner cmdRunner;
    private final ConfigHandler configHandler;
    private final QueueManager queueManager;
    private final String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "-" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;

    public CmdCommand(Command64 javaPlugin, CommandRunner cmdRunner, ConfigHandler configHandler, QueueManager queueManager) {
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

        if (args.length < 1 || args.length > 5) return false;

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
            case "sudo":
                if (sender.hasPermission("command64.sudo.console") || sender.hasPermission("command64.sudo.player"))
                    return cmdRunner.triggerSudoCommand(sender, args);
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
            + ChatColor.WHITE + "/cmd help" + ChatColor.GRAY + " Display the admin command list\n"
            + ChatColor.WHITE + "/cmd sudo <player>/RANDOM_PLAYER/CONSOLE <cmd>" + ChatColor.GRAY + " Run command as someone else (plus signs convert to spaces in cmd)\n"
            + ChatColor.WHITE + "/cmd give <itemKey> [player]/RANDOM_PLAYER [amount]" + ChatColor.GRAY + " Spawn custom item(s)\n"
            + ChatColor.WHITE + "/cmd <customCommand> [argument]/RANDOM_PLAYER [argument2]" + ChatColor.GRAY + " Run custom command w/ optional argument(s)\n"
            + ChatColor.WHITE + "/cmd -d <tickDelay> <customCommand> [argument]/RANDOM_PLAYER [argument2]" + ChatColor.GRAY + " Schedule delayed custom command w/ optional argument(s)\n"
            + ChatColor.WHITE + "/cmd reward <rewardName> <player>/RANDOM_PLAYER [argument]" + ChatColor.GRAY + " Add reward to a player's queue w/ optional argument"
            );
        return true;
    }
    
    private boolean handleQueueCommand(CommandSender sender, String[] args) {
        for (RewardCmdEntry entry : configHandler.getRewardCmdEntries()) {
            if (args[1].equalsIgnoreCase(entry.getName())) {
                String targetName = args[2];
                if (targetName.equalsIgnoreCase("RANDOM_PLAYER")) {
                    targetName = javaPlugin.getRandomPlayerName(sender.getName());
                }
                String argument = null;
                if (args.length>3) argument = args[3];
                if (queueManager.enqueueReward(targetName, entry.getName(), argument)) {
                    if (argument==null) {
                        sender.sendMessage(prefix + ChatColor.GREEN + "You enqueued the " + entry.getName() + " reward for " + targetName);
                        javaPlugin.getLogger().info(sender.getName() + " has enqueued the " + entry.getName() + " reward for " + targetName);
                    } else {
                        sender.sendMessage(prefix + ChatColor.GREEN + "You enqueued the " + entry.getName() +":"+ argument + " reward for " + targetName);
                        javaPlugin.getLogger().info(sender.getName() + " has enqueued the " + entry.getName() +":"+ argument + " reward for " + targetName);
                    }
                    
                    Player target = javaPlugin.getServer().getPlayer(targetName);
                    if (target != null && target.isOnline()) {
                        target.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.getNewRewardMessage())));
                    }
                    
                    return true;
                } else return false;
            }
        }
        return false;
    }

    private boolean handleCustomCommand(CommandSender sender, String[] args) {
        if (args.length > 1 && args[1].equalsIgnoreCase("RANDOM_PLAYER")) {
            args[1] = javaPlugin.getRandomPlayerName(sender.getName());
        }
        for (CustomCmdEntry entry : configHandler.getCustomCmdEntries()) {
            if (sender.hasPermission(entry.getPerm()) != entry.getPermValue()) 
                continue;
            else if (args[0].equalsIgnoreCase("-d") && args[2].equalsIgnoreCase(entry.getPlayerCommand())) {
                if (cmdRunner.triggerDelayedCmdEntry(entry, sender, args))
                    return true;
                sender.sendMessage(prefix + ChatColor.RED + "Delayed custom command entry failed!");
                return false;
            } else if (args[0].equalsIgnoreCase(entry.getPlayerCommand())) {
                if (cmdRunner.triggerCustomCmdEntry(entry, sender, args))
                    return true;
                sender.sendMessage(prefix + ChatColor.RED + "Custom command entry failed!");
                return false;
            }
        }

        sender.sendMessage(prefix + ChatColor.RED + "Command failed!");
        return false;
    }
    
    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        Player player;
        int quantity = 1;
    
        if (args.length < 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + ChatColor.RED + "This command can only be run by a player!");
                return false;
            }

            player = (Player) sender;
        } else {
            if (args.length > 1 && args[1].equalsIgnoreCase("RANDOM_PLAYER")) {
                args[2] = javaPlugin.getRandomPlayerName(sender.getName());
            }
            player = javaPlugin.getServer().getPlayer(args[2]);
            if (player == null) {
                sender.sendMessage(prefix + ChatColor.RED + "Could not find target player!");
                return false;
            }
            if (args.length==4) {
                try {
                    quantity = Integer.parseInt(args[3]);
                } catch (Exception e) {
                    sender.sendMessage(prefix + ChatColor.RED + "Quantity must an integer!");
                    return false;
                }
            }
        }
    
        for (ItemCmdEntry entry : configHandler.getItemCmdEntries()) {
            if (args[1].equals(entry.getKeyString()))
                if ((sender.hasPermission(entry.getGivePerm()) == entry.getGivePermValue()) || sender instanceof ConsoleCommandSender) {
                    giveItemToPlayer(player, entry, quantity);
                    return true;
                }
        }
        return false;
    }

    private void giveItemToPlayer(Player player, ItemCmdEntry entry, int quantity) {
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
        item.setAmount(quantity);
        giveItem(player, item, entry);
        javaPlugin.getLogger().info(player.getName() + " has been given " + quantity + " " + entry.getKeyString());
    }

    /**
     * Gives a player an ItemStack.
     * If they have a full inv, it drops on the ground.
     */
    private void giveItem(Player player, ItemStack item, ItemCmdEntry entry) {
        boolean inInv = true;
        
        if ((player.getInventory().firstEmpty() == -1)) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            inInv = false;
        } else {
            player.getInventory().addItem(item);
        }

        if (inInv) {
            if (item.getAmount()==1) 
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix  + "You received a &a" + entry.getName() + "&f!")));
            else
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix  + "You received &l" + item.getAmount() + " &r&a" + entry.getName() + "s &f!")));
        } else {
            if (item.getAmount()==1) 
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix  + "You received a &a" + entry.getName() + "&f... &cBut your inventory was full so it dropped on the ground!")));
            else
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix  + "You received &l" + item.getAmount() + " &r&a" + entry.getName() + "&fs... &cBut your inventory was full so they dropped on the ground!")));
        }
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
            if (sender.hasPermission("command64.sudo.players")||sender.hasPermission("command64.sudo.console")) {
                list.add("sudo");
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
                if (args[0].toString().equals("sudo")) {
                    if ("CONSOLE".startsWith(args[1])||"console".startsWith(args[1])) list.add("CONSOLE");
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().startsWith(args[1])) list.add(p.getName());
                }
                if ("RANDOM_PLAYER".startsWith(args[1])||"random_player".startsWith(args[1])) list.add("RANDOM_PLAYER");
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
                    if (p.getName().startsWith(args[2])) list.add(p.getName());
                }
                if ("RANDOM_PLAYER".startsWith(args[1])||"random_player".startsWith(args[1])) list.add("RANDOM_PLAYER");
            }
        }
        
        if (args.length == 4) {
            list.clear();
            if (args[0].toString().equals("-d")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().startsWith(args[3])) list.add(p.getName());
                }
                if ("RANDOM_PLAYER".startsWith(args[1])||"random_player".startsWith(args[1])) list.add("RANDOM_PLAYER");
            }
        }
        
        return list;
    }
}
