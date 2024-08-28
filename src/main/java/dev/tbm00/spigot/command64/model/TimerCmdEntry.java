package dev.tbm00.spigot.command64.model;

import java.util.List;

public class TimerCmdEntry {
    private String perm;
    private Boolean permValue;
    private String playerCommand;
    private List<String> consoleCommands;

    public TimerCmdEntry(String perm, Boolean permValue, String playerCommand, List<String> consoleCommands) {
        this.perm = perm;
        this.permValue = permValue;
        this.playerCommand = playerCommand;
        this.consoleCommands = consoleCommands;
    }

    public Boolean getPermValue() {
        return permValue;
    }

    public void setPermValue(Boolean permValue) {
        this.permValue = permValue;
    }

    public String getPerm() {
        return perm;
    }

    public void setPerm(String perm) {
        this.perm = perm;
    }

    public String getPlayerCommand() {
        return playerCommand;
    }

    public void setPlayerCommand(String playerCommand) {
        this.playerCommand = playerCommand;
    }

    public List<String> getConsoleCommands() {
        return consoleCommands;
    }

    public void setConsoleCommands(List<String> consoleCommands) {
        this.consoleCommands = consoleCommands;
    }
}