package dev.tbm00.spigot.command64.model;

public class CommandEntry {
    private String perm;
    private Boolean permValue;
    private String command;

    public CommandEntry(String perm, Boolean permValue, String command) {
        this.perm = perm;
        this.permValue = permValue;
        this.command = command;
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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}