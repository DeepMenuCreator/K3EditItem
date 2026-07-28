package ru.k3.edititem.abilities.impl;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

public class LaserAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public LaserAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        double damage = abilityData.getDamage();
        double maxDist = plugin.getConfig().getDouble("abilities.laser.max-distance", 50);
        boolean penetration = plugin.getConfig().getBoolean("abilities.laser.penetration", true);

        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection().normalize();

        player.getWorld().playSound(eye, Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 2.0f);

        new BukkitRunnable() {
            double dist = 0;
            boolean hit = false;
            @Override
            public void run() {
                if (dist >= maxDist || (!penetration && hit)) {
                    cancel();
                    return;
                }
                Location point = eye.clone().add(direction.clone().multiply(dist));
                point.getWorld().spawnParticle(Particle.END_ROD, point, 3, 0.05, 0.05, 0.05, 0);
                point.getWorld().spawnParticle(Particle.DUST, point, 2, 0.05, 0.05, 0.05, 0,
                    new Particle.DustOptions(Color.RED, 1.0f));

                for (Entity entity : point.getWorld().getNearbyEntities(point, 1, 1, 1)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        ((LivingEntity) entity).damage(damage, player);
                        entity.setFireTicks(20);
                        if (!penetration) hit = true;
                    }
                }
                dist += 0.5;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.LASER; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}