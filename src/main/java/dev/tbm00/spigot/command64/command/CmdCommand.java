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
import dev.tbm00.spigot.command64.model.CustomCmdEntry;
import dev.tbm00.spigot.command64.model.ItemCmdEntry;

public class CmdCommand implements TabExecutor {
    private final JavaPlugin javaPlugin;
    private final ConsoleCommandSender console;
    private final ItemManager itemManager;
    private final List<CustomCmdEntry> customCmdEntries;
    private final List<ItemCmdEntry> itemCmdEntries;
    private final Boolean customEnabled;

    public CmdCommand(JavaPlugin javaPlugin, ItemManager itemManager) {
        this.javaPlugin = javaPlugin;
        this.console = Bukkit.getServer().getConsoleSender();
        this.itemManager = itemManager;
        this.customCmdEntries = new ArrayList<>();
        this.itemCmdEntries = itemManager.getItemCmdEntries();

        // Load Custom Commands from config.yml
        ConfigurationSection customCmdSection = javaPlugin.getConfig().getConfigurationSection("customCommandEntries");
        if (customCmdSection != null && customCmdSection.getBoolean("enabled")) {
            this.customEnabled = true;
            for (String key : customCmdSection.getKeys(false)) {
                ConfigurationSection customCmdEntry = customCmdSection.getConfigurationSection(key);
                
                if (customCmdEntry != null && customCmdEntry.getBoolean("enabled")) {
                    String usePerm = customCmdEntry.getString("usePerm");
                    Boolean usePermValue = customCmdEntry.getBoolean("usePermValue");
                    String playerCommand = customCmdEntry.getString("customCommand");
                    List<String> consoleCommands = customCmdEntry.getStringList("consoleCommands");

                    if (usePerm != null && consoleCommands != null && playerCommand != null && !consoleCommands.isEmpty()) {
                        CustomCmdEntry entry = new CustomCmdEntry(usePerm, usePermValue, playerCommand, consoleCommands);
                        this.customCmdEntries.add(entry);
                        System.out.println("Loaded customCmdEntry: "+ usePerm + " " + usePermValue + " " + playerCommand);
                    } else {
                        System.out.println("Error: Poorly defined customCmdEntry: " + usePerm + " " + usePermValue+ " " + playerCommand);
                    }
                }
            }
        } else this.customEnabled = false;
    }

    public boolean onCommand(CommandSender sender, Command consoleCommand, String label, String[] args) {
        if (sender.hasPermission("command64.help") && args.length == 0) {
            showHelp(sender);
            return true;
        }

        if (args.length != 1 && args.length != 2) return false;

        String subCommand = args[0].toLowerCase();
        String argument = null;
        if (args.length == 2) argument = args[1];

        if (sender.hasPermission("command64.help") && subCommand.equals("help")) {
            showHelp(sender);
            return true;
        }

        // Run a Custom Command
        if (customEnabled) {
            for (CustomCmdEntry entry : customCmdEntries) {
                if (subCommand.equals(entry.getPlayerCommand())) {
                    List<String> consoleCommands = entry.getConsoleCommands();
                    if (consoleCommands != null && !consoleCommands.isEmpty()) {
                        for (String consoleCmd : consoleCommands) {
                            consoleCmd = consoleCmd.replace("<player>", sender.getName());
                            if (args.length == 2) consoleCmd = consoleCmd.replace("<argument>", argument);
                            if (sender.hasPermission(entry.getPerm()) == entry.getPermValue()) {
                                System.out.println("Running customCmdEntry: " + consoleCmd);
                                Bukkit.dispatchCommand(console, consoleCmd);
                                sender.sendMessage(ChatColor.GREEN + "Ran command: " + consoleCmd);
                            }
                        }
                        return true;
                    } else {
                        System.out.println("Error: 'consoleCommands' is null or empty for customCmdEntry: " + entry.getPerm() + " " + entry.getPermValue());
                        return false;
                    }
                }
            }
        }

        // Run a give Item Command
        if (itemManager.isEnabled() && subCommand.equals("give") && argument != null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player!");
                return false;
            }
            for (ItemCmdEntry entry : itemCmdEntries) {
                if (argument.equals(entry.getKeyString())) {
                    Player player = (Player) sender;
                    if (player.hasPermission(entry.getGivePerm()) == entry.getGivePermValue()) {
                        // Create the item
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
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command consoleCommand, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.clear();
            for (CustomCmdEntry n : customCmdEntries) {
                if (n!=null && sender.hasPermission(n.getPerm()) && n.getPlayerCommand().startsWith(args[0])) {
                    list.add(n.getPlayerCommand());
                }
            }
            int i = 0;
            for (ItemCmdEntry n : itemCmdEntries) {
                if (n!=null && sender.hasPermission(n.getGivePerm()) && n.getKeyString().startsWith(args[0])) {
                    i = i+1;
                }
            }
            if (i>=1) list.add("give");
        }
        if (args.length == 2) {
            list.clear();
            if (args[0].toString().equals("give")) {
                for (ItemCmdEntry n : itemCmdEntries) {
                    if (n!=null && sender.hasPermission(n.getGivePerm()) && n.getKeyString().startsWith(args[1])) {
                        list.add(n.getKeyString());
                    }
                }
            } else if (!args[0].toString().equals("help")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                }
            }
        }
        return list;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "--- " + ChatColor.RED + "Command64 Admin Commands" + ChatColor.DARK_RED + " ---\n"
            + ChatColor.WHITE + "/cmd help" + ChatColor.GRAY + " Display this command list\n"
            + ChatColor.WHITE + "/cmd give <itemKey>" + ChatColor.GRAY + " Spawn in a custom <item>\n"
            + ChatColor.WHITE + "/cmd <customCommand> [argument]" + ChatColor.GRAY + " Run custom command as Console w/ optional argument\n"
            );
    }
}
