package dev.tbm00.spigot.command64.model;

import java.util.List;

public class CmdEntry {
    private String perm;
    private Boolean permValue;
    private List<String> commands;

    public CmdEntry(String perm, Boolean permValue, List<String> commands) {
        this.perm = perm;
        this.permValue = permValue;
        this.commands = commands;
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

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}