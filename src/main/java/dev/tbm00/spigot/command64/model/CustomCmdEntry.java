package dev.tbm00.spigot.command64.model;

import java.util.List;

public class CustomCmdEntry {
    private String perm;
    private Boolean permValue;
    private String playerCommand;
    private List<String> consoleCommands;
    private Boolean checkInv;
    private String checkInvPlayer;
    private List<String> checkInvConsoleCommands;
    private Boolean checkOnline;
    private String checkOnlinePlayer;
    private List<String> checkOnlineConsoleCommands;

    public CustomCmdEntry(String perm, Boolean permValue, String playerCommand, List<String> consoleCommands, Boolean checkInv, String checkInvPlayer, List<String> checkInvConsoleCommands, Boolean checkOnline, String checkOnlinePlayer, List<String> checkOnlineConsoleCommands) {
        this.perm = perm;
        this.permValue = permValue;
        this.playerCommand = playerCommand;
        this.consoleCommands = consoleCommands;
        this.checkInv = checkInv;
        this.checkInvPlayer = checkInvPlayer;
        this.checkInvConsoleCommands = checkInvConsoleCommands;
        this.checkOnline = checkOnline;
        this.checkOnlinePlayer = checkOnlinePlayer;
        this.checkOnlineConsoleCommands = checkOnlineConsoleCommands;
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

    public Boolean getCheckInv() {
        return checkInv;
    }

    public void setCheckInv(Boolean checkInv) {
        this.checkInv = checkInv;
    }

    public String getCheckInvPlayer() {
        return checkInvPlayer;
    }

    public void getCheckInvPlayer(String checkInvPlayer) {
        this.checkInvPlayer = checkInvPlayer;
    }

    public List<String> getCheckInvConsoleCommands() {
        return checkInvConsoleCommands;
    }

    public void setCheckInvConsoleCommands(List<String> checkInvConsoleCommands) {
        this.checkInvConsoleCommands = checkInvConsoleCommands;
    }

    public Boolean getCheckOnline() {
        return checkOnline;
    }

    public void setCheckOnline(Boolean checkOnline) {
        this.checkOnline = checkOnline;
    }

    public String getCheckOnlinePlayer() {
        return checkOnlinePlayer;
    }

    public void getCheckOnlinePlayer(String checkOnlinePlayer) {
        this.checkOnlinePlayer = checkOnlinePlayer;
    }

    public List<String> getCheckOnlineConsoleCommands() {
        return checkOnlineConsoleCommands;
    }

    public void setCheckOnlineConsoleCommands(List<String> checkOnlineConsoleCommands) {
        this.checkOnlineConsoleCommands = checkOnlineConsoleCommands;
    }
}