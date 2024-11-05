package dev.tbm00.spigot.command64.model;

public class DelayedTask {
    private final CustomCmdEntry customCmdEntry;
    private final String[] args;
     
    public DelayedTask(CustomCmdEntry entry, String[] args) {
        customCmdEntry = entry;
        this.args = args;
    }

    public CustomCmdEntry getCustomCmdEntry() {
        return customCmdEntry;
    }

    public String[] getArgs() {
        return args;
    }
}
