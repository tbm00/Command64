package dev.tbm00.spigot.command64.reward;

import java.util.List;

public class Reward {
    private final List<String> consoleCommands;
    private final boolean invCheck;

    public Reward(List<String> consoleCommands, boolean invCheck) {
        this.consoleCommands = consoleCommands;
        this.invCheck = invCheck;
    }

    public List<String> getConsoleCommands() {
        return consoleCommands;
    }

    public boolean isInvCheck() {
        return invCheck;
    }
}