package ru.k3.edititem.abilities.impl;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

public class InvisibilityAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public InvisibilityAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int duration = abilityData.getDuration();
        boolean particles = plugin.getConfig().getBoolean("abilities.invisibility.particles", false);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, false, particles));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSER_MIRROR_MOVE, 1.0f, 1.0f);

        // Эффект исчезновения
        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);

        // Периодические частицы
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration || !player.isOnline()) {
                    cancel();
                    // Возвращение видимости
                    player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
                    return;
                }
                if (particles) {
                    player.getWorld().spawnParticle(Particle.WITCH, player.getLocation().add(0, 1, 0), 2, 0.3, 0.3, 0.3, 0);
                }
                ticks += 10;
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return !player.hasPotionEffect(PotionEffectType.INVISIBILITY);
    }

    @Override
    public AbilityType getType() { return AbilityType.INVISIBILITY; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}