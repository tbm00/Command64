package dev.tbm00.spigot.command64;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.listener.PlayerJoin;
import dev.tbm00.spigot.command64.command.CmdCommand;
import dev.tbm00.spigot.command64.listener.ItemUse;

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

        // initialize managers
        cmdRunner = new CommandRunner(this);
        configHandler = new ConfigHandler(this);
        queueManager = new QueueManager(this);

        // load command
        getCommand("cmd").setExecutor(new CmdCommand(this, cmdRunner, configHandler, queueManager));

        // register listeners
        if (this.getConfig().getBoolean("itemCommandEntries.enabled"))
            getServer().getPluginManager().registerEvents(new ItemUse(this, cmdRunner, configHandler), this);
        if (this.getConfig().getBoolean("joinCommandEntries.enabled") || this.getConfig().getBoolean("rewardClaimingSystem.enabled")) 
            getServer().getPluginManager().registerEvents(new PlayerJoin(this, cmdRunner, configHandler), this);
    }

    private void log(String... strings) {
		for (String s : strings)
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + s);
	}
}