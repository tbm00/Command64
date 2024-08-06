package dev.tbm00.spigot.command64.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.model.JoinCmdEntry;

public class PlayerJoin implements Listener {
    private final ConsoleCommandSender console;
    private final Boolean enabled;
    private final List<JoinCmdEntry> joinCmdEntries;

    public PlayerJoin(JavaPlugin javaPlugin) {
        this.console = Bukkit.getServer().getConsoleSender();
        this.joinCmdEntries = new ArrayList<>();

        ConfigurationSection joinCmdSection = javaPlugin.getConfig().getConfigurationSection("joinCommandEntries");
        if (joinCmdSection != null) {
            if (!joinCmdSection.getBoolean("enabled")) {
                this.enabled = false;
                return;
            } else this.enabled = true;
            for (String key : joinCmdSection.getKeys(false)) {
                ConfigurationSection joinCmdEntry = joinCmdSection.getConfigurationSection(key);
                
                if (joinCmdEntry != null && joinCmdEntry.getBoolean("enabled")) {
                    String checkPerm = joinCmdEntry.getString("checkPerm");
                    boolean checkPermValue = joinCmdEntry.getBoolean("checkPermValue");
                    List<String> consoleCommands = joinCmdEntry.getStringList("consoleCommands");

                    if (checkPerm != null && consoleCommands != null && !consoleCommands.isEmpty()) {
                        JoinCmdEntry entry = new JoinCmdEntry(checkPerm, checkPermValue, consoleCommands);
                        joinCmdEntries.add(entry);
                        System.out.println("Loaded joinCmdEntry: "+ checkPerm + " " + checkPermValue + " " + consoleCommands);
                    } else {
                        System.out.println("Error: Poorly defined joinCmdEntry: " + checkPerm + " " + checkPermValue);
                    }
                }
            }
        } else this.enabled = true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) return;
        Player player = event.getPlayer();

        for (JoinCmdEntry entry : joinCmdEntries) {
            String checkPerm = entry.getPerm();
            Boolean checkPermValue = entry.getPermValue();
            List<String> consoleCommands = entry.getConsoleCommands();

            if (consoleCommands != null && !consoleCommands.isEmpty()) {
                for (String consoleCommand : consoleCommands) {
                    consoleCommand = consoleCommand.replace("<player>", player.getName());
                    if (player.hasPermission(checkPerm) == checkPermValue) {
                        System.out.println("Running joinCmdEntry: " + consoleCommand);
                        Bukkit.dispatchCommand(console, consoleCommand);
                    }
                }
            } else {
                System.out.println("Error: 'consoleCommands' is null or empty for joinCmdEntry: " + checkPerm + " " + checkPermValue);
            }
        }
    }
}