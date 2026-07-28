package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeStopAbility implements AbilityExecutor {

    private final K3EditItem plugin;
    private final Map<UUID, Vector> frozenVelocities = new HashMap<>();

    public TimeStopAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int duration = abilityData.getDuration();
        double radius = abilityData.getRadius();
        Location center = player.getLocation();

        player.getWorld().playSound(center, Sound.BLOCK_END_PORTAL_SPAWN, 0.5f, 2.0f);

        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity living = (LivingEntity) entity;
                frozenVelocities.put(entity.getUniqueId(), entity.getVelocity().clone());
                entity.setVelocity(new Vector(0, 0, 0));
                living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 10, false, false));
                living.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, duration, 10, false, false));
                living.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, duration, 128, false, false));
                if (living instanceof org.bukkit.entity.Mob) {
                    ((org.bukkit.entity.Mob) living).setAI(false);
                }
            }
        }

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration) {
                    for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            LivingEntity living = (LivingEntity) entity;
                            if (living instanceof org.bukkit.entity.Mob) {
                                ((org.bukkit.entity.Mob) living).setAI(true);
                            }
                            Vector vel = frozenVelocities.remove(entity.getUniqueId());
                            if (vel != null) entity.setVelocity(vel);
                        }
                    }
                    cancel();
                    return;
                }

                for (int i = 0; i < 20; i++) {
                    double angle = Math.PI * 2 * i / 20;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    center.getWorld().spawnParticle(Particle.REVERSE_PORTAL, center.clone().add(x, 0.5, z), 1, 0, 0, 0, 0);
                }
                center.getWorld().spawnParticle(Particle.WITCH, center, 5, radius, 1, radius, 0);
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.TIME_STOP; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}