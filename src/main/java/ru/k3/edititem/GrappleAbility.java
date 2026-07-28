package ru.k3.edititem.abilities.impl;

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

public class GrappleAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public GrappleAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        double maxDist = plugin.getConfig().getDouble("abilities.grapple.max-distance", 24);
        double pullSpeed = plugin.getConfig().getDouble("abilities.grapple.pull-speed", 0.8);

        Location eye = player.getEyeLocation();
        Entity target = null;
        double closest = Double.MAX_VALUE;

        for (Entity entity : player.getNearbyEntities(maxDist, maxDist, maxDist)) {
            if (entity instanceof LivingEntity && entity != player) {
                double dist = entity.getLocation().distance(eye);
                if (dist < closest && eye.getDirection().angle(entity.getLocation().toVector().subtract(eye.toVector()).normalize()) < 0.3) {
                    closest = dist;
                    target = entity;
                }
            }
        }

        if (target == null) {
            player.sendMessage("§cЦель не найдена!");
            return;
        }

        final Entity finalTarget = target;
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1.0f, 0.5f);

        // Цепь
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 20 || finalTarget.isDead()) {
                    cancel();
                    return;
                }
                Location mid = player.getLocation().add(finalTarget.getLocation()).multiply(0.5);
                player.getWorld().spawnParticle(Particle.CRIT, mid, 3, 0.2, 0.2, 0.2, 0);
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);

        // Притягивание
        Vector pull = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(pullSpeed);
        target.setVelocity(pull);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.GRAPPLE; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}