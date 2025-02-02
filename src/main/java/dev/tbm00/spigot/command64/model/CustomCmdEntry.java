package dev.tbm00.spigot.command64.model;

import java.util.List;

public class CustomCmdEntry {
    private String perm;
    private Boolean permValue;
    private String playerCommand;
    private List<String> consoleCommands;
    private Boolean checkInv;
    private String checkPlayer;
    private List<String> bkupConsoleCommands;

    public CustomCmdEntry(String perm, Boolean permValue, String playerCommand, List<String> consoleCommands, Boolean checkInv, String checkPlayer, List<String> bkupConsoleCommands) {
        this.perm = perm;
        this.permValue = permValue;
        this.playerCommand = playerCommand;
        this.consoleCommands = consoleCommands;
        this.checkInv = checkInv;
        this.checkPlayer = checkPlayer;
        this.bkupConsoleCommands = bkupConsoleCommands;
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

    public String getCheckPlayer() {
        return checkPlayer;
    }

    public void getCheckPlayer(String checkPlayer) {
        this.checkPlayer = checkPlayer;
    }

    public List<String> getBkupConsoleCommands() {
        return bkupConsoleCommands;
    }

    public void setBkupConsoleCommands(List<String> bkupConsoleCommands) {
        this.bkupConsoleCommands = bkupConsoleCommands;
    }
}