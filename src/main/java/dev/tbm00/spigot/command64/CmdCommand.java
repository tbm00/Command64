package dev.tbm00.spigot.command64;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.model.CustomCmdEntry;
// import dev.tbm00.spigot.command64.model.ItemCmdEntry;

public class CmdCommand {
    private final ConsoleCommandSender console;
    private final List<CustomCmdEntry> customCmdEntries;
    // private final String[] itemCmdEntries;
    private final Boolean customEnabled;
    // private final Boolean itemEnabled;

    public CmdCommand(JavaPlugin javaPlugin) {
        this.console = Bukkit.getServer().getConsoleSender();
        this.customCmdEntries = new ArrayList<>();
        // this.itemCmdEntries = new ArrayList<>();

        ConfigurationSection customCmdSection = javaPlugin.getConfig().getConfigurationSection("customCommandEntries");
        if (customCmdSection != null) {
            if (!customCmdSection.getBoolean("enabled")) {
                this.customEnabled = false;
                return;
            } else this.customEnabled = true;
            for (String key : customCmdSection.getKeys(false)) {
                ConfigurationSection joinCmdEntry = customCmdSection.getConfigurationSection(key);
                
                if (joinCmdEntry != null && joinCmdEntry.getBoolean("enabled")) {
                    String checkPerm = joinCmdEntry.getString("checkPerm");
                    boolean checkPermValue = joinCmdEntry.getBoolean("checkPermValue");
                    String playerCommand = joinCmdEntry.getString("customCommand");
                    List<String> consoleCommands = joinCmdEntry.getStringList("consoleCommands");
                    if (checkPerm != null && consoleCommands != null && playerCommand != null && !consoleCommands.isEmpty()) {
                        CustomCmdEntry entry = new CustomCmdEntry(checkPerm, checkPermValue, playerCommand, consoleCommands);
                        this.customCmdEntries.add(entry);
                        System.out.println("Loaded customCmdEntry: "+ checkPerm + " " + checkPermValue + " " + playerCommand);
                    } else {
                        System.out.println("Error: Poorly defined customCmdEntry: " + checkPerm + " " + checkPermValue+ " " + playerCommand);
                    }
                }
            }
        } else this.customEnabled = true;
    }

    public boolean onCommand(CommandSender sender, Command consoleCommand, String label, String[] args) {
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command consoleCommand, String alias, String[] args) {
    }
}
