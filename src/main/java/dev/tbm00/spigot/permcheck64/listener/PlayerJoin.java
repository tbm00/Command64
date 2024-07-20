package dev.tbm00.spigot.permcheck64.listener;

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

import dev.tbm00.spigot.permcheck64.model.CommandEntry;

public class PlayerJoin implements Listener {
    private final ConsoleCommandSender console;
    private final List<CommandEntry> commandEntries;

    public PlayerJoin(JavaPlugin javaPlugin) {
        this.console = Bukkit.getServer().getConsoleSender();
        this.commandEntries = new ArrayList<>();

        ConfigurationSection permissionCommands = javaPlugin.getConfig().getConfigurationSection("permCommandEntries");
        if (permissionCommands != null) {
            for (String key : permissionCommands.getKeys(false)) {
                ConfigurationSection commandSection = permissionCommands.getConfigurationSection(key);
                
                if (commandSection != null && commandSection.getBoolean("enabled")) {
                    String perm = commandSection.getString("perm");
                    boolean permValue = commandSection.getBoolean("permValue");
                    String command = commandSection.getString("command");

                    if (perm != null && command != null ) {
                        CommandEntry entry = new CommandEntry(perm, permValue, command);
                        commandEntries.add(entry);
                        System.out.println("Loaded command entry: "+ perm + " " + permValue + " " + command);
                    } else {
                        System.out.println("Error: Poorly defined command entry: " + perm + " " + permValue + " " + command);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (CommandEntry entry : commandEntries) {
            String perm = entry.getPerm();
            Boolean permValue = entry.getPermValue();
            String command = entry.getCommand();
            if (command != null) {
                command = command.replace("<player>", player.getName()); 
            } else {
                System.out.println("Error: 'command' is null in onPlayerJoin");
                continue;
            }
            if (player.hasPermission(perm) == permValue) {
                System.out.println("Running command: " + command);
                Bukkit.dispatchCommand(console, command);
            }
        }
    }
}


