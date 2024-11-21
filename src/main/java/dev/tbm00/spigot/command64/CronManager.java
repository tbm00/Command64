package dev.tbm00.spigot.command64;

import java.util.List;
import java.time.LocalDateTime;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import dev.tbm00.spigot.command64.model.CronTaskEntry;

public class CronManager {
    private final JavaPlugin javaPlugin;
    private final CommandRunner cmdRunner;
    private final List<CronTaskEntry> cronTaskEntries;
    private int taskId = -1;

    public CronManager(JavaPlugin javaPlugin, CommandRunner cmdRunner, ConfigHandler configHandler) {
        this.javaPlugin = javaPlugin;
        this.cmdRunner = cmdRunner;
        cronTaskEntries = configHandler.getCronTaskEntries();

        startCronScheduler();
    }

    private void startCronScheduler() {
        long initialDelay = getInitialDelay();
        javaPlugin.getLogger().info("CronSchedule starting in " + initialDelay / 20 + " seconds!");
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
            @Override
            public void run() {
                checkCronTasks();
            }
        }, initialDelay, 20 * 60);
    }

    private long getInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        int seconds = now.getSecond();
        int millis = now.getNano() / 1000000;
        int delaySeconds = 60 - seconds;
        // 1 tick = 50ms ... 1s = 20 ticks
        return ((delaySeconds * 1000) - millis) / 50; 
    }

    private void checkCronTasks() {
        for (CronTaskEntry entry : cronTaskEntries) {
            if (isCronMatch(entry.getTiming())) {
                cmdRunner.runCronCommand(entry.getTiming(), entry.getConsoleCommand());
            }
        }
    }

    private boolean isCronMatch(String timing) {
        String[] parts = timing.split("\\s+");
        if (parts.length != 5) {
            javaPlugin.getLogger().warning("Invalid cron expression: " + timing);
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        int minute = now.getMinute();
        int hour = now.getHour();
        int dayOfMonth = now.getDayOfMonth();
        int month = now.getMonthValue(); // 1=January thru 12=December
        int dayOfWeek = now.getDayOfWeek().getValue() % 7; // 0=Sunday thru 6=Saturday

        return matchCronField(parts[0], minute, 0, 59) // minutes
            && matchCronField(parts[1], hour, 0, 23) // hours
            && matchCronField(parts[2], dayOfMonth, 1, 31) // day of month
            && matchCronField(parts[3], month, 1, 12) // month
            && matchCronField(parts[4], dayOfWeek, 0, 6); // day of week
    }

    private boolean matchCronField(String field, int value, int minValue, int maxValue) {
        if (field.equals("*")) {
            return true;
        }

        String[] commaParts = field.split(",");
        for (String part : commaParts) {
            if (part.contains("/")) { // step values
                String[] slashParts = part.split("/");
                String rangePart = slashParts[0];
                int step = Integer.parseInt(slashParts[1]);

                int start = minValue;
                int end = maxValue;

                if (!rangePart.equals("*")) {
                    if (rangePart.contains("-")) { // ranged values within step
                        String[] range = rangePart.split("-");
                        start = Integer.parseInt(range[0]);
                        end = Integer.parseInt(range[1]);
                    } else {
                        start = Integer.parseInt(rangePart);
                    }
                }

                for (int i = start; i <= end; i += step) {
                    if (i == value) {
                        return true;
                    }
                }
            } else if (part.contains("-")) { // ranged value
                String[] range = part.split("-");
                int start = Integer.parseInt(range[0]);
                int end = Integer.parseInt(range[1]);
                if (value >= start && value <= end) {
                    return true;
                }
            } else { // exact value
                int val = Integer.parseInt(part);
                if (val == value) {
                    return true;
                }
            }
        }
        return false;
    }

    public void shutdown() {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
    }
}
