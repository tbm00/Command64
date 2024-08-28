package dev.tbm00.spigot.command64.model;

public class TimerTask {
    private final TimerCmdEntry timerCmdEntry;
    private final String[] args;
     
    public TimerTask(TimerCmdEntry entry, String[] args) {
        timerCmdEntry = entry;
        this.args = args;
    }

    public TimerCmdEntry getTimerCmdEntry() {
        return timerCmdEntry;
    }

    public String[] getArgs() {
        return args;
    }
}
