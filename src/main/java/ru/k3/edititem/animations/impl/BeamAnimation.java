package ru.k3.edititem.animations.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class BeamAnimation {

    public static void play(Player player, int tick) {
        Location loc = player.getLocation().add(0, 1, 0);
        for (double y = 0; y < 3; y += 0.5) {
            player.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(0, y, 0), 1, 0.1, 0, 0.1, 0);
        }
    }
}