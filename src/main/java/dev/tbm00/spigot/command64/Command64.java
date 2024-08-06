package dev.tbm00.spigot.command64;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.listener.PlayerJoin;



public class Command64 extends JavaPlugin {

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

        // Register Listener
        if (this.getConfig().getBoolean("joinCommandEntries.enabled")) {
            getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        }
        // Register Command
        getCommand("cmd").setExecutor(new CmdCommand(this));
    }

    private void log(String... strings) {
		for (String s : strings)
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + s);
	}
}