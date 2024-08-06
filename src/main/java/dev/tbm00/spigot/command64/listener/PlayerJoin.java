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

import dev.tbm00.spigot.command64.model.CmdEntry;

public class PlayerJoin implements Listener {
    private final ConsoleCommandSender console;
    private final List<CmdEntry> joinCmdEntries;
    private final Boolean enabled;

    public PlayerJoin(JavaPlugin javaPlugin) {
        this.console = Bukkit.getServer().getConsoleSender();
        this.joinCmdEntries = new ArrayList<>();

        ConfigurationSection joinCmdSection = javaPlugin.getConfig().getConfigurationSection("joinCommandEntries");
        if (joinCmdSection != null) {
            if (!joinCmdSection.getBoolean("enabled")) {
                enabled = false;
                return;
            } else enabled = true;
            for (String key : joinCmdSection.getKeys(false)) {
                ConfigurationSection joinCmdEntry = joinCmdSection.getConfigurationSection(key);
                
                if (joinCmdEntry != null && joinCmdEntry.getBoolean("enabled")) {
                    String checkPerm = joinCmdEntry.getString("checkPerm");
                    boolean checkPermValue = joinCmdEntry.getBoolean("checkPermValue");
                    List<String> commands = joinCmdEntry.getStringList("commands");

                    if (checkPerm != null && commands != null && !commands.isEmpty()) {
                        CmdEntry entry = new CmdEntry(checkPerm, checkPermValue, commands);
                        joinCmdEntries.add(entry);
                        System.out.println("Loaded command entry: "+ checkPerm + " " + checkPermValue + " " + commands);
                    } else {
                        System.out.println("Error: Poorly defined command entry: " + checkPerm + " " + checkPermValue + " " + commands);
                    }
                }
            }
        } else enabled = true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) return;
        Player player = event.getPlayer();

        for (CmdEntry entry : joinCmdEntries) {
            String checkPerm = entry.getPerm();
            Boolean checkPermValue = entry.getPermValue();
            List<String> commands = entry.getCommands();

            if (commands != null && !commands.isEmpty()) {
                for (String command : commands) {
                    command = command.replace("<player>", player.getName());
                    if (player.hasPermission(checkPerm) == checkPermValue) {
                        System.out.println("Running command: " + command);
                        Bukkit.dispatchCommand(console, command);
                    }
                }
            } else {
                System.out.println("Error: 'commands' is null or empty for " + checkPerm + " " + checkPermValue);
            }
        }
    }
}


