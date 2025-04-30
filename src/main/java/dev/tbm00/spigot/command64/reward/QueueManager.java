package dev.tbm00.spigot.command64.reward;

import java.util.LinkedList;
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
                synchronized (rewardQueues) {
                    jsonHandler.saveRewards(rewardQueues);
                }
                javaPlugin.getLogger().info("Saved player rewards to JSON.");
            }, ticks, ticks);
        } else saveTask = null;
    }

    public void shutdown() {
        if (saveTask != null && !saveTask.isCancelled()) saveTask.cancel();
        synchronized (rewardQueues) {
            jsonHandler.saveRewards(rewardQueues);
        }
    }

    // get or make player's queue and add reward to it
    public boolean enqueueReward(String playerName, String rewardName, String argument) {
        String stored = null; 
        if (argument!=null && !argument.isBlank())
            stored = rewardName + ":" + argument.replace("+"," ");
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
            if (queue == null) {
                return false;
            } if (queue.isEmpty()) {
                rewardQueues.remove(playerName);
                return false;
            }

            // first, try the head
            String firstRewardEntry = queue.peek();
            String[] firstParts = firstRewardEntry.split(":", 2);
            String firstRewardName = firstParts[0];
            String firstArg = firstParts.length>1 ? firstParts[1] : null;
            boolean firstInvCheck = Boolean.TRUE.equals(configHandler.getRewardInvCheckByName(firstRewardName));
            if (!firstInvCheck || hasInvSpace) {
                if (triggerRewardCommands(playerName, firstRewardName, firstArg)) {
                    queue.poll();
                    return true;
                } else return false;
            } else {
                // head requires inv space & player doesnt have it
                // start looping through the rest of the queue to run the first entry without an invCheck
                Iterator<String> iter = queue.iterator();
                iter.next(); // skip the head, we already tried it
    
                while (iter.hasNext()) {
                    String rewardEntry = iter.next();
                    String[] parts = rewardEntry.split(":", 2);
                    String rewardName = parts[0];
                    String arg = parts.length > 1 ? parts[1] : null;
                    boolean invCheck = Boolean.TRUE.equals(configHandler.getRewardInvCheckByName(rewardName));
    
                    // only pick those with invCheck == false
                    if (!invCheck) {
                        if (triggerRewardCommands(playerName, rewardName, arg)) {
                            iter.remove();
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        javaPlugin.getLogger().warning("Error: Ran out of queued rewards... no rewards triggered: " + playerName + " hasInvSpace:" + hasInvSpace);
        return false;
    }

    private boolean triggerRewardCommands(String playerName, String rewardName, String arg) {
        if (rewardName != null) {
            if (cmdRunner.runRewardCommands(configHandler.getRewardCommandsByName(rewardName), playerName, arg)) {
                return true;
            } else {
                javaPlugin.getLogger().warning("Error: 'consoleCommands' is null or empty for rewardEntry: " + rewardName);
                return false;
            }
        } else {
            javaPlugin.getLogger().warning("Error: rewardEntry's 'name' is null or empty for the reward " + playerName + " is trying to redeem!");
            return false;
        }
    }
}