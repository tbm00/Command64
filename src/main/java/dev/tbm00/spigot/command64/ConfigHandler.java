package dev.tbm00.spigot.command64;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.model.CustomCmdEntry;
import dev.tbm00.spigot.command64.model.ItemCmdEntry;
import dev.tbm00.spigot.command64.model.JoinCmdEntry;
import dev.tbm00.spigot.command64.model.RewardCmdEntry;

public class ConfigHandler {
    private final JavaPlugin javaPlugin;
    private final List<RewardCmdEntry> rewardCmdEntries = new ArrayList<>();
    private final List<CustomCmdEntry> customCmdEntries = new ArrayList<>();
    private final List<ItemCmdEntry> itemCmdEntries = new ArrayList<>();
    private final List<JoinCmdEntry> joinCmdEntries = new ArrayList<>();
    private boolean rewardsEnabled;
    private boolean itemEnabled;
    private boolean customEnabled;
    private boolean joinEnabled;
    private String noRewardMessage;
    private String noInvSpaceMessage;
    private String rewardedMessage;
    private String joinMessage;
    private int joinMessageDelay;
    private int saveDataInterval;

    public ConfigHandler(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        if (!loadRewardConfig()) rewardsEnabled = false;
        else rewardsEnabled = true;
        if (!loadItemConfig()) itemEnabled = false;
        else itemEnabled = true;
        if (!loadCustomConfig()) customEnabled = false;
        else customEnabled = true;
        if (!loadJoinConfig()) joinEnabled = false;
        else joinEnabled = true;
    }

    private boolean loadRewardConfig() {
        ConfigurationSection rewardSystemSec = javaPlugin.getConfig().getConfigurationSection("rewardSystem");
        if (rewardSystemSec == null || !rewardSystemSec.getBoolean("enabled")) return false;

        saveDataInterval = rewardSystemSec.getInt("saveDataInterval");
        noRewardMessage = rewardSystemSec.getString("redeemMessages.noRewardMessage");
        noInvSpaceMessage = rewardSystemSec.getString("redeemMessages.noInvSpaceMessage");
        rewardedMessage = rewardSystemSec.getString("redeemMessages.rewardedMessage");
        joinMessage = rewardSystemSec.getString("pendingRewardsJoinMessage.message");
        joinMessageDelay = rewardSystemSec.getInt("pendingRewardsJoinMessage.tickDelay");

        ConfigurationSection rewardCmdSection = rewardSystemSec.getConfigurationSection("rewardEntries");
        for (String key : rewardCmdSection.getKeys(false)) {
            ConfigurationSection rewardCmdEntry = rewardCmdSection.getConfigurationSection(key);
            if (rewardCmdEntry == null)
                continue;
            
            String name = rewardCmdEntry.getString("name");
            Boolean invCheck = rewardCmdEntry.getBoolean("invCheck");
            List<String> consoleCommands = rewardCmdEntry.getStringList("consoleCommands");

            if (name != null && consoleCommands != null && !consoleCommands.isEmpty()) {
                RewardCmdEntry entry = new RewardCmdEntry(name, invCheck, consoleCommands);
                rewardCmdEntries.add(entry);
                javaPlugin.getLogger().info("Loaded rewardCmdEntry: " + name + " " + invCheck + " " + consoleCommands);
            } else
                javaPlugin.getLogger().warning("Error: Poorly defined rewardCmdEntry: " + name + " " + invCheck);
        } return true;
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
            String checkPlayer = customCmdEntry.contains("") ? customCmdEntry.getString("invCheck.checkOnPlayer") : "";
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

    public List<String> getRewardCommandsByName(String rewardName) {
        for (RewardCmdEntry entry : rewardCmdEntries) {
            if (entry.getName().equalsIgnoreCase(rewardName)) {
                return entry.getConsoleCommands();
            }
        }
        return null;
    }
    
    public Boolean getRewardInvCheckByName(String rewardName) {
        for (RewardCmdEntry entry : rewardCmdEntries) {
            if (entry.getName().equalsIgnoreCase(rewardName)) {
                return entry.getInvCheck();
            }
        }
        return null;
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

    public List<RewardCmdEntry> getRewardCmdEntries() {
        return rewardCmdEntries;
    }
    
    public boolean isRewardsEnabled() {
        return rewardsEnabled;
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

    public String getNoRewardMessage() {
        return noRewardMessage;
    }
    
    public String getNoInvSpaceMessage() {
        return noInvSpaceMessage;
    }
    
    public String getRewardedMessage() {
        return rewardedMessage;
    }
    
    public String getJoinMessage() {
        return joinMessage;
    }
    
    public int getJoinMessageDelay() {
        return joinMessageDelay;
    }

    public int getSaveDataInterval() {
        return saveDataInterval;
    }
}
