package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CloneAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public CloneAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int cloneCount = plugin.getConfig().getInt("abilities.clone.clone-count", 2);
        int duration = plugin.getConfig().getInt("abilities.clone.duration", 200);

        List<Location> cloneLocs = new ArrayList<>();

        for (int i = 0; i < cloneCount; i++) {
            double angle = Math.PI * 2 * i / cloneCount;
            Location loc = player.getLocation().add(Math.cos(angle) * 3, 0, Math.sin(angle) * 3);
            cloneLocs.add(loc);

            // Визуал клона
            player.getWorld().spawnParticle(Particle.CLOUD, loc.add(0, 1, 0), 30, 0.3, 0.5, 0.3, 0.1);
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 1.0f);

        // Клоны следуют за игроком
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration || !player.isOnline()) {
                    for (Location loc : cloneLocs) {
                        player.getWorld().spawnParticle(Particle.SMOKE, loc.add(0, 1, 0), 20, 0.3, 0.5, 0.3, 0.1);
                    }
                    cancel();
                    return;
                }

                for (int i = 0; i < cloneCount; i++) {
                    double angle = Math.PI * 2 * i / cloneCount + ticks * 0.05;
                    Location newLoc = player.getLocation().add(Math.cos(angle) * 3, 0, Math.sin(angle) * 3);
                    player.getWorld().spawnParticle(Particle.DUST, newLoc.add(0, 1, 0), 3, 0.2, 0.4, 0.2, 0,
                        new org.bukkit.Particle.DustOptions(org.bukkit.Color.AQUA, 1.0f));
                    player.getWorld().spawnParticle(Particle.WITCH, newLoc, 1, 0.1, 0.1, 0.1, 0);
                }
                ticks += 2;
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.CLONE; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}