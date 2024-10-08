package dev.tbm00.spigot.command64.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.model.JoinCmdEntry;
import dev.tbm00.spigot.command64.CommandRunner;

public class PlayerJoin implements Listener {
    private final JavaPlugin javaPlugin;
    private final CommandRunner cmdRunner;
    private final Boolean enabled;
    private final List<JoinCmdEntry> joinCmdEntries;

    public PlayerJoin(JavaPlugin javaPlugin, CommandRunner cmdRunner) {
        this.javaPlugin = javaPlugin;
        this.cmdRunner = cmdRunner;
        this.joinCmdEntries = new ArrayList<>();
        
        if (!loadJoinConfig()) enabled = false;
        else enabled = true;
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) return;
        Player player = event.getPlayer();

        for (JoinCmdEntry entry : joinCmdEntries) {
            if (player.hasPermission(entry.getPerm()) != entry.getPermValue())
                continue;
            if (!cmdRunner.runJoinCommand(entry.getConsoleCommands(), player, entry.getTickDelay()))
                javaPlugin.getLogger().warning("Error: 'consoleCommands' is null or empty for joinCmdEntry: " + entry.toString());
        }
    }
}