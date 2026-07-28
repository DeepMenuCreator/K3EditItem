package ru.k3.edititem.animations.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class SpiralAnimation {

    public static void play(Player player, int tick) {
        Location loc = player.getLocation().add(0, 1, 0);
        double angle = Math.toRadians(tick * 3);
        double x = Math.cos(angle) * 0.8;
        double z = Math.sin(angle) * 0.8;
        double y = (tick % 40) / 20.0;
        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
    }
}