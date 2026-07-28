package ru.k3.edititem.animations.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleTrail {

    public static void play(Player player, int tick) {
        if (player.getVelocity().length() > 0.1) {
            Location loc = player.getLocation().add(0, 1, 0);
            player.getWorld().spawnParticle(Particle.CLOUD, loc, 2, 0.2, 0.2, 0.2, 0.01);
        }
    }
}