package dev.tbm00.spigot.command64;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.listener.PlayerJoin;
import dev.tbm00.spigot.command64.command.CmdCommand;
import dev.tbm00.spigot.command64.listener.ItemUse;


public class Command64 extends JavaPlugin {
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        // Load Config  
        this.saveDefaultConfig();

        // Startup Message
        final PluginDescriptionFile pdf = this.getDescription();
		log(
            ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-",
            pdf.getName() + " v" + pdf.getVersion() + " created by tbm00",
            ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
		);

        if (this.getConfig().getBoolean("itemCommandEntries.enabled")
                || this.getConfig().getBoolean("customCommandEntries.enabled")) {
            // Create itemManager and load config
            itemManager = new ItemManager(this);

            // Register Commands
            getCommand("cmd").setExecutor(new CmdCommand(this, itemManager));
            
            // Register ItemUse listener
            if (this.getConfig().getBoolean("itemCommandEntries.enabled")) {
                getServer().getPluginManager().registerEvents(new ItemUse(this, itemManager), this);
            }
        }


        // Register PlayerJoin listener
        if (this.getConfig().getBoolean("joinCommandEntries.enabled")) {
            getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        }
        
    }

    private void log(String... strings) {
		for (String s : strings)
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + s);
	}
}