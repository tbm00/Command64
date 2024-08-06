package dev.tbm00.spigot.command64;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.model.CustomCmdEntry;
// import dev.tbm00.spigot.command64.model.ItemCmdEntry;

public class CmdCommand implements TabExecutor {
    private final ConsoleCommandSender console;
    private final List<CustomCmdEntry> customCmdEntries;
    // private final String[] itemCmdEntries;
    private final Boolean customEnabled;
    // private final Boolean itemEnabled;

    public CmdCommand(JavaPlugin javaPlugin) {
        this.console = Bukkit.getServer().getConsoleSender();
        this.customCmdEntries = new ArrayList<>();
        
        // this.itemCmdEntries = new ArrayList<>();

        ConfigurationSection customCmdSection = javaPlugin.getConfig().getConfigurationSection("customCommandEntries");
        if (customCmdSection != null) {
            if (!customCmdSection.getBoolean("enabled")) {
                this.customEnabled = false;
                return;
            } else this.customEnabled = true;
            List<String> tempSubCommands = new ArrayList<>();
            for (String key : customCmdSection.getKeys(false)) {
                ConfigurationSection joinCmdEntry = customCmdSection.getConfigurationSection(key);
                
                if (joinCmdEntry != null && joinCmdEntry.getBoolean("enabled")) {
                    String checkPerm = joinCmdEntry.getString("checkPerm");
                    boolean checkPermValue = joinCmdEntry.getBoolean("checkPermValue");
                    String playerCommand = joinCmdEntry.getString("customCommand");
                    List<String> consoleCommands = joinCmdEntry.getStringList("consoleCommands");
                    if (checkPerm != null && consoleCommands != null && playerCommand != null && !consoleCommands.isEmpty()) {
                        CustomCmdEntry entry = new CustomCmdEntry(checkPerm, checkPermValue, playerCommand, consoleCommands);
                        this.customCmdEntries.add(entry);
                        tempSubCommands.add(playerCommand);
                        System.out.println("Loaded customCmdEntry: "+ checkPerm + " " + checkPermValue + " " + playerCommand);
                    } else {
                        System.out.println("Error: Poorly defined customCmdEntry: " + checkPerm + " " + checkPermValue+ " " + playerCommand);
                    }
                }
            }
        } else this.customEnabled = false;
    }

    public boolean onCommand(CommandSender sender, Command consoleCommand, String label, String[] args) {
        // /cmd
        if (args.length != 1 && args.length != 2) {
            return false;
        }
        String subCommand = args[0].toLowerCase();


        if (customEnabled) {
            for (CustomCmdEntry entry : customCmdEntries) {
                if (subCommand.equals(entry.getPlayerCommand())) {
                    String checkPerm = entry.getPerm();
                    Boolean checkPermValue = entry.getPermValue();
                    List<String> consoleCommands = entry.getConsoleCommands();
                    String argument = null;
        
                    if (consoleCommands != null && !consoleCommands.isEmpty()) {
                        if (args.length == 2) {
                            argument = args[1];
                        }
                        for (String runningConsoleCommand : consoleCommands) {
                            runningConsoleCommand = runningConsoleCommand.replace("<player>", sender.getName())
                                                                        .replace("<argument>", argument);
                            if (sender.hasPermission(checkPerm) == checkPermValue) {
                                System.out.println("Running customCmdEntry: " + runningConsoleCommand);
                                Bukkit.dispatchCommand(console, runningConsoleCommand);
                                return true;
                            }
                        }
                    } else {
                        System.out.println("Error: 'consoleCommands' is null or empty for customCmdEntry: " + checkPerm + " " + checkPermValue);
                        return false;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command consoleCommand, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.clear();
            for (CustomCmdEntry n : customCmdEntries) {
                if (n!=null && sender.hasPermission(n.getPerm()) && n.getPlayerCommand().startsWith(args[0])) {
                    list.add(n.getPlayerCommand());
                }
            }
        }
        if (args.length == 2) {
            list.clear();
            for (Player p : Bukkit.getOnlinePlayers()) {
                list.add(p.getName());
            }
        }
        return list;
    }
}
