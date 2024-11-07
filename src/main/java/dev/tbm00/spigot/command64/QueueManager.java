package dev.tbm00.spigot.command64;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Iterator;


import dev.tbm00.spigot.command64.model.Reward;

public class QueueManager {
    private final CommandRunner cmdRunner;
    private final Map<String, Queue<Reward>> rewardQueues;

    public QueueManager(CommandRunner cmdRunner) {
        this.cmdRunner = cmdRunner;
        this.rewardQueues = new HashMap<>();
    }

    // get or make player's queue and add reward to it
    public boolean enqueueReward(String playerName, List<String> consoleCommands, boolean invCheck) {
        Reward reward = new Reward(consoleCommands, invCheck);
        Queue<Reward> queue = rewardQueues.computeIfAbsent(playerName, k -> new LinkedList<>());
        queue.add(reward);
        return true;
    }

    public int getPlayersQueueSize(String playerName) {
        Queue<Reward> queue = rewardQueues.get(playerName);
        return (queue != null) ? queue.size() : 0;
    }

    // dequeue and run commands
    public boolean redeemReward(String playerName, boolean checkInvSpace) {
        Queue<Reward> queue = rewardQueues.get(playerName);

        if (queue == null || queue.isEmpty()) {
            return false;
        }

        if (checkInvSpace) { // redeem first rewards since there is space
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
