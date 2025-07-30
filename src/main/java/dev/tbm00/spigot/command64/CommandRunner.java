package dev.tbm00.spigot.command64;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.chat.TextComponent;

import dev.tbm00.spigot.command64.model.CustomCmdEntry;
import dev.tbm00.spigot.command64.model.DelayedTask;

public class CommandRunner {
    private final Command64 javaPlugin;
    private final ConsoleCommandSender console;
    private final Map<DelayedTask, BukkitTask> pendingTasks;
    private final String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "cmd" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;

    public CommandRunner(Command64 javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.console = Bukkit.getServer().getConsoleSender();
        this.pendingTasks = new HashMap<>();
    }

    public boolean triggerJoinCommands(List<String> consoleCmds, Player player, long tickDelay, String type) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;
        String name = player.getName();

        Bukkit.getScheduler().runTaskLater(javaPlugin, () -> {
            if (!player.isOnline()) return;

            //javaPlugin.getLogger().info(name + " triggered a joinCmdEntry's "+type+"consoleCommands...");
            runConsoleCmds(consoleCmds, name, null, null);
        }, tickDelay);
        
        return true;
    }

    public boolean triggerSudoCommand(CommandSender sender, String[] args) {
        String targetName = null, cmd = null;
        if (args.length==3) {
            targetName = args[1];
            cmd = args[2];
            cmd = cmd.replace("+", " ");
        } else { 
            sendMessage(sender, ChatColor.RED + "Sudo requires a command sender and a command.");
            if (sender.hasPermission("command64.sudo.console")) {
                sendMessage(sender, ChatColor.WHITE + "Ex Usage: /cmd sudo CONSOLE say+hello+world");
            } else if (sender.hasPermission("command64.sudo.player")) {
                sendMessage(sender, ChatColor.WHITE + "Ex Usage: /cmd sudo Notch pay+Steve+10000");
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
            if (targetName.equalsIgnoreCase("RANDOM_PLAYER")) {
                target = javaPlugin.getRandomPlayer(sender.getName());
            } else {
                target = javaPlugin.getServer().getPlayer(targetName);
            }
        } 
        
        if (target==null) {
            sendMessage(sender, ChatColor.RED + "Couldn't find target: " + targetName);
            return false;
        } else {
            targetName = target.getName();
        }

        javaPlugin.getLogger().info(sender.getName() + " triggered sudo command: <"+targetName+"> " + cmd);
        sendMessage(sender, ChatColor.YELLOW + "Running sudo command: " + ChatColor.WHITE + "<"+targetName+"> " + cmd);
        runPlayerCmd(target, cmd);
        return true;
    }

    public boolean triggerCronCommand(String timing, String consoleCmd) {
        if (consoleCmd == null || consoleCmd.isEmpty()) return false;
        
        if (consoleCmd.contains("<random_player>") || consoleCmd.contains("<random_uuid>")) {
            Player randomPlayer = javaPlugin.getRandomPlayer("null");
            consoleCmd = consoleCmd.replace("<random_player>", randomPlayer.getName());
            consoleCmd = consoleCmd.replace("<random_uuid>", randomPlayer.getUniqueId().toString());
        }
        
        javaPlugin.getLogger().info("CronSchedule triggered a taskEntry: " + timing + " " + consoleCmd);
        runPlayerCmd(console, consoleCmd);
        return true;
    }

    public boolean triggerItemCommands(List<String> consoleCmds, Player player) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String name = player.getName();
        //javaPlugin.getLogger().info(name + " triggered an itemCmdEntry's consoleCommands...");
        runConsoleCmds(consoleCmds, name, null, null);

        return true;
    }

    public boolean triggerRewardCommands(List<String> consoleCmds, String player, String argument) {
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        //javaPlugin.getLogger().info(player + " triggered a rewardEntry's consoleCommands...");
        runConsoleCmds(consoleCmds, player, argument, null);
        
        return true;
    }

    // args[0] = custom command
    // args[1] = configurable [argument]
    // args[2] = configurable [argument2]
    public boolean triggerCustomCmdEntry(CustomCmdEntry entry, CommandSender sender, String[] args) {
        List<String> consoleCmds = entry.getConsoleCommands();
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String senderName = sender.getName();
        String argument = args.length > 1 ? args[1] : "";
        String argument2 = args.length > 2 ? args[2] : "";
        
        //javaPlugin.getLogger().info(senderName + " triggered a customCmdEntry's consoleCommands...");
        sendMessage(sender, ChatColor.YELLOW + "Running custom command...");

        if (entry.getCheckOnline()) {
            String checkPlayer = entry.getCheckOnlinePlayer();
            Player target = null;

            // get target
            if (checkPlayer.equalsIgnoreCase("SENDER"))
                target = (Player) sender;
            else if (checkPlayer.equalsIgnoreCase("ARGUMENT"))
                target = javaPlugin.getServer().getPlayer(argument);
            else {
                sendMessage(sender, ChatColor.RED + "Custom command failed because " + checkPlayer + " is not SENDER or ARGUMENT... Aborting!");
                return false;
            }

            // if target isnt online, run backup commands
            if (target==null || !target.isOnline()) {
                sendMessage(sender, ChatColor.YELLOW + "Custom command failed because " + checkPlayer + " is not online... Running backup command(s) if any...");
                runConsoleCmds(entry.getCheckOnlineConsoleCommands(), senderName, argument, argument2);
                return true;
            }
        }

        if (entry.getCheckInv()) {
            String checkPlayer = entry.getCheckInvPlayer();
            Player target = null;

            // get target
            if (checkPlayer.equalsIgnoreCase("SENDER"))
                target = (Player) sender;
            else if (checkPlayer.equalsIgnoreCase("ARGUMENT"))
                target = javaPlugin.getServer().getPlayer(argument);
            else {
                sendMessage(sender, ChatColor.RED + "Custom command failed because " + checkPlayer + " is not SENDER or ARGUMENT... Aborting!");
                return false;
            } if (target==null) {
                sendMessage(sender, ChatColor.RED + "Custom command failed because " + target + " is null... Aborting!");
                return false;
            }

            // if target doesnt have inv space, run backup commands
            if ((target.getInventory().firstEmpty() == -1)) {
                sendMessage(sender, ChatColor.YELLOW + "Custom command failed because " + target.getName() + " has no inv space... Running backup command(s) if any...");
                runConsoleCmds(entry.getCheckInvConsoleCommands(), senderName, argument, argument2);
                return true;
            }
        }
        runConsoleCmds(consoleCmds, senderName, argument, argument2);
        return true;
    }

    // args[0] = "delayed"
    // args[1] = tick wait
    // args[2] = custom command
    // args[3] = configurable [argument]
    // args[4] = configurable [argument2]
    public boolean triggerDelayedCmdEntry(CustomCmdEntry entry, CommandSender sender, String[] args) {
        List<String> consoleCmds = entry.getConsoleCommands();
        if (consoleCmds == null || consoleCmds.isEmpty()) return false;

        String senderName = sender.getName();
        String argument = args.length > 3 ? args[3] : "";
        String argument2 = args.length > 4 ? args[4] : "";
        int tickDelay;
        try {
            tickDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sendMessage(sender, ChatColor.RED + "Invalid tick delay: " + args[1]);
            return false;
        }
        DelayedTask delayedTask = new DelayedTask(entry, args);

        //javaPlugin.getLogger().info(senderName + " triggered a customCmdEntry's consoleCommands... (delayed " + tickDelay + " ticks)");
        sendMessage(sender, ChatColor.YELLOW + "Running delayed custom command in " + tickDelay + " ticks...");
        
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (entry.getCheckInv()) {
                    String checkPlayer = entry.getCheckInvPlayer();
                    Player target = null;

                    // get target
                    if (checkPlayer.equalsIgnoreCase("SENDER"))
                        target = (Player) sender;
                    else if (checkPlayer.equalsIgnoreCase("ARGUMENT"))
                        target = javaPlugin.getServer().getPlayer(argument);
                    else {
                        sendMessage(sender, ChatColor.RED + "Delayed command failed because " + checkPlayer + " is not SENDER or ARGUMENT... Aborting!");
                        return;
                    } if (target==null) {
                        sendMessage(sender, ChatColor.RED + "Delayed command failed because " + target + " is null... Aborting!");
                        return;
                    }

                    // if target doesnt have inv space, run backup commands
                    List<String> bkupConsoleCommands = entry.getCheckInvConsoleCommands();
                    if ((target.getInventory().firstEmpty() == -1)) {
                        sendMessage(sender, ChatColor.YELLOW + "Delayed command failed because " + target.getName() + " has no inv space... Running backup command(s) if any...");
                        runConsoleCmds(bkupConsoleCommands, senderName, argument, argument2);
                        pendingTasks.remove(delayedTask);
                        return;
                    }
                }
                runConsoleCmds(consoleCmds, senderName, argument, argument2);
                pendingTasks.remove(delayedTask);
            }
        }.runTaskLater(javaPlugin, tickDelay);

        pendingTasks.put(delayedTask, bukkitTask);
        return true;
    }

    private void runConsoleCmds(List<String> consoleCmds, String senderName, String argument, String argument2) {
        if (consoleCmds == null || consoleCmds.isEmpty()) {
            javaPlugin.getLogger().warning(senderName + " triggered NULL console commands!");
            return;
        }

        Player randomPlayer = javaPlugin.getRandomPlayer(senderName);
        String senderUuid;
        try {
            senderUuid = Bukkit.getServer().getPlayer(senderName).getUniqueId().toString();
        } catch (Exception e) {senderUuid=null;}

        for (String consoleCmd : consoleCmds) {
            consoleCmd = consoleCmd.replace("<player>", senderName);
            if (senderUuid!=null) consoleCmd = consoleCmd.replace("<player_uuid>", senderUuid);
            consoleCmd = consoleCmd.replace("<random_player>", randomPlayer.getName());
            consoleCmd = consoleCmd.replace("<random_uuid>", randomPlayer.getUniqueId().toString());

            if ((argument!=null)&&(argument2!=null) && !argument.isBlank() && !argument2.isEmpty()) {
                consoleCmd = consoleCmd.replace("<argument>", argument);
                argument2 = argument2.replace("+", " ");
                consoleCmd = consoleCmd.replace("<argument2>", argument2);
                javaPlugin.getLogger().info(senderName + " triggered console command: <"+argument+":"+argument2+"> " + consoleCmd);
            } else if (argument!=null && !argument.isBlank()) {
                consoleCmd = consoleCmd.replace("<argument>", argument);
                javaPlugin.getLogger().info(senderName + " triggered console command: <"+argument+"> " + consoleCmd);
            } else javaPlugin.getLogger().info(senderName + " triggered console command: " + consoleCmd);
            
            Bukkit.dispatchCommand(console, consoleCmd);
        }
    }

    private void runPlayerCmd(CommandSender sender, String consoleCmd) {
        if (consoleCmd == null || consoleCmd.isBlank()) {
            javaPlugin.getLogger().warning(sender.getName() + " triggered NULL console command!");
            return;
        }
        String randomPlayer = javaPlugin.getRandomPlayerName(sender.getName());
        consoleCmd = consoleCmd.replace("RANDOM_PLAYER", randomPlayer);

        if (consoleCmd!=null) {
            Bukkit.dispatchCommand(sender, consoleCmd);
        }
    }

    private void sendMessage(CommandSender target, String string) {
        if (!string.isBlank())
            target.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', prefix + string)));
    }

}
