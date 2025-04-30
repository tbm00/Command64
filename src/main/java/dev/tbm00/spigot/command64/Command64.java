package dev.tbm00.spigot.command64;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private final Random random = new Random();

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

    public String getRandomPlayer(String excludedPlayer) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) return "Herobrine";
        if (players.size()==1) {
            if (getServer().getPlayer(excludedPlayer)!=null && getServer().getPlayer(excludedPlayer).isOnline()) {
                return "HEROBRINE";
            }
            List<? extends Player> list = new ArrayList<>(players);
            return list.get(0).getName();
        }

        List<? extends Player> list = new ArrayList<>(players);
        Player chosen = list.get(random.nextInt(list.size()));
        
        if (chosen.getName().equalsIgnoreCase(excludedPlayer))
            return getRandomPlayer(excludedPlayer);
        else if (!chosen.isOnline())
            return getRandomPlayer(excludedPlayer);
        else return chosen.getName();
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