package dev.tbm00.spigot.command64.listener;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.NamespacedKey;

import dev.tbm00.spigot.command64.CommandRunner;
import dev.tbm00.spigot.command64.ItemManager;
import dev.tbm00.spigot.command64.model.ItemCmdEntry;


public class ItemUse implements Listener {
    private final JavaPlugin javaPlugin;
    private final CommandRunner cmdRunner;
    private final Boolean enabled;
    private final List<ItemCmdEntry> itemCmdEntries;


    public ItemUse(JavaPlugin javaPlugin, CommandRunner cmdRunner, ItemManager itemManager) {
        this.javaPlugin = javaPlugin;
        this.cmdRunner = cmdRunner;
        this.enabled = itemManager.isEnabled();
        this.itemCmdEntries = itemManager.getItemCmdEntries();
    }


    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (!enabled) return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();

        for (ItemCmdEntry entry : itemCmdEntries) {
            if (player.hasPermission(entry.getUsePerm()) != entry.getUsePermValue())
                continue;

            NamespacedKey key = new NamespacedKey(javaPlugin, entry.getKeyString());
            if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING))
                continue;

            event.setCancelled(true);
    
            if (!cmdRunner.runItemCommand(entry.getConsoleCommands(), player))
                System.out.println("Error: 'consoleCommands' is null or empty for itemCmdEntry: " + entry.toString());
        }
    }
}