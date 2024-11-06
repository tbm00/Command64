package dev.tbm00.spigot.command64.model;

import java.util.List;

public class RewardCmdEntry {
    private String name;
    private Boolean invCheck;
    private List<String> consoleCommands;

    public RewardCmdEntry(String name, Boolean invCheck, List<String> consoleCommands) {
        this.name = name;
        this.invCheck = invCheck;
        this.consoleCommands = consoleCommands;
    }

    public Boolean getInvCheck() {
        return invCheck;
    }

    public void setInvCheck(Boolean invCheck) {
        this.invCheck = invCheck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getConsoleCommands() {
        return consoleCommands;
    }

    public void setConsoleCommands(List<String> consoleCommands) {
        this.consoleCommands = consoleCommands;
    }
}