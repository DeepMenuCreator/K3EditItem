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

public class BlackHoleAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public BlackHoleAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int duration = abilityData.getDuration();
        double radius = abilityData.getRadius();
        double pullStrength = plugin.getConfig().getDouble("abilities.black-hole.pull-strength", 0.5);
        double damage = plugin.getConfig().getDouble("abilities.black-hole.damage", 2.0);

        Location target = player.getTargetBlockExact(32);
        if (target == null) target = player.getLocation().add(player.getLocation().getDirection().multiply(10));

        player.getWorld().playSound(target, Sound.ENTITY_WITHER_SPAWN, 0.5f, 0.2f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration) {
                    // Взрыв при исчезновении
                    target.getWorld().createExplosion(target, 2.0f, false, false, player);
                    cancel();
                    return;
                }

                // Спираль внутрь
                for (int i = 0; i < 16; i++) {
                    double angle = ticks * 0.5 + (Math.PI * 2 * i / 16);
                    double r = radius * (1 - (ticks % 10) / 10.0);
                    double x = Math.cos(angle) * r;
                    double z = Math.sin(angle) * r;
                    target.getWorld().spawnParticle(Particle.DRAGON_BREATH, target.clone().add(x, 0.5, z), 1, 0, 0, 0, 0);
                }
                target.getWorld().spawnParticle(Particle.SQUID_INK, target, 5, 0.5, 0.5, 0.5, 0.1);

                // Засасывание
                for (Entity entity : target.getWorld().getNearbyEntities(target, radius, radius, radius)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        Vector pull = target.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(pullStrength);
                        entity.setVelocity(entity.getVelocity().add(pull));
                        if (entity.getLocation().distance(target) < 2) {
                            ((LivingEntity) entity).damage(damage, player);
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.BLACK_HOLE; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}