package dev.tbm00.spigot.command64.listener;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.ConfigHandler;
import dev.tbm00.spigot.command64.CommandRunner;
import dev.tbm00.spigot.command64.model.JoinCmdEntry;

public class PlayerJoin implements Listener {
    private final JavaPlugin javaPlugin;
    private final CommandRunner cmdRunner;
    private final List<JoinCmdEntry> joinCmdEntries;

    public PlayerJoin(JavaPlugin javaPlugin, CommandRunner cmdRunner, ConfigHandler configHandler) {
        this.javaPlugin = javaPlugin;
        this.cmdRunner = cmdRunner;
        this.joinCmdEntries = configHandler.getJoinCmdEntries();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (JoinCmdEntry entry : joinCmdEntries) {
            if (player.hasPermission(entry.getPerm()) != entry.getPermValue())
                continue;
            if (!cmdRunner.runJoinCommand(entry.getConsoleCommands(), player, entry.getTickDelay()))
                javaPlugin.getLogger().warning("Error: 'consoleCommands' is null or empty for joinCmdEntry: " + entry.toString());
        }
    }
}