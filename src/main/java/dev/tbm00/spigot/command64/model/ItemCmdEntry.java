package dev.tbm00.spigot.command64.model;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCmdEntry {
    private String givePerm;
    private Boolean givePermValue;
    private String usePerm;
    private Boolean usePermValue;
    private List<String> consoleCommands;
    private NamespacedKey key;
    private String keyString;
    private String name;
    private String item;
    private Boolean glowing;
    private List<String> lore;

    public ItemCmdEntry(JavaPlugin javaPlugin, String givePerm, Boolean givePermValue, String usePerm, Boolean usePermValue, 
                        List<String> consoleCommands, String KEY, String name, String item, Boolean glowing, List<String> lore) {
        this.givePerm = givePerm;
        this.givePermValue = givePermValue;
        this.usePerm = usePerm;
        this.usePermValue = usePermValue;
        this.consoleCommands = consoleCommands;
        this.key = new NamespacedKey(javaPlugin, KEY);
        this.keyString = KEY;
        this.name = name;
        this.item = item;
        this.glowing = glowing;
        this.lore = lore;
    }

    public String getGivePerm() {
        return givePerm;
    }

    public void setGivePerm(String givePerm) {
        this.givePerm = givePerm;
    }

    public Boolean getGivePermValue() {
        return givePermValue;
    }

    public void setGivePermValue(Boolean givePermValue) {
        this.givePermValue = givePermValue;
    }

    public String getUsePerm() {
        return usePerm;
    }

    public void setUsePerm(String usePerm) {
        this.usePerm = usePerm;
    }

    public Boolean getUsePermValue() {
        return usePermValue;
    }

    public void setUsePermValue(Boolean usePermValue) {
        this.usePermValue = usePermValue;
    }

    public List<String> getConsoleCommands() {
        return consoleCommands;
    }

    public void setConsoleCommands(List<String> consoleCommands) {
        this.consoleCommands = consoleCommands;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public String getKeyString() {
        return keyString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Boolean getGlowing() {
        return glowing;
    }

    public void setGlowing(Boolean glowing) {
        this.glowing = glowing;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}