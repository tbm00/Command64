package dev.tbm00.spigot.command64;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.chat.TextComponent;

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

    public boolean runSudoCommand(CommandSender sender, String[] args) {
        String targetName = null, cmd = null;
        if (args.length==3) {
            targetName = args[1];
            cmd = args[2];
            cmd = cmd.replace("_", " ");
            cmd = cmd.replace("<me>", sender.getName());
        } else { 
            sendMessage(sender, ChatColor.RED + "Sudo requires a command sender and a command.");
            if (sender.hasPermission("command64.sudo.console")) {
                sendMessage(sender, ChatColor.WHITE + "Ex Usage: /cmd sudo CONSOLE say_hello_world");
                sendMessage(sender, ChatColor.WHITE + "Ex Usage: /cmd sudo CONSOLE say_<me>_says_hello");
            } else if (sender.hasPermission("command64.sudo.player")) {
                sendMessage(sender, ChatColor.WHITE + "Ex Usage: /cmd sudo CONSOLE say_<me>_says_hello");
                sendMessage(sender, ChatColor.WHITE + "Ex Usage: /cmd sudo Notch pay_Steve_10000");
                sendMessage(sender, ChatColor.WHITE + "Ex Usage: /cmd sudo Notch pay_<me>_10000");
            }
            return false;
        }

        // get target
        CommandSender target = null;
        if (targetName.equalsIgnoreCase("CONSOLE") || targetName.equalsIgnoreCase("_CONSOLE_")) {
            if (!sender.hasPermission("command64.sudo.console")) return false;
            target = console;
        } else {
            if (!sender.hasPermission("command64.sudo.player")) return false; 
            target = javaPlugin.getServer().getPlayer(targetName);
        } if (target==null) {
            sendMessage(sender, ChatColor.RED + "Couldn't find target: " + target);
            return false;
        }

        javaPlugin.getLogger().info(sender.getName() + " triggered sudo command: <"+targetName+"> " + cmd);
        sendMessage(sender, ChatColor.YELLOW + "Running sudo command: " + ChatColor.WHITE + "<"+targetName+"> " + cmd);
        Bukkit.dispatchCommand(target, cmd);
        return true;
    }

    public boolean runCronCommand(String timing, String consoleCmd) {
        if (consoleCmd == null || consoleCmd.isEmpty()) return false;
        
        javaPlugin.getLogger().info("CronSchedule triggered a taskEntry: " + timing + " " + consoleCmd);
        Bukkit.dispatchCommand(console, consoleCmd);
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
                argument = argument.replace("_", " ");
                consoleCmd = consoleCmd.replace("<argument>", argument);
                javaPlugin.getLogger().info(player + " triggered reward command: <"+argument+"> " + consoleCmd);
            } else javaPlugin.getLogger().info(player + " triggered reward command: " + consoleCmd);

            Bukkit.dispatchCommand(console, consoleCmd);
        }
        return true;
    }

    // args[0] = custom command
    // args[1] = configurable [argument]
    // args[2] = configurable [argument2]
    public boolean runCustomCommand(CustomCmdEntry entry, CommandSender sender, String[] args) {
        List<String> consoleCmds = entry.getConsoleCommands();
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String senderName = sender.getName();
        javaPlugin.getLogger().info(senderName + " triggered a customCmdEntry's consoleCommands...");
        sendMessage(sender, ChatColor.YELLOW + "Running custom command...");

        if (entry.getCheckInv()) {
            String checkPlayer = entry.getCheckPlayer();
            Player target = null;

            // get target
            if (checkPlayer.equalsIgnoreCase("SENDER"))
                target = (Player) sender;
            else if (checkPlayer.equalsIgnoreCase("ARGUMENT"))
                target = javaPlugin.getServer().getPlayer(args[1]);
            else {
                sendMessage(sender, ChatColor.RED + "Custom command failed because " + checkPlayer + " is not SENDER or ARGUMENT... Aborting!");
                return false;
            } if (target==null) {
                sendMessage(sender, ChatColor.RED + "Custom command failed because " + target + " is null... Aborting!");
                return false;
            }

            // if target doesnt have inv space, run backup commands
            List<String> bkupConsoleCommands = entry.getBkupConsoleCommands();
            if ((target.getInventory().firstEmpty() == -1)) {
                sendMessage(sender, ChatColor.YELLOW + "Custom command failed because " + target.getName() + " has no inv space... Running backup command(s) if any...");
                runConsoleCmds(bkupConsoleCommands, senderName, args[1], args[2]);
                return true;
            }
        }
        runConsoleCmds(consoleCmds, senderName, args[1], args[2]);
        return true;
    }

    // args[0] = "delayed"
    // args[1] = tick wait
    // args[2] = custom command
    // args[3] = configurable [argument]
    // args[4] = configurable [argument2]
    public boolean runDelayedCommand(CustomCmdEntry entry, CommandSender sender, String[] args) {
        List<String> consoleCmds = entry.getConsoleCommands();
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = sender.getName();
        int tickDelay = Integer.parseInt(args[1]);
        DelayedTask delayedTask = new DelayedTask(entry, args);

        javaPlugin.getLogger().info(name + " triggered a customCmdEntry's consoleCommands... (delayed " + tickDelay + " ticks)");
        sendMessage(sender, ChatColor.YELLOW + "Running delayed custom command in " + tickDelay + " ticks...");
        
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (entry.getCheckInv()) {
                    String checkPlayer = entry.getCheckPlayer();
                    Player target = null;

                    // get target
                    if (checkPlayer.equalsIgnoreCase("SENDER"))
                        target = (Player) sender;
                    else if (checkPlayer.equalsIgnoreCase("ARGUMENT"))
                        target = javaPlugin.getServer().getPlayer(args[3]);
                    else {
                        sendMessage(sender, ChatColor.RED + "Delayed command failed because " + checkPlayer + " is not SENDER or ARGUMENT... Aborting!");
                        return;
                    } if (target==null) {
                        sendMessage(sender, ChatColor.RED + "Delayed command failed because " + target + " is null... Aborting!");
                        return;
                    }

                    // if target doesnt have inv space, run backup commands
                    List<String> bkupConsoleCommands = entry.getBkupConsoleCommands();
                    if ((target.getInventory().firstEmpty() == -1)) {
                        sendMessage(sender, ChatColor.YELLOW + "Delayed command failed because " + target.getName() + " has no inv space... Running backup command(s) if any...");
                        runConsoleCmds(bkupConsoleCommands, name, args[3], args[4]);
                        pendingTasks.remove(delayedTask);
                        return;
                    }
                }
                runConsoleCmds(consoleCmds, name, args[3], args[4]);
                pendingTasks.remove(delayedTask);
            }
        }.runTaskLater(javaPlugin, tickDelay);

        pendingTasks.put(delayedTask, bukkitTask);
        return true;
    }

    private void runConsoleCmds(List<String> consoleCmds, String senderName, String argument, String argument2) {
        String randomPlayer = getRandomPlayer();
        for (String consoleCmd : consoleCmds) {
            consoleCmd = consoleCmd.replace("<player>", senderName);
            consoleCmd = consoleCmd.replace("<random_player>", randomPlayer);

            if (argument2!=null && argument!=null) {
                consoleCmd = consoleCmd.replace("<argument>", argument);
                argument2 = argument2.replace("_", " ");
                consoleCmd = consoleCmd.replace("<argument2>", argument2);
                javaPlugin.getLogger().info(senderName + " triggered console command: <"+argument+":"+argument2+"> " + consoleCmd);
            } else if (argument!=null) {
                consoleCmd = consoleCmd.replace("<argument>", argument);
                javaPlugin.getLogger().info(senderName + " triggered console command: <"+argument+"> " + consoleCmd);
            } else javaPlugin.getLogger().info(senderName + " triggered console command: " + consoleCmd);
            
            Bukkit.dispatchCommand(console, consoleCmd);
        }
    }

    private void sendMessage(CommandSender target, String string) {
        if (!string.isBlank())
            target.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', prefix + string)));
    }
    
    private String getRandomPlayer() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) return "Notch"; //return null; 
        int randomIndex = new Random().nextInt(players.size());
        int currentIndex = 0;
        for (Player p : players) {
            if (currentIndex == randomIndex) return p.getName();
            currentIndex++;
        }
        return "Notch"; //return null; 
    }
}
