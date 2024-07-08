package dev.tbm00.spigot.permcheck64.listener;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoin implements Listener {
    private JavaPlugin javaPlugin;
    private final ConsoleCommandSender console;

    public PlayerJoin(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.console = Bukkit.getServer().getConsoleSender();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!javaPlugin.getConfig().getBoolean("enabled", true)) return;

        Player player = event.getPlayer();

        for (String key : javaPlugin.getConfig().getConfigurationSection("").getKeys(false)) {
            if (key.equals("enabled") || key.equals("prefix")) continue;

            String permission = javaPlugin.getConfig().getString(key + ".perm");
            boolean permValue = javaPlugin.getConfig().getBoolean(key + ".perm-value");
            String command = javaPlugin.getConfig().getString(key + ".command").replace("<player>", player.getName());

            if (player.hasPermission(permission) == permValue) {
                Bukkit.dispatchCommand(console, command);
            }
        }
    }
}


