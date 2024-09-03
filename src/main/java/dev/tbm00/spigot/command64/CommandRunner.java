package dev.tbm00.spigot.command64;

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

    public boolean runJoinCommand(List<String> consoleCmds, Player player) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = player.getName();
        System.out.println(name + " used a joinCmdEntry...");

        for (String consoleCmd : consoleCmds) {
            consoleCmd = consoleCmd.replace("<player>", name);

            Bukkit.dispatchCommand(console, consoleCmd);
            System.out.println(name + " ran join command: " + consoleCmd);
        }
        return true;
    }

    public boolean runItemCommand(List<String> consoleCmds, Player player) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = player.getName();
        System.out.println(name + " used an itemCmdEntry...");

        for (String consoleCmd : consoleCmds) {
            consoleCmd = consoleCmd.replace("<player>", name);

            Bukkit.dispatchCommand(console, consoleCmd);
            System.out.println(name + " ran item command: " + consoleCmd);
        }
        return true;
    }

    public boolean runCustomCommand(List<String> consoleCmds, CommandSender sender, String argument) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = sender.getName();
        System.out.println(name + " used a customCmdEntry...");
        sender.sendMessage(ChatColor.YELLOW + "Running custom command...");

        for (String consoleCmd : consoleCmds) {
            consoleCmd = consoleCmd.replace("<player>", name);
            if (argument != null)
                consoleCmd = consoleCmd.replace("<argument>", argument);

            Bukkit.dispatchCommand(console, consoleCmd);
            System.out.println(name + " ran custom command: " + consoleCmd);
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
        int tickWait = Integer.parseInt(args[1]);
        TimerTask timerTask = new TimerTask(entry, args);
        
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println(name + " used a timerCmdEntry...");
                sender.sendMessage(ChatColor.YELLOW + "Running timer command...");

                //List<String> consoleCmds = timerTask.getTimerCmdEntry().getConsoleCommands(); // not really needed
                //String[] args = timerTask.getArgs();
                for (String consoleCmd : consoleCmds) {
                    if (args.length == 4)
                        consoleCmd = consoleCmd.replace("<argument>", args[3]);

                    Bukkit.dispatchCommand(console, consoleCmd);
                    System.out.println(name + " ran timer command: " + consoleCmd);
                    sender.sendMessage(ChatColor.GREEN + "Ran timer command: " + consoleCmd);
                } pendingTasks.remove(timerTask);
            }
        }.runTaskLater(javaPlugin, tickWait);

        pendingTasks.put(timerTask, bukkitTask);
        System.out.println(name + " is running timer commands in " + tickWait + " ticks...");
        sender.sendMessage(ChatColor.GREEN + "Running timer commands in " + tickWait + " ticks...");
        return true;
    }
}
