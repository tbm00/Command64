package dev.tbm00.spigot.command64;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.command.*;
import dev.tbm00.spigot.command64.listener.*;
import dev.tbm00.spigot.command64.reward.QueueManager;

public class Command64 extends JavaPlugin {
    private static ConfigHandler configHandler;
    private static CommandRunner cmdRunner;
    private static QueueManager queueManager;

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
        
        if (this.getConfig().getBoolean("rewardSystem.enabled")) {
            queueManager = new QueueManager(this, cmdRunner, configHandler);
            getCommand("redeemreward").setExecutor(new RedeemCommand(configHandler, queueManager));
        } else queueManager = null;
        
        getCommand("cmd").setExecutor(new CmdCommand(this, cmdRunner, configHandler, queueManager));

        if (this.getConfig().getBoolean("itemCommandEntries.enabled"))
            getServer().getPluginManager().registerEvents(new ItemUse(this, cmdRunner, configHandler), this);
        if (this.getConfig().getBoolean("joinCommandEntries.enabled") || this.getConfig().getBoolean("rewardSystem.enabled")) 
            getServer().getPluginManager().registerEvents(new PlayerConnection(this, cmdRunner, configHandler, queueManager), this);
    }

    private void log(String... strings) {
		for (String s : strings)
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + s);
	}

    @Override
    public void onDisable() {
        if (queueManager != null) {
            queueManager.shutdown();
        }
    }
}