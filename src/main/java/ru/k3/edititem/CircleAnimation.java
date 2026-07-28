package ru.k3.edititem.animations.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.k3.edititem.K3EditItem;

public class CircleAnimation {

    public static void play(Player player, int tick) {
        Location loc = player.getLocation().add(0, 1, 0);
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(tick + (i * 45));
            double x = Math.cos(angle) * 1.2;
            double z = Math.sin(angle) * 1.2;
            player.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(x, 0, z), 1, 0, 0, 0, 0);
        }
    }
}