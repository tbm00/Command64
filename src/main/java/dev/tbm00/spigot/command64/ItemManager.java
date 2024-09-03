package dev.tbm00.spigot.command64;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.model.ItemCmdEntry;

public class ItemManager {
    private final JavaPlugin javaPlugin;
    private final List<ItemCmdEntry> itemCmdEntries;
    private final Boolean enabled;

    public ItemManager(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.itemCmdEntries = new ArrayList<>();

        if (!loadItemConfig()) enabled = false;
        else enabled = true;
    }

    public boolean loadItemConfig() {
        ConfigurationSection itemCmdSection = javaPlugin.getConfig().getConfigurationSection("itemCommandEntries");
        if (itemCmdSection == null || !itemCmdSection.getBoolean("enabled")) return false;

        for (String key : itemCmdSection.getKeys(false)) {
            ConfigurationSection itemCmdEntry = itemCmdSection.getConfigurationSection(key);
            if (itemCmdEntry == null || !itemCmdEntry.getBoolean("enabled")) continue;
            
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
                itemCmdEntries.add(entry);
                javaPlugin.getLogger().info("Loaded itemCmdEntry: " + KEY + " " + item + " " + usePerm + " " + usePermValue + " " + consoleCommands);
            } else 
                javaPlugin.getLogger().warning("Error: Poorly defined itemCmdEntry: " + KEY + " " + item + " " + usePerm + " " + usePermValue + " " + consoleCommands);
        } return true;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public List<ItemCmdEntry> getItemCmdEntries() {
        return itemCmdEntries;
    }
}
