package dev.tbm00.spigot.command64.model;

import java.util.List;

public class JoinCmdEntry {
    private String perm;
    private Boolean permValue;
    private List<String> consoleCommands;

    public JoinCmdEntry(String perm, Boolean permValue, List<String> consoleCommands) {
        this.perm = perm;
        this.permValue = permValue;
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

    public List<String> getConsoleCommands() {
        return consoleCommands;
    }

    public void setConsoleCommands(List<String> consoleCommands) {
        this.consoleCommands = consoleCommands;
    }
}