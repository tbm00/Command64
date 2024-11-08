package dev.tbm00.spigot.command64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import dev.tbm00.spigot.command64.model.CustomCmdEntry;
import dev.tbm00.spigot.command64.model.DelayedTask;

public class CommandRunner {
    private final JavaPlugin javaPlugin;
    private final ConsoleCommandSender console;
    private final Map<DelayedTask, BukkitTask> pendingTasks;
    private final String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "cmd" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;

    public CommandRunner(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.console = Bukkit.getServer().getConsoleSender();
        this.pendingTasks = new HashMap<>();
    }

    public boolean runJoinCommand(List<String> consoleCmds, Player player, long tickDelay) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = player.getName();

        Bukkit.getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> {
            if (!player.isOnline()) return;

            javaPlugin.getLogger().info(name + " triggered a joinCmdEntry's consoleCommands...");

            List<String> processedCmds = new ArrayList<>();
            for (String consoleCmd : consoleCmds)
                processedCmds.add(consoleCmd.replace("<player>", name));

            Bukkit.getScheduler().runTask(javaPlugin, () -> {
                for (String processedCmd : processedCmds) {
                    javaPlugin.getLogger().info(name + " triggered join command: " + processedCmd);

                    Bukkit.dispatchCommand(console, processedCmd);
                }
            });
        }, tickDelay);
        
        return true;
    }

    public boolean runItemCommand(List<String> consoleCmds, Player player) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = player.getName();
        javaPlugin.getLogger().info(name + " triggered an itemCmdEntry's consoleCommands...");

        for (String consoleCmd : consoleCmds) {
            consoleCmd = consoleCmd.replace("<player>", name);
            javaPlugin.getLogger().info(name + " triggered item command: " + consoleCmd);

            Bukkit.dispatchCommand(console, consoleCmd);
        }
        return true;
    }

    public boolean runRewardCommand(List<String> consoleCmds, String player, String argument) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        javaPlugin.getLogger().info(player + " triggered a rewardEntry's consoleCommands...");

        for (String consoleCmd : consoleCmds) {
            if (player != null)
                consoleCmd = consoleCmd.replace("<player>", player);
            else return false;
            if (argument != null) {
                consoleCmd = consoleCmd.replace("<argument>", argument);
                javaPlugin.getLogger().info(player + " triggered reward command: <"+argument+"> - " + consoleCmd);
            } else javaPlugin.getLogger().info(player + " triggered reward command: " + consoleCmd);

            Bukkit.dispatchCommand(console, consoleCmd);
        }
        return true;
    }

    public boolean runCustomCommand(List<String> consoleCmds, CommandSender sender, String argument) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = sender.getName();
        javaPlugin.getLogger().info(name + " triggered a customCmdEntry's consoleCommands...");
        sender.sendMessage(prefix + ChatColor.YELLOW + "Running custom command...");

        for (String consoleCmd : consoleCmds) {
            consoleCmd = consoleCmd.replace("<player>", name);
            if (argument != null) {
                consoleCmd = consoleCmd.replace("<argument>", argument);
                javaPlugin.getLogger().info(name + " triggered custom command: <"+argument+"> - " + consoleCmd);
            } else javaPlugin.getLogger().info(name + " triggered custom command: " + consoleCmd);

            Bukkit.dispatchCommand(console, consoleCmd);
        }
        return true;
    }

    // args[0] = "delayed"
    // args[1] = tick wait
    // args[2] = custom command
    // args[3] = configurable [argument]
    public boolean runDelayedCommand(List<String> consoleCmds, CommandSender sender, CustomCmdEntry entry, String[] args) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = sender.getName();
        int tickDelay = Integer.parseInt(args[1]);
        DelayedTask delayedTask = new DelayedTask(entry, args);

        javaPlugin.getLogger().info(name + " triggered a customCmdEntry's consoleCommands... (delayed " + tickDelay + " ticks)");
        sender.sendMessage(prefix + ChatColor.YELLOW + "Running delayed custom command in " + tickDelay + " ticks...");
        
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (entry.getCheckInv()) {
                    String checkPlayer = entry.getCheckPlayer();
                    Player target = null;
                    if (checkPlayer.equalsIgnoreCase("SENDER"))
                        target = (Player) sender;
                    else if (checkPlayer.equalsIgnoreCase("ARGUMENT"))
                        target = javaPlugin.getServer().getPlayer(args[3]);
                    else {
                        javaPlugin.getLogger().info(name + "'s delayed command failed because " + checkPlayer + " is not SENDER or ARGUMENT... Aborting!");
                        sender.sendMessage(prefix + ChatColor.GREEN + "Delayed command failed because " + checkPlayer + " is not SENDER or ARGUMENT... Aborting!");
                        return;
                    }

                    if (target==null) {
                        javaPlugin.getLogger().info(name + "'s delayed command failed because " + target + " is null... Aborting!");
                        sender.sendMessage(prefix + ChatColor.GREEN + "Delayed command failed because " + target + " is null... Aborting!");
                        return;
                    }

                    List<String> bkupConsoleCommands = entry.getBkupConsoleCommands();

                    // check for space
                    if ((target.getInventory().firstEmpty() == -1)) {
                        sender.sendMessage(prefix + ChatColor.GREEN + "Delayed command failed because " + target.getName() + " has no inv space... Running backup command(s) if applicable...");
                        for (String consoleCmd : bkupConsoleCommands) {
                            consoleCmd = consoleCmd.replace("<player>", name);
                            if (args.length == 4) {
                                consoleCmd = consoleCmd.replace("<argument>", args[3]);
                                javaPlugin.getLogger().info(name + " triggered delayed custom command bkup command: <"+args[3]+"> - " + consoleCmd);
                            } else javaPlugin.getLogger().info(name + " triggered delayed custom command bkup command: " + consoleCmd);
                            
                            Bukkit.dispatchCommand(console, consoleCmd);
                        } pendingTasks.remove(delayedTask);
                        return;
                    }
                }
                for (String consoleCmd : consoleCmds) {
                    consoleCmd = consoleCmd.replace("<player>", name);
                    if (args.length == 4) {
                        consoleCmd = consoleCmd.replace("<argument>", args[3]);
                        javaPlugin.getLogger().info(name + " triggered delayed custom command bkup command: <"+args[3]+"> - " + consoleCmd);
                    } else javaPlugin.getLogger().info(name + " triggered delayed custom command bkup command: " + consoleCmd);
                    
                    Bukkit.dispatchCommand(console, consoleCmd);
                } pendingTasks.remove(delayedTask);
            }
        }.runTaskLater(javaPlugin, tickDelay);

        pendingTasks.put(delayedTask, bukkitTask);
        return true;
    }
}
