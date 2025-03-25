package dev.tbm00.spigot.command64;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.command.*;
import dev.tbm00.spigot.command64.listener.*;
import dev.tbm00.spigot.command64.reward.QueueManager;

public class Command64 extends JavaPlugin {
    private static ConfigHandler configHandler;
    private static CommandRunner cmdRunner;
    private static QueueManager queueManager;
    private static CronManager cronManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        final PluginDescriptionFile pdf = this.getDescription();
		log(
            ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-",
            pdf.getName() + " v" + pdf.getVersion() + " created by tbm00",
            ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
		);

        // initialize managers, listeners, & commands
        configHandler = new ConfigHandler(this);
        cmdRunner = new CommandRunner(this);

        if (configHandler.isCronEnabled()) {
            cronManager = new CronManager(this, cmdRunner, configHandler);
        } else cronManager = null;
        
        if (configHandler.isRewardsEnabled()) {
            queueManager = new QueueManager(this, cmdRunner, configHandler);
            getCommand("redeemreward").setExecutor(new RedeemCommand(configHandler, queueManager));
        } else queueManager = null;
        
        getCommand("cmd").setExecutor(new CmdCommand(this, cmdRunner, configHandler, queueManager));

        if (configHandler.isItemEnabled())
            getServer().getPluginManager().registerEvents(new ItemUse(this, cmdRunner, configHandler), this);
        if (configHandler.isJoinEnabled() || configHandler.isRewardsEnabled()) 
            getServer().getPluginManager().registerEvents(new PlayerConnection(this, cmdRunner, configHandler, queueManager), this);
    }

    private void log(String... strings) {
		for (String s : strings)
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + s);
	}

    public String getRandomPlayer() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) {
            return "Herobrine";
        }
    
        int targetIndex = new Random().nextInt(players.size());
        int i = 0;
        for (Player player : players) {
            if (i == targetIndex) {
                return player.getName();
            }
            i++;
        }
    
        return "Herobrine";
    }
    

    @Override
    public void onDisable() {
        if (queueManager != null) {
            queueManager.shutdown();
        }
        if (cronManager != null) {
            cronManager.shutdown();
        }
    }
}