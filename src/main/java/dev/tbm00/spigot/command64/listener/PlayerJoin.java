package dev.tbm00.spigot.command64.listener;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.chat.TextComponent;

import dev.tbm00.spigot.command64.ConfigHandler;
import dev.tbm00.spigot.command64.QueueManager;
import dev.tbm00.spigot.command64.CommandRunner;
import dev.tbm00.spigot.command64.model.JoinCmdEntry;

public class PlayerJoin implements Listener {
    private final JavaPlugin javaPlugin;
    private final CommandRunner cmdRunner;
    private final List<JoinCmdEntry> joinCmdEntries;
    private final ConfigHandler configHandler;
    private final QueueManager queueManager;

    public PlayerJoin(JavaPlugin javaPlugin, CommandRunner cmdRunner, ConfigHandler configHandler, QueueManager queueManager) {
        this.javaPlugin = javaPlugin;
        this.cmdRunner = cmdRunner;
        this.joinCmdEntries = configHandler.getJoinCmdEntries();
        this.configHandler = configHandler;
        this.queueManager = queueManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (configHandler.isJoinEnabled()) {
            for (JoinCmdEntry entry : joinCmdEntries) {
                if (player.hasPermission(entry.getPerm()) != entry.getPermValue())
                    continue;
                if (!cmdRunner.runJoinCommand(entry.getConsoleCommands(), player, entry.getTickDelay()))
                    javaPlugin.getLogger().warning("Error: 'consoleCommands' is null or empty for joinCmdEntry: " + entry.toString());
            }
        }

        if (configHandler.isRewardsEnabled()) {
            UUID uuid = event.getPlayer().getUniqueId();
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player!=null && queueManager.getPlayersQueueSize(player.getName())>0) {
                        player.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.getJoinMessage())));
                    }
                }
            }.runTaskLater(javaPlugin, configHandler.getJoinMessageDelay());
        }
    }
}