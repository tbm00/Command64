package dev.tbm00.spigot.command64.reward;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Queue;
import java.io.Reader;
import java.io.FileReader;
import java.io.Writer;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import org.bukkit.plugin.java.JavaPlugin;

public class JSONHandler {
    private final JavaPlugin javaPlugin;
    private final File jsonFile;
    private final Gson gson;

    public JSONHandler(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.gson = new Gson();

        if (!javaPlugin.getDataFolder().exists()) javaPlugin.getDataFolder().mkdirs();
        this.jsonFile = new File(javaPlugin.getDataFolder(), "player_rewards.json");
        try {
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
                try (Writer writer = new FileWriter(jsonFile)) {
                    gson.toJson(new HashMap<String, Queue<String>>(), writer);
                }
            }
        } catch (IOException e) {
            javaPlugin.getLogger().severe("Could not create player_rewards.json: " + e.getMessage());
        }
    }

    // load rewards from JSON
    public Map<String, Queue<String>> loadRewards() {
        try (Reader reader = new FileReader(jsonFile)) {
            Type type = new TypeToken<Map<String, Queue<String>>>() {}.getType();
            Map<String, Queue<String>> rewardQueues = gson.fromJson(reader, type);
            if (rewardQueues == null)
                return new HashMap<>();
            return rewardQueues;
        } catch (IOException e) {
            javaPlugin.getLogger().severe("Could not read player_rewards.json: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public void saveRewards(Map<String, Queue<String>> rewardQueues) {
        // make copy of rewardQueues to avoid concurrent modification
        Map<String, Queue<String>> rewardQueuesCopy = new HashMap<>();
        synchronized (rewardQueues) {
            for (Map.Entry<String, Queue<String>> entry : rewardQueues.entrySet()) {
                rewardQueuesCopy.put(entry.getKey(), new LinkedList<>(entry.getValue()));
            }
        }

        CompletableFuture.runAsync(() -> {
            try (Writer writer = new FileWriter(jsonFile)) {
                gson.toJson(rewardQueuesCopy, writer);
            } catch (IOException e) {
                javaPlugin.getLogger().severe("Could not write to player_rewards.json: " + e.getMessage());
            }
        });
    }
}