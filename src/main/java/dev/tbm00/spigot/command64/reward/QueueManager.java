package dev.tbm00.spigot.command64.reward;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Iterator;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import dev.tbm00.spigot.command64.CommandRunner;
import dev.tbm00.spigot.command64.ConfigHandler;

public class QueueManager {
    private final JavaPlugin javaPlugin;
    private final CommandRunner cmdRunner;
    private final ConfigHandler configHandler;
    private final Map<String, Queue<String>> rewardQueues;
    private final JSONHandler jsonHandler;
    private BukkitTask saveTask;

    public QueueManager(JavaPlugin javaPlugin, CommandRunner cmdRunner, ConfigHandler configHandler) {
        this.javaPlugin = javaPlugin;
        this.cmdRunner = cmdRunner;
        this.configHandler = configHandler;
        this.rewardQueues = new HashMap<>();
        this.jsonHandler = new JSONHandler(javaPlugin);

        Map<String, Queue<String>> loadedQueues = jsonHandler.loadRewards();
        if (loadedQueues != null) {
            synchronized (rewardQueues) {
                rewardQueues.putAll(loadedQueues);
            }
            javaPlugin.getLogger().info("Loaded player rewards from JSON.");
        }

        // saving to JSON every 30 minutes 36000
        if (configHandler.getSaveDataInterval()>0) {
            long ticks = configHandler.getSaveDataInterval()*1200;
            this.saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(javaPlugin, () -> {
                jsonHandler.saveRewards(rewardQueues);
                javaPlugin.getLogger().info("Saved player rewards to JSON.");
            }, ticks, ticks);
        } else saveTask = null;
    }

    public void shutdown() {
        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel();
        jsonHandler.saveRewards(rewardQueues);
    }

    // get or make player's queue and add reward to it
    public boolean enqueueReward(String playerName, String rewardName, String argument) {
        String stored = null; 
        if (argument!=null && !argument.isBlank())
            stored = rewardName + ":" + argument;
        else stored = rewardName;
        
        synchronized (rewardQueues) {
            Queue<String> queue = rewardQueues.computeIfAbsent(playerName, k -> new LinkedList<>());
            queue.add(stored);
        }
        return true;
    }

    public int getPlayersQueueSize(String playerName) {
        synchronized (rewardQueues) {
            Queue<String> queue = rewardQueues.get(playerName);
            return (queue != null) ? queue.size() : 0;
        }
    }

    // dequeue and run commands
    public boolean redeemReward(String playerName, boolean hasInvSpace) {
        synchronized (rewardQueues) {
            Queue<String> queue = rewardQueues.get(playerName);

            if (queue == null || queue.isEmpty()) {
                return false;
            }

            if (hasInvSpace) { // redeem first rewards since there is space
                // Player has inventory space, redeem the first reward
                String[] reward = queue.poll().split(":");
                String rewardName = reward[0];
                String arg = (reward[1]!=null) ? reward[1] : null; 

                if (rewardName != null) {
                    List<String> consoleCommands = configHandler.getRewardCommandsByName(rewardName);
                    Boolean invCheck = configHandler.getRewardInvCheckByName(rewardName);
                    if (consoleCommands == null || invCheck == null) {
                        javaPlugin.getLogger().warning(playerName + "'s Reward '" + rewardName + "' not found in config..!");
                        return false;
                    }
                    cmdRunner.runRewardCommand(consoleCommands, playerName, arg);
                    return true;
                }
            } else { // redeem the first reward that doesn't require space
                Iterator<String> iterator = queue.iterator();
                while (iterator.hasNext()) {
                    String[] reward = queue.poll().split(":");
                    String rewardName = reward[0];
                    String arg = reward[1];
                    Boolean invCheck = configHandler.getRewardInvCheckByName(rewardName);
                    if (invCheck != null && !invCheck) {
                        iterator.remove();
                        List<String> consoleCommands = configHandler.getRewardCommandsByName(rewardName);
                        if (consoleCommands == null) {
                            javaPlugin.getLogger().warning("Reward '" + rewardName + "' not found in config.");
                            return false;
                        }
                        cmdRunner.runRewardCommand(consoleCommands, playerName, arg);
                        return true;
                    }
                }
            }
            return false;
        }
    }
}