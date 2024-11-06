package dev.tbm00.spigot.command64.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import dev.tbm00.spigot.command64.ConfigHandler;
import dev.tbm00.spigot.command64.QueueManager;
import dev.tbm00.spigot.command64.CommandRunner;
import dev.tbm00.spigot.command64.model.CustomCmdEntry;
import dev.tbm00.spigot.command64.model.ItemCmdEntry;
import dev.tbm00.spigot.command64.model.RewardCmdEntry;
import net.md_5.bungee.api.chat.TextComponent;

public class RedeemCommand implements TabExecutor {
    private final JavaPlugin javaPlugin;
    private final CommandRunner cmdRunner;
    private final ConfigHandler configHandler;
    private final QueueManager queueManager;

    public RedeemCommand(JavaPlugin javaPlugin, CommandRunner cmdRunner, ConfigHandler configHandler, QueueManager queueManager) {
        this.javaPlugin = javaPlugin;
        this.cmdRunner = cmdRunner;
        this.configHandler = configHandler;
        this.queueManager = queueManager;
    }

    public boolean onCommand(CommandSender sender, Command consoleCommand, String label, String[] args) {
        if (!sender.hasPermission("command64.redeemrewards") || !(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (queueManager.getPlayersQueueSize(player.getName())<=0) {
            player.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.noRewardMessage)));
            return false;
        }

        // if player doesn't have inv space, run the first queued reward in which invCheck==false
        // else run the first queued reward
        if ((player.getInventory().firstEmpty() == -1)) {
            if (queueManager.redeemReward(player.getDisplayName(), false)) {
                player.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.rewardedMessage)));
                return true;
            } else {
                player.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.noInvSpaceMessage)));
                return true;
            }
        } else {
            if (queueManager.redeemReward(player.getDisplayName(), true)) {
                player.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.rewardedMessage)));
                return true;
            } else {
                return false;
            }
        }

    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command consoleCommand, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        list.clear();
        return list;
    }
}
