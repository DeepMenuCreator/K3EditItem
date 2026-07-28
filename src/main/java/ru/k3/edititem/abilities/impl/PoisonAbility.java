package ru.k3.edititem.abilities.impl;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class PoisonAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public PoisonAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int duration = abilityData.getDuration();
        int amplifier = plugin.getConfig().getInt("abilities.poison.amplifier", 1);
        double radius = abilityData.getRadius();
        Location center = player.getLocation();

        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity living = (LivingEntity) entity;
                living.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier, false, true));
                living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 0, false, true));
            }
        }

        center.getWorld().playSound(center, Sound.ENTITY_SPLASH_POTION_BREAK, 1.0f, 0.8f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }
                center.getWorld().spawnParticle(Particle.SNEEZE, center, 15, radius, 1, radius, 0.05);
                center.getWorld().spawnParticle(Particle.DUST, center, 10, radius, 1, radius, 0, 
                    new Particle.DustOptions(Color.GREEN, 1.5f));
                ticks += 5;
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.POISON; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}