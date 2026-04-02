package com.nightwatcher;

import com.nightwatcher.listeners.EerieSoundListener;
import com.nightwatcher.listeners.MobSpawnListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class NightWatcher extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save default config.yml if it doesn't exist
        saveDefaultConfig();

        // Register event listeners
        getServer().getPluginManager().registerEvents(new MobSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new EerieSoundListener(this), this);

        getLogger().info("NightWatcher has been enabled. The night grows darker...");
    }

    @Override
    public void onDisable() {
        getLogger().info("NightWatcher has been disabled.");
    }
}