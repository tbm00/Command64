package dev.tbm00.spigot.command64;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.listener.PlayerJoin;
import dev.tbm00.spigot.command64.command.CmdCommand;
import dev.tbm00.spigot.command64.listener.ItemUse;

public class Command64 extends JavaPlugin {
    private static ItemManager itemManager;
    private static CommandRunner cmdRunner;

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
        itemManager = new ItemManager(this);

        // load command
        getCommand("cmd").setExecutor(new CmdCommand(this, cmdRunner, itemManager));

        // register listeners
        if (this.getConfig().getBoolean("itemCommandEntries.enabled"))
            getServer().getPluginManager().registerEvents(new ItemUse(this, cmdRunner, itemManager), this);
        if (this.getConfig().getBoolean("joinCommandEntries.enabled")) 
            getServer().getPluginManager().registerEvents(new PlayerJoin(this, cmdRunner), this);
    }

    private void log(String... strings) {
		for (String s : strings)
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + s);
	}
}