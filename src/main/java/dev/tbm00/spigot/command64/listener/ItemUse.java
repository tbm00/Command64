package dev.tbm00.spigot.command64.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.ItemManager;
import dev.tbm00.spigot.command64.model.ItemCmdEntry;


public class ItemUse implements Listener {
    private final ConsoleCommandSender console;
    private final Boolean enabled;
    private final ItemManager itemManager;
    private final List<ItemCmdEntry> itemCmdEntries;


    public ItemUse(JavaPlugin javaPlugin, ItemManager itemManager) {
        this.console = Bukkit.getServer().getConsoleSender();
        this.enabled = itemManager.isEnabled();
        this.itemManager = itemManager;
        this.itemCmdEntries = itemManager.getItemCmdEntries();
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        // First check to make sure the item use is not air
        // then check to make sure the item used has one of the ItemCmdEntry's keys
            // if so, run the desired command as console
    }
}