package me.animepdf.fastsellcmi.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BukkitRunner {
    final JavaPlugin javaPlugin;

    public BukkitRunner(JavaPlugin plugin) {
        this.javaPlugin = plugin;
    }

    public void run(Runnable task) {
        Bukkit.getScheduler().runTask(javaPlugin, task);
    }
    public void runLater(Runnable task, long delay) {
        Bukkit.getScheduler().runTaskLater(javaPlugin, task, delay);
    }
}
