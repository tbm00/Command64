package dev.tbm00.spigot.command64.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import dev.tbm00.spigot.command64.ItemManager;
import dev.tbm00.spigot.command64.CommandRunner;
import dev.tbm00.spigot.command64.model.CustomCmdEntry;
import dev.tbm00.spigot.command64.model.ItemCmdEntry;
import dev.tbm00.spigot.command64.model.TimerCmdEntry;

public class CmdCommand implements TabExecutor {
    private final JavaPlugin javaPlugin;
    private final CommandRunner cmdRunner;
    private final ItemManager itemManager;
    private final List<CustomCmdEntry> customCmdEntries;
    private final List<TimerCmdEntry> timerCmdEntries;
    private final List<ItemCmdEntry> itemCmdEntries;
    private final Boolean customEnabled;
    private final Boolean timerEnabled;

    public CmdCommand(JavaPlugin javaPlugin, CommandRunner cmdRunner, ItemManager itemManager) {
        this.javaPlugin = javaPlugin;
        this.cmdRunner = cmdRunner;
        this.itemManager = itemManager;
        this.customCmdEntries = new ArrayList<>();
        this.timerCmdEntries = new ArrayList<>();
        this.itemCmdEntries = itemManager.getItemCmdEntries();

        if (!loadCustomConfig()) customEnabled = false;
        else customEnabled = true;

        if (!loadTimerConfig()) timerEnabled = false;
        else timerEnabled = true;
    }

    private boolean loadCustomConfig() {
        ConfigurationSection customCmdSection = javaPlugin.getConfig().getConfigurationSection("customCommandEntries");
        if (customCmdSection == null || !customCmdSection.getBoolean("enabled")) return false;

        for (String key : customCmdSection.getKeys(false)) {
            ConfigurationSection customCmdEntry = customCmdSection.getConfigurationSection(key);
            if (customCmdEntry == null || !customCmdEntry.getBoolean("enabled")) continue;
            
            String usePerm = customCmdEntry.getString("usePerm");
            Boolean usePermValue = customCmdEntry.getBoolean("usePermValue");
            String playerCommand = customCmdEntry.getString("customCommand");
            List<String> consoleCommands = customCmdEntry.getStringList("consoleCommands");

            if (usePerm != null && consoleCommands != null && playerCommand != null && !consoleCommands.isEmpty()) {
                CustomCmdEntry entry = new CustomCmdEntry(usePerm, usePermValue, playerCommand, consoleCommands);
                customCmdEntries.add(entry);
                javaPlugin.getLogger().info("Loaded customCmdEntry: "+ usePerm + " " + usePermValue + " " + playerCommand);
            } else 
                javaPlugin.getLogger().warning("Error: Poorly defined customCmdEntry: " + usePerm + " " + usePermValue + " " + playerCommand);
        } return true;
    }

    private boolean loadTimerConfig() {
        ConfigurationSection timerCmdSection = javaPlugin.getConfig().getConfigurationSection("timerCommandEntries");
        if (timerCmdSection == null || !timerCmdSection.getBoolean("enabled")) return false;

        for (String key : timerCmdSection.getKeys(false)) {
            ConfigurationSection timerCmdEntry = timerCmdSection.getConfigurationSection(key);
            if (timerCmdEntry == null || !timerCmdEntry.getBoolean("enabled")) continue;
            
            String usePerm = timerCmdEntry.getString("usePerm");
            Boolean usePermValue = timerCmdEntry.getBoolean("usePermValue");
            String playerCommand = timerCmdEntry.getString("timerCommand");
            List<String> consoleCommands = timerCmdEntry.getStringList("consoleCommands");

            if (usePerm != null && consoleCommands != null && playerCommand != null && !consoleCommands.isEmpty()) {
                TimerCmdEntry entry = new TimerCmdEntry(usePerm, usePermValue, playerCommand, consoleCommands);
                timerCmdEntries.add(entry);
                javaPlugin.getLogger().info("Loaded timerCmdEntry: "+ usePerm + " " + usePermValue + " " + playerCommand);
            } else 
                javaPlugin.getLogger().warning("Error: Poorly defined timerCmdEntry: " + usePerm + " " + usePermValue + " " + playerCommand);
        } return true;
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
                if (itemManager.isEnabled() && args.length >= 2)
                    return handleGiveCommand(sender, args);
                break;
            case "timer":
                if (timerEnabled && args.length >= 3)
                    return handleTimerCommand(sender, args);
                break;
            default:
                if (customEnabled)
                    return handleCustomCommand(sender, args);
                break;
        }
        return false;
    }

    private boolean handleHelpCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "--- " + ChatColor.RED + "Command64 Admin Commands" + ChatColor.DARK_RED + " ---\n"
            + ChatColor.WHITE + "/cmd help" + ChatColor.GRAY + " Display this command list\n"
            + ChatColor.WHITE + "/cmd give <itemKey> [player]" + ChatColor.GRAY + " Spawn a custom item\n"
            + ChatColor.WHITE + "/cmd timer <tickDelay> <timerCommand> [argument]" + ChatColor.GRAY + " Run delayed command as Console w/ optional argument\n"
            + ChatColor.WHITE + "/cmd <customCommand> [argument]" + ChatColor.GRAY + " Run custom command as Console w/ optional argument\n"
            );
        return true;
    }
    
    private boolean handleCustomCommand(CommandSender sender, String[] args) {
        for (CustomCmdEntry entry : customCmdEntries)
            if (args[0].toLowerCase().equals(entry.getPlayerCommand())) {
                if (sender.hasPermission(entry.getPerm()) != entry.getPermValue()) 
                    continue;
                String argument = args.length > 1 ? args[1] : null;
                if (cmdRunner.runCustomCommand(entry.getConsoleCommands(), sender, argument))
                    return true;
                else
                    javaPlugin.getLogger().warning("Error: 'consoleCommands' is null or empty for customCmdEntry: " + entry.toString());
            }
        return false;
    }

    private boolean handleTimerCommand(CommandSender sender, String[] args) {
        for (TimerCmdEntry entry : timerCmdEntries) {
            if ((!sender.hasPermission(entry.getPerm()) != entry.getPermValue()) 
                || !args[2].equals(entry.getPlayerCommand())) 
                continue;
            if (cmdRunner.runTimerCommand(entry.getConsoleCommands(), sender, entry, args))
                return true;
            else 
                javaPlugin.getLogger().warning("Error: 'consoleCommands' is null or empty for timerCmdEntry: " + entry.toString());
        }
        return false;
    }

    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        Player player;
    
        if (args.length < 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player!");
                return false;
            }
            player = (Player) sender;
        } else {
            player = javaPlugin.getServer().getPlayer(args[2]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Could not find target player!");
                return false;
            }
        }
    
        for (ItemCmdEntry entry : itemCmdEntries) {
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
        player.sendMessage(ChatColor.GREEN + "You have been given the " + entry.getKeyString());
        javaPlugin.getLogger().info(player.getDisplayName() + " has been given the " + entry.getKeyString());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command consoleCommand, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.clear();
            for (CustomCmdEntry n : customCmdEntries) {
                if (n!=null && sender.hasPermission(n.getPerm()) && n.getPlayerCommand().startsWith(args[0]))
                    list.add(n.getPlayerCommand());
            }
            int i = 0;
            for (ItemCmdEntry n : itemCmdEntries) {
                if (n!=null && sender.hasPermission(n.getGivePerm()) && "give".startsWith(args[0]))
                    i = i+1;
            }
            if (i>=1) list.add("give");
            int j = 0;
            for (TimerCmdEntry m : timerCmdEntries) {
                if (m!=null && sender.hasPermission(m.getPerm()) && "timer".startsWith(args[0]))
                    j = j+1;
            }
            if (j>=1) list.add("timer");
            if (sender.hasPermission("command64.help")) list.add("help");
        }
        if (args.length == 2) {
            list.clear();
            if (args[0].toString().equals("give"))
                for (ItemCmdEntry n : itemCmdEntries)
                    if (n!=null && sender.hasPermission(n.getGivePerm()) && n.getKeyString().startsWith(args[1]))
                        list.add(n.getKeyString());
            else if (args[0].toString().equals("timer"))
                list.add("<#>");
            else if (!args[0].toString().equals("help"))
                for (Player p : Bukkit.getOnlinePlayers())
                    list.add(p.getName());
        }
        if (args.length == 3) {
            list.clear();
            if (args[0].toString().equals("timer")) 
                for (TimerCmdEntry n : timerCmdEntries) 
                    if (n!=null && sender.hasPermission(n.getPerm()) && n.getPlayerCommand().startsWith(args[2]))
                        list.add(n.getPlayerCommand());
             else if (args[0].toString().equals("give"))
                for (Player p : Bukkit.getOnlinePlayers())
                    list.add(p.getName());
        }
        if (args.length == 4) {
            list.clear();
            if (args[0].toString().equals("timer")) 
                for (Player p : Bukkit.getOnlinePlayers()) 
                    list.add(p.getName());
        }
        return list;
    }
}
