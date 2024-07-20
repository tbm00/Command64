package dev.tbm00.spigot.permcheck64;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.permcheck64.listener.PlayerJoin;

public class PermCheck64 extends JavaPlugin {

    @Override
    public void onEnable() {
        // Load Config  
        this.saveDefaultConfig();

        // Check if enabled
        if (this.getConfig().getBoolean("enabled") == false) return;

        // Startup Message
        final PluginDescriptionFile pdf = this.getDescription();
		log(
            ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-",
            pdf.getName() + " v" + pdf.getVersion() + " created by tbm00",
            ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
		);

        // Register Listener
        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
    }

    private void log(String... strings) {
		for (String s : strings)
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + s);
	}
}