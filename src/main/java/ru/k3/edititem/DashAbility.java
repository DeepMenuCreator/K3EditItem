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

public class DashAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public DashAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        double distance = plugin.getConfig().getDouble("abilities.dash.distance", 8);
        double damage = plugin.getConfig().getDouble("abilities.dash.damage-on-impact", 3.0);

        Vector direction = player.getLocation().getDirection().normalize();
        Location start = player.getLocation();

        player.setVelocity(direction.multiply(distance * 0.4));
        player.getWorld().playSound(start, Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.5f);

        // Трейл
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks > 10) {
                    cancel();
                    return;
                }
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 5, 0.3, 0.3, 0.3, 0.05);
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);

        // Урон при столкновении
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : player.getNearbyEntities(1.5, 1.5, 1.5)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        ((LivingEntity) entity).damage(damage, player);
                        entity.setVelocity(direction.multiply(0.8));
                    }
                }
            }
        }.runTaskLater(plugin, 2);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return player.isOnGround();
    }

    @Override
    public AbilityType getType() { return AbilityType.DASH; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}