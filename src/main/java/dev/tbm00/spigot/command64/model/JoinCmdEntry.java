package dev.tbm00.spigot.command64.model;

import java.util.List;

public class JoinCmdEntry {
    private String perm;
    private Boolean permValue;
    private List<String> consoleCommands;
    private long tickDelay;
    private Boolean checkNewbie;
    private List<String> checkNewbieConsoleCommands;

    public JoinCmdEntry(String perm, Boolean permValue, List<String> consoleCommands, Long tickDelay, Boolean checkNewbie, List<String> checkNewbieConsoleCommands) {
        this.perm = perm;
        this.permValue = permValue;
        this.consoleCommands = consoleCommands;
        this.tickDelay = tickDelay;
        this.checkNewbie = checkNewbie;
        this.checkNewbieConsoleCommands = checkNewbieConsoleCommands;
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

    public long getTickDelay() {
        return tickDelay;
    }

    public void setTickDelay(long tickDelay) {
        this.tickDelay = tickDelay;
    }

    public Boolean getCheckNewbie() {
        return checkNewbie;
    }

    public void setCheckNewbie(Boolean checkNewbie) {
        this.checkNewbie = checkNewbie;
    }

    public List<String> getCheckNewbieConsoleCommands() {
        return checkNewbieConsoleCommands;
    }

    public void setCheckNewbieConsoleCommands(List<String> checkNewbieConsoleCommands) {
        this.checkNewbieConsoleCommands = checkNewbieConsoleCommands;
    }
}