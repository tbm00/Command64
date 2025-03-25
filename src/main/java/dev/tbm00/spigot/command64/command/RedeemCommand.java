package dev.tbm00.spigot.command64.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import net.md_5.bungee.api.chat.TextComponent;

import dev.tbm00.spigot.command64.ConfigHandler;
import dev.tbm00.spigot.command64.reward.QueueManager;

public class RedeemCommand implements TabExecutor {
    private final ConfigHandler configHandler;
    private final QueueManager queueManager;

    public RedeemCommand(ConfigHandler configHandler, QueueManager queueManager) {
        this.configHandler = configHandler;
        this.queueManager = queueManager;
    }

    public boolean onCommand(CommandSender sender, Command consoleCommand, String label, String[] args) {
        if (!sender.hasPermission("command64.redeemrewards") || !(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (queueManager.getPlayersQueueSize(player.getName())<=0) {
            if (configHandler.getNoRewardMessage()!=null&& !configHandler.getNoRewardMessage().isBlank())
                player.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.getNoRewardMessage())));
            return false;
        }

        // if player doesn't have inv space, run the first queued reward in which invCheck==false
        // else run the first queued reward
        if ((player.getInventory().firstEmpty() == -1)) {
            if (queueManager.redeemReward(player.getName(), false)) {
                if (configHandler.getRewardedMessage()!=null&& !configHandler.getRewardedMessage().isBlank())
                    player.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.getRewardedMessage())));
                return true;
            } else {
                if (configHandler.getNoInvSpaceMessage()!=null&& !configHandler.getNoInvSpaceMessage().isBlank())
                    player.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.getNoInvSpaceMessage())));
                return true;
            }
        } else {
            if (queueManager.redeemReward(player.getName(), true)) {
                if (configHandler.getRewardedMessage()!=null&& !configHandler.getRewardedMessage().isBlank())
                    player.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.getRewardedMessage())));
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
