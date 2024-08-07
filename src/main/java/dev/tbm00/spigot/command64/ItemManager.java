package dev.tbm00.spigot.command64;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.NamespacedKey;

import dev.tbm00.spigot.command64.model.ItemCmdEntry;

public class ItemManager {
    private final List<ItemCmdEntry> itemCmdEntries;
    private final Boolean enabled;

    public ItemManager(JavaPlugin javaPlugin) {
        this.itemCmdEntries = null;

        // Load Item Commands from config.yml
        ConfigurationSection itemCmdSection = javaPlugin.getConfig().getConfigurationSection("itemCommandEntries");
        if (itemCmdSection != null && itemCmdSection.getBoolean("enabled")) {
            this.enabled = true;
            for (String key : itemCmdSection.getKeys(false)) {
                ConfigurationSection itemCmdEntry = itemCmdSection.getConfigurationSection(key);
                
                if (itemCmdEntry != null && itemCmdEntry.getBoolean("enabled")) {
                    String givePerm = itemCmdEntry.getString("givePerm");
                    Boolean givePermValue = itemCmdEntry.getBoolean("givePermValue");
                    String usePerm = itemCmdEntry.getString("usePerm");
                    Boolean usePermValue = itemCmdEntry.getBoolean("usePermValue");
                    List<String> consoleCommands = itemCmdEntry.getStringList("consoleCommands");
                    String KEY = itemCmdEntry.getString("key");
                    String name = itemCmdEntry.getString("name");
                    String item = itemCmdEntry.getString("item");
                    Boolean glowing = itemCmdEntry.getBoolean("glowing");
                    List<String> lore = itemCmdEntry.getStringList("lore");
                    
                    if (usePerm != null && givePerm != null && consoleCommands != null && key != null && !consoleCommands.isEmpty()) {
                        ItemCmdEntry entry = new ItemCmdEntry(javaPlugin, givePerm, givePermValue, usePerm, usePermValue, consoleCommands, KEY, name, item, glowing, lore);
                        this.itemCmdEntries.add(entry);
                        System.out.println("Loaded itemCmdEntry: " + name + " " + key + " " + usePerm + " " + usePermValue + " " + consoleCommands);
                    } else {
                        System.out.println("Error: Poorly defined itemCmdEntry: " + name + " " + key + " " + usePerm + " " + usePermValue + " " + consoleCommands);
                    }
                }
            }
        } else this.enabled = false;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public List<ItemCmdEntry> getItemCmdEntries() {
        return itemCmdEntries;
    }
}
