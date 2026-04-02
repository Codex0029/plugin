package com.nightwatcher.listeners;

import com.nightwatcher.NightWatcher;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EerieSoundListener implements Listener {

    private final NightWatcher plugin;
    private final Map<UUID, Long> lastSoundCheckMap = new HashMap<>();

    // A collection of creepy ambient sounds
    private final Sound[] eerieSounds = {
            Sound.AMBIENT_CAVE,
            Sound.ENTITY_ENDERMAN_STARE,
            Sound.ENTITY_GHAST_AMBIENT,
            Sound.ENTITY_WOLF_HOWL,
            Sound.ENTITY_PHANTOM_SWOOP,
            Sound.BLOCK_SCULK_SHRIEKER_SHRIEK
    };

    public EerieSoundListener(NightWatcher plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        // Ignore minor movements (camera rotations)
       if (event.getFrom().getBlockX() == event.getTo().getBlockX()
        && event.getFrom().getBlockY() == event.getTo().getBlockY()
        && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
        return;
       }

        Player player = event.getPlayer();
        World world = player.getWorld();

        // Only trigger sounds in the Overworld
        if (world.getEnvironment() != World.Environment.NORMAL) {
            return;
        }

        // Check if it's night
        long time = world.getTime();
        if (time < 13000 || time > 23000) {
            return;
        }

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldown = plugin.getConfig().getLong("sounds.cooldown-ms", 5000L);

        // Throttle checks to prevent spamming random generation
        if (currentTime - lastSoundCheckMap.getOrDefault(playerId, 0L) < cooldown) {
            return;
        }

        // Update last check time
        lastSoundCheckMap.put(playerId, currentTime);

        double chance = plugin.getConfig().getDouble("sounds.chance", 0.15);

        // Roll chance for playing an eerie sound
        if (ThreadLocalRandom.current().nextDouble() <= chance) {
            playEerieSound(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Prevent memory leaks by removing offline players from the cache
        lastSoundCheckMap.remove(event.getPlayer().getUniqueId());
    }

    private void playEerieSound(Player player) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Sound sound = eerieSounds[random.nextInt(eerieSounds.length)];
        
        // Randomize pitch and volume to make it sound different each time
        float pitch = 0.5f + random.nextFloat() * 0.5f; // Deep pitch (0.5 to 1.0)
        float volume = 0.4f + random.nextFloat() * 0.4f; // Medium volume (0.4 to 0.8)

        // Calculate a location slightly offset from the player to simulate ambient origin
        Location soundLoc = player.getLocation().add(
                (random.nextDouble() - 0.5) * 10,
                (random.nextDouble() - 0.5) * 5,
                (random.nextDouble() - 0.5) * 10
        );

        player.playSound(soundLoc, sound, SoundCategory.AMBIENT, volume, pitch);
    }
}
