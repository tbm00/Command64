package dev.tbm00.spigot.permcheck64;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.permcheck64.command.MainCommand;
import dev.tbm00.spigot.permcheck64.listener.PlayerJoin;

public class PermCheck64 extends JavaPlugin {

    @Override
    public void onEnable() {
        // Startup Message
        final PluginDescriptionFile pdf = this.getDescription();
		log(
            ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-",
            pdf.getName() + " v" + pdf.getVersion() + " created by tbm00",
            ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
		);

        // Load Config  
        this.saveDefaultConfig();
        FileConfiguration fileConfig = this.getConfig();

        // Register Listener
        getServer().getPluginManager().registerEvents(new PlayerJoin(fileConfig), this);

        // Register Command
        getCommand("permchecker64").setExecutor(new MainCommand(this, fileConfig));
    }

    @Override
    public void onDisable() {
    }

    private void log(String... strings) {
		for (String s : strings)
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + s);
	}
}