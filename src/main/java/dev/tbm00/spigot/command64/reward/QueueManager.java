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
    private final CommandRunner cmdRunner;
    private final Map<String, Queue<Reward>> rewardQueues;
    private final JSONHandler jsonHandler;
    private BukkitTask saveTask;

    public QueueManager(JavaPlugin javaPlugin, CommandRunner cmdRunner, ConfigHandler configHandler) {
        this.cmdRunner = cmdRunner;
        this.rewardQueues = new HashMap<>();
        this.jsonHandler = new JSONHandler(javaPlugin);

        Map<String, Queue<Reward>> loadedQueues = jsonHandler.loadRewards();
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
    public boolean enqueueReward(String playerName, List<String> consoleCommands, boolean invCheck) {
        Reward reward = new Reward(consoleCommands, invCheck);
        synchronized (rewardQueues) {
            Queue<Reward> queue = rewardQueues.computeIfAbsent(playerName, k -> new LinkedList<>());
            queue.add(reward);
        }
        return true;
    }

    public int getPlayersQueueSize(String playerName) {
        synchronized (rewardQueues) {
            Queue<Reward> queue = rewardQueues.get(playerName);
            return (queue != null) ? queue.size() : 0;
        }
    }

    // dequeue and run commands
    public boolean redeemReward(String playerName, boolean checkInvSpace) {
        synchronized (rewardQueues) {
            Queue<Reward> queue = rewardQueues.get(playerName);

            if (queue == null || queue.isEmpty()) {
                return false;
            }

            if (checkInvSpace) { // redeem first rewards since there is space
                // Player has inventory space, redeem the first reward
                Reward reward = queue.poll();
                if (reward != null) {
                    cmdRunner.runRewardCommand(reward.getConsoleCommands(), playerName);
                    return true;
                }
            } else { // redeem the first reward that doesn't require space
                Iterator<Reward> iterator = queue.iterator();
                while (iterator.hasNext()) {
                    Reward reward = iterator.next();
                    if (!reward.isInvCheck()) {
                        iterator.remove();
                        cmdRunner.runRewardCommand(reward.getConsoleCommands(), playerName);
                        return true;
                    }
                }
            }
            return false;
        }
    }
}