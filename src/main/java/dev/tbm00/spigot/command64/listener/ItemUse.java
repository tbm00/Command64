package dev.tbm00.spigot.command64.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;

import dev.tbm00.spigot.command64.ItemManager;
import dev.tbm00.spigot.command64.model.ItemCmdEntry;


public class ItemUse implements Listener {
    private final JavaPlugin javaPlugin;
    private final ConsoleCommandSender console;
    private final Boolean enabled;
    private final List<ItemCmdEntry> itemCmdEntries;


    public ItemUse(JavaPlugin javaPlugin, ItemManager itemManager) {
        this.javaPlugin = javaPlugin;
        this.console = Bukkit.getServer().getConsoleSender();
        this.enabled = itemManager.isEnabled();
        this.itemCmdEntries = itemManager.getItemCmdEntries();
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            for (ItemCmdEntry entry : itemCmdEntries) {
                NamespacedKey key = new NamespacedKey(javaPlugin, entry.getKeyString());
                if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    if (player.hasPermission(entry.getUsePerm()) == entry.getUsePermValue()) {
                        for (String command : entry.getConsoleCommands()) {
                            String cmd = command.replace("<player>", player.getName());
                            System.out.println("Running itemCmdEntry: " + cmd);
                            Bukkit.dispatchCommand(console, cmd);
                        }
                        event.setCancelled(true);
                        return;
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have permission to use this item.");
                    }
                }
            }
        }
    }
}