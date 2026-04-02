package com.nightwatcher.listeners;

import com.nightwatcher.NightWatcher;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.concurrent.ThreadLocalRandom;

public class MobSpawnListener implements Listener {

    private final NightWatcher plugin;

    public MobSpawnListener(NightWatcher plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // We only want to process NATURAL spawns. This automatically prevents 
        // infinite loops when we use SpawnReason.CUSTOM for our duplicates.
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        LivingEntity entity = event.getEntity();
        World world = entity.getWorld();

        // Only increase spawns in the Overworld where day/night cycles happen
        if (world.getEnvironment() != World.Environment.NORMAL) {
            return;
        }

        // Only affect hostile mobs (Monsters)
        if (!(entity instanceof Monster)) {
            return;
        }

        // Check if it is nighttime (13000 to 23000 ticks)
        long time = world.getTime();
        if (time < 13000 || time > 23000) {
            return;
        }

        double spawnChance = plugin.getConfig().getDouble("extra-spawn-chance", 0.50);
        
        // Roll the chance to duplicate the spawn
        if (ThreadLocalRandom.current().nextDouble() <= spawnChance) {
            // Spawn an identical mob at the exact location. 
            // Vanilla entity collision will naturally push them apart.
            // Using SpawnReason.CUSTOM ensures this event listener won't infinitely trigger itself.
            world.spawnEntity(entity.getLocation(), entity.getType());
        }
    }
}