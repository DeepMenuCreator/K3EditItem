package ru.k3.edititem.animations;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnimationManager {

    private final K3EditItem plugin;
    private final Map<UUID, AnimationType> playerAnimations;
    private final Map<UUID, BukkitRunnable> activeRunnables;

    public AnimationManager(K3EditItem plugin) {
        this.plugin = plugin;
        this.playerAnimations = new HashMap<>();
        this.activeRunnables = new HashMap<>();
        startGlobalAnimationLoop();
    }

    private void startGlobalAnimationLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    AnimationType anim = playerAnimations.get(player.getUniqueId());
                    if (anim != null) {
                        playAnimationFrame(player, anim);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    public void setPlayerAnimation(Player player, AnimationType type) {
        playerAnimations.put(player.getUniqueId(), type);
    }

    public void removePlayerAnimation(Player player) {
        playerAnimations.remove(player.getUniqueId());
    }

    public AnimationType getPlayerAnimation(Player player) {
        return playerAnimations.get(player.getUniqueId());
    }

    public void playAbilityAnimation(Player player, AbilityType ability) {
        Location loc = player.getLocation().add(0, 1, 0);

        switch (ability) {
            case FIREBALL -> {
                for (int i = 0; i < 8; i++) {
                    double angle = Math.PI * 2 * i / 8;
                    loc.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(Math.cos(angle), 0, Math.sin(angle)), 2, 0, 0, 0, 0.1);
                }
            }
            case LIGHTNING -> {
                loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 30, 1, 1, 1, 0.5);
            }
            case TELEPORT -> {
                loc.getWorld().spawnParticle(Particle.PORTAL, loc, 50, 0.5, 1, 0.5, 1);
            }
            case HEAL -> {
                loc.getWorld().spawnParticle(Particle.HEART, loc, 15, 1, 1, 1, 0);
            }
            case EXPLOSION -> {
                loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 5, 0.5, 0.5, 0.5, 0.1);
            }
            case METEOR -> {
                loc.getWorld().spawnParticle(Particle.LAVA, loc, 20, 1, 1, 1, 0.3);
            }
            case BLACK_HOLE -> {
                loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 30, 1, 1, 1, 0.1);
            }
            default -> {
                loc.getWorld().spawnParticle(Particle.WITCH, loc, 10, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }

    private void playAnimationFrame(Player player, AnimationType type) {
        Location loc = player.getLocation().add(0, 1, 0);
        int tick = (int) (System.currentTimeMillis() / 100) % 360;

        switch (type) {
            case CIRCLE -> {
                for (int i = 0; i < 8; i++) {
                    double angle = Math.toRadians(tick + (i * 45));
                    double x = Math.cos(angle) * 1.2;
                    double z = Math.sin(angle) * 1.2;
                    player.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(x, 0, z), 1, 0, 0, 0, 0);
                }
            }
            case SPIRAL -> {
                double angle = Math.toRadians(tick * 3);
                double x = Math.cos(angle) * 0.8;
                double z = Math.sin(angle) * 0.8;
                double y = (tick % 40) / 20.0;
                player.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
            }
            case PULSE -> {
                if (tick % 20 == 0) {
                    player.getWorld().spawnParticle(Particle.DUST, loc, 30, 1.5, 1, 1.5, 0,
                        new org.bukkit.Particle.DustOptions(org.bukkit.Color.RED, 2.0f));
                }
            }
            case BEAM -> {
                for (double y = 0; y < 3; y += 0.5) {
                    player.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(0, y, 0), 1, 0.1, 0, 0.1, 0);
                }
            }
            case AURA -> {
                player.getWorld().spawnParticle(Particle.DUST, loc, 3, 0.5, 0.5, 0.5, 0,
                    new org.bukkit.Particle.DustOptions(org.bukkit.Color.AQUA, 1.0f));
            }
            case TRAIL -> {
                if (player.getVelocity().length() > 0.1) {
                    player.getWorld().spawnParticle(Particle.CLOUD, loc, 2, 0.2, 0.2, 0.2, 0.01);
                }
            }
            case RINGS -> {
                double radius = 1 + Math.sin(Math.toRadians(tick * 2)) * 0.5;
                for (int i = 0; i < 12; i++) {
                    double a = Math.PI * 2 * i / 12;
                    player.getWorld().spawnParticle(Particle.FIREWORK, loc.clone().add(Math.cos(a) * radius, 0, Math.sin(a) * radius), 1, 0, 0, 0, 0);
                }
            }
            case STAR -> {
                for (int i = 0; i < 5; i++) {
                    double a1 = Math.toRadians(tick + (i * 72));
                    double a2 = Math.toRadians(tick + ((i + 2) * 72));
                    Location p1 = loc.clone().add(Math.cos(a1) * 1.2, 0, Math.sin(a1) * 1.2);
                    Location p2 = loc.clone().add(Math.cos(a2) * 1.2, 0, Math.sin(a2) * 1.2);
                    // Линия между точками
                    for (double t = 0; t <= 1; t += 0.2) {
                        Location mid = p1.clone().add(p2.toVector().subtract(p1.toVector()).multiply(t));
                        player.getWorld().spawnParticle(Particle.GLOW, mid, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }
}