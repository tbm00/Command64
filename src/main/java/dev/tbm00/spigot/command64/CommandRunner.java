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

import dev.tbm00.spigot.command64.model.TimerCmdEntry;
import dev.tbm00.spigot.command64.model.TimerTask;

public class CommandRunner {
    private final JavaPlugin javaPlugin;
    private final ConsoleCommandSender console;
    private final Map<TimerTask, BukkitTask> pendingTasks;

    public CommandRunner(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.console = Bukkit.getServer().getConsoleSender();
        this.pendingTasks = new HashMap<>();
    }

    public boolean runJoinCommand(List<String> consoleCmds, Player player, long tickDelay) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = player.getName();
        javaPlugin.getLogger().info(name + " triggered a joinCmdEntry...");

        Bukkit.getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> {
            if (!player.isOnline()) {
                javaPlugin.getLogger().warning(name + " disconnected before joinCmdEntry operated!");
                return;
            }

            List<String> processedCmds = new ArrayList<>();
            for (String consoleCmd : consoleCmds)
                processedCmds.add(consoleCmd.replace("<player>", name));

            Bukkit.getScheduler().runTask(javaPlugin, () -> {
                for (String processedCmd : processedCmds) {
                    Bukkit.dispatchCommand(console, processedCmd);
                    javaPlugin.getLogger().info(name + " triggered join command: " + processedCmd);
                }
            });
        }, tickDelay);
        
        return true;
    }

    public boolean runItemCommand(List<String> consoleCmds, Player player) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = player.getName();
        javaPlugin.getLogger().info(name + " used an itemCmdEntry...");

        for (String consoleCmd : consoleCmds) {
            consoleCmd = consoleCmd.replace("<player>", name);

            Bukkit.dispatchCommand(console, consoleCmd);
            javaPlugin.getLogger().info(name + " ran item command: " + consoleCmd);
        }
        return true;
    }

    public boolean runCustomCommand(List<String> consoleCmds, CommandSender sender, String argument) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = sender.getName();
        javaPlugin.getLogger().info(name + " used a customCmdEntry...");
        sender.sendMessage(ChatColor.YELLOW + "Running custom command...");

        for (String consoleCmd : consoleCmds) {
            consoleCmd = consoleCmd.replace("<sender>", name);
            if (argument != null)
                consoleCmd = consoleCmd.replace("<argument>", argument);

            Bukkit.dispatchCommand(console, consoleCmd);
            javaPlugin.getLogger().info(name + " ran custom command: " + consoleCmd);
            sender.sendMessage(ChatColor.GREEN + "Ran custom command: " + consoleCmd);
        }
        return true;
    }

    // args[0] = "timer"
    // args[1] = tick wait
    // args[2] = custom command
    // args[3] = configurable [argument]
    public boolean runTimerCommand(List<String> consoleCmds, CommandSender sender, TimerCmdEntry entry, String[] args) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = sender.getName();
        int tickDelay = Integer.parseInt(args[1]);
        TimerTask timerTask = new TimerTask(entry, args);
        
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                javaPlugin.getLogger().info(name + " used a timerCmdEntry...");
                sender.sendMessage(ChatColor.YELLOW + "Running timer command...");
                if (entry.getCheckInv()) {
                    String checkPlayer = entry.getCheckPlayer();
                    Player target = null;
                    if (checkPlayer.equalsIgnoreCase("SENDER"))
                        target = (Player) sender;
                    else if (checkPlayer.equalsIgnoreCase("ARGUMENT"))
                        target = javaPlugin.getServer().getPlayer(args[3]);
                    else {
                        javaPlugin.getLogger().info(name + "'s ran timer command failed because " + checkPlayer + " is not SENDER or ARGUMENT... Aborting!");
                        sender.sendMessage(ChatColor.GREEN + "Timer command failed because " + checkPlayer + " is not SENDER or ARGUMENT... Aborting!");
                        return;
                    }

                    if (target==null) {
                        javaPlugin.getLogger().info(name + "'s ran timer command failed because " + target + " is null... Aborting!");
                        sender.sendMessage(ChatColor.GREEN + "Timer command failed because " + target + " is null... Aborting!");
                        return;
                    }

                    List<String> bkupConsoleCommands = entry.getBkupConsoleCommands();

                    // check for space
                    if ((target.getInventory().firstEmpty() == -1)) {
                        for (String consoleCmd : bkupConsoleCommands) {
                            consoleCmd = consoleCmd.replace("<sender>", name);
                            if (args.length == 4)
                                consoleCmd = consoleCmd.replace("<argument>", args[3]);
        
                            Bukkit.dispatchCommand(console, consoleCmd);
                            javaPlugin.getLogger().info(name + " ran timer command: " + consoleCmd);
                            sender.sendMessage(ChatColor.GREEN + "Ran timer command: " + consoleCmd);
                        } pendingTasks.remove(timerTask);
                        javaPlugin.getLogger().info(name + "'s ran timer command failed because " + target.getName() + " is has no inv space... Running backup command(s): " + bkupConsoleCommands);
                        sender.sendMessage(ChatColor.GREEN + "Timer command failed because " + target.getName() + " is has no inv space... Running backup command(s): " + bkupConsoleCommands);
                        return;
                    }
                }
                for (String consoleCmd : consoleCmds) {
                    consoleCmd = consoleCmd.replace("<sender>", name);
                    if (args.length == 4)
                        consoleCmd = consoleCmd.replace("<argument>", args[3]);

                    Bukkit.dispatchCommand(console, consoleCmd);
                    javaPlugin.getLogger().info(name + " ran timer command: " + consoleCmd);
                    sender.sendMessage(ChatColor.GREEN + "Ran timer command: " + consoleCmd);
                } pendingTasks.remove(timerTask);
            }
        }.runTaskLater(javaPlugin, tickDelay);

        pendingTasks.put(timerTask, bukkitTask);
        javaPlugin.getLogger().info(name + " is running timer commands in " + tickDelay + " ticks...");
        sender.sendMessage(ChatColor.YELLOW + "Running timer commands in " + tickDelay + " ticks...");
        return true;
    }
}
