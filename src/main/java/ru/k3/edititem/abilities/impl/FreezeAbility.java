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
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

public class FreezeAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public FreezeAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        double radius = abilityData.getRadius();
        int duration = abilityData.getDuration();
        Location center = player.getLocation();

        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity living = (LivingEntity) entity;
                living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 10, false, true));
                living.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, duration, 5, false, true));
                living.getWorld().spawnParticle(Particle.SNOWFLAKE, living.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
            }
        }

        center.getWorld().playSound(center, Sound.BLOCK_GLASS_BREAK, 1.0f, 0.5f);

        // Визуальный эффект заморозки
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }
                for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        entity.getWorld().spawnParticle(Particle.SNOWFLAKE, entity.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0);
                    }
                }
                ticks += 5;
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.FREEZE; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}