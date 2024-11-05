package dev.tbm00.spigot.command64;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.model.CustomCmdEntry;
import dev.tbm00.spigot.command64.model.ItemCmdEntry;
import dev.tbm00.spigot.command64.model.JoinCmdEntry;

public class ConfigHandler {
    private final JavaPlugin javaPlugin;
    private final List<CustomCmdEntry> customCmdEntries = new ArrayList<>();
    private final List<ItemCmdEntry> itemCmdEntries = new ArrayList<>();
    private final List<JoinCmdEntry> joinCmdEntries = new ArrayList<>();
    boolean itemEnabled;
    boolean customEnabled;
    boolean joinEnabled;

    public ConfigHandler(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        if (!loadItemConfig()) itemEnabled = false;
        else itemEnabled = true;
        if (!loadCustomConfig()) customEnabled = false;
        else customEnabled = true;
        if (!loadJoinConfig()) joinEnabled = false;
        else joinEnabled = true;
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

    private boolean loadCustomConfig() {
        ConfigurationSection customCmdSection = javaPlugin.getConfig().getConfigurationSection("customCommandEntries");
        if (customCmdSection == null || !customCmdSection.getBoolean("enabled")) return false;

        for (String key : customCmdSection.getKeys(false)) {
            ConfigurationSection customCmdEntry = customCmdSection.getConfigurationSection(key);
            if (customCmdEntry == null || !customCmdEntry.getBoolean("enabled")) continue;
            
            String usePerm = customCmdEntry.contains("usePerm") ? customCmdEntry.getString("usePerm") : null;
            Boolean usePermValue = customCmdEntry.contains("") ? customCmdEntry.getBoolean("usePermValue") : null;
            String playerCommand = customCmdEntry.contains("") ? customCmdEntry.getString("customCommand") : null;
            List<String> consoleCommands = customCmdEntry.contains("") ? customCmdEntry.getStringList("consoleCommands") : null;
            Boolean checkInv = customCmdEntry.contains("") ? customCmdEntry.getBoolean("invCheck.checkIfSpaceBeforeRun") : false;
            String checkPlayer = customCmdEntry.contains("") ? customCmdEntry.getString("invCheck.checkPlayer") : "";
            List<String> bkupConsoleCommands = customCmdEntry.contains("") ? customCmdEntry.getStringList("invCheck.ifNoSpaceConsoleCommands") : null;

            if (usePerm != null && playerCommand != null && (consoleCommands != null && !consoleCommands.isEmpty()) || (bkupConsoleCommands != null && !bkupConsoleCommands.isEmpty())) {
                CustomCmdEntry entry = new CustomCmdEntry(usePerm, usePermValue, playerCommand, consoleCommands, checkInv, checkPlayer, bkupConsoleCommands);
                customCmdEntries.add(entry);
                javaPlugin.getLogger().info("Loaded customCmdEntry: " + playerCommand);
            } else 
                javaPlugin.getLogger().warning("Loaded customCmdEntry: " + usePerm + " " + usePermValue + " " + playerCommand + " " + consoleCommands);
        } return true;
    }

    private boolean loadJoinConfig() {
        ConfigurationSection joinCmdSection = javaPlugin.getConfig().getConfigurationSection("joinCommandEntries");
        if (joinCmdSection == null || !joinCmdSection.getBoolean("enabled")) return false;

        for (String key : joinCmdSection.getKeys(false)) {
            ConfigurationSection joinCmdEntry = joinCmdSection.getConfigurationSection(key);
            if (joinCmdEntry == null || !joinCmdEntry.getBoolean("enabled"))
                continue;
            
            String checkPerm = joinCmdEntry.getString("checkPerm");
            Boolean checkPermValue = joinCmdEntry.getBoolean("checkPermValue");
            List<String> consoleCommands = joinCmdEntry.getStringList("consoleCommands");
            Long tickDelay = joinCmdEntry.getLong("tickDelay");

            if (checkPerm != null && consoleCommands != null && !consoleCommands.isEmpty() && tickDelay != null) {
                JoinCmdEntry entry = new JoinCmdEntry(checkPerm, checkPermValue, consoleCommands, tickDelay);
                joinCmdEntries.add(entry);
                javaPlugin.getLogger().info("Loaded joinCmdEntry: " + checkPerm + " " + checkPermValue + " " + consoleCommands + " " + tickDelay);
            } else
                javaPlugin.getLogger().warning("Error: Poorly defined joinCmdEntry: " + checkPerm + " " + checkPermValue + " " + tickDelay);
        } return true;
    }

    public List<CustomCmdEntry> getCustomCmdEntries() {
        return customCmdEntries;
    }
    
    public List<ItemCmdEntry> getItemCmdEntries() {
        return itemCmdEntries;
    }

    public List<JoinCmdEntry> getJoinCmdEntries() {
        return joinCmdEntries;
    }
    
    public boolean isJoinEnabled() {
        return joinEnabled;
    }

    public boolean isItemEnabled() {
        return itemEnabled;
    }
    
    public boolean isCustomEnabled() {
        return customEnabled;
    }
}
