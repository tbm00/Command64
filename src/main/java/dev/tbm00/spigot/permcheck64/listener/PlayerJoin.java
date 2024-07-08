package dev.tbm00.spigot.permcheck64.listener;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    private final FileConfiguration fileConfig;
    private final ConsoleCommandSender console;

    public PlayerJoin(FileConfiguration fileConfig) {
        this.fileConfig = fileConfig;
        this.console = Bukkit.getServer().getConsoleSender();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!fileConfig.getBoolean("enabled", true)) return;

        Player player = event.getPlayer();

        for (String key : fileConfig.getConfigurationSection("").getKeys(false)) {
            if (key.equals("enabled") || key.equals("prefix")) continue;

            String permission = fileConfig.getString(key + ".perm");
            boolean permValue = fileConfig.getBoolean(key + ".perm-value");
            String command = fileConfig.getString(key + ".command").replace("<player>", player.getName());

            if (player.hasPermission(permission) == permValue) {
                Bukkit.dispatchCommand(console, command);
            }
        }
    }
}


