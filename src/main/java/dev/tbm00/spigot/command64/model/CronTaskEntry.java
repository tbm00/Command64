package dev.tbm00.spigot.command64.model;

public class CronTaskEntry {
    private String timing;
    private String consoleCommand;

    public CronTaskEntry(String timing, String consoleCommand) {
        this.timing = timing;
        this.consoleCommand = consoleCommand;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getConsoleCommand() {
        return consoleCommand;
    }

    public void setConsoleCommand(String consoleCommand) {
        this.consoleCommand = consoleCommand;
    }
}