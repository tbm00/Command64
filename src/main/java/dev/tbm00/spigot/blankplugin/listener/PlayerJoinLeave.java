package dev.tbm00.spigot.blankplugin.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeave implements Listener {

    public PlayerJoinLeave(FileConfiguration fileConfig) {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
    }
}


