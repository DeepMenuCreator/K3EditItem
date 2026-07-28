package ru.k3.edititem.animations.impl;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class PulseAnimation {

    public static void play(Player player, int tick) {
        if (tick % 20 == 0) {
            Location loc = player.getLocation().add(0, 1, 0);
            player.getWorld().spawnParticle(Particle.DUST, loc, 30, 1.5, 1, 1.5, 0,
                new Particle.DustOptions(Color.RED, 2.0f));
        }
    }
}