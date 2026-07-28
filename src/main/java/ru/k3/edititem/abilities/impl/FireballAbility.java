package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

public class FireballAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public FireballAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        double damage = abilityData.getDamage();
        double speed = plugin.getConfig().getDouble("abilities.fireball.speed", 1.5);
        float power = (float) plugin.getConfig().getDouble("abilities.fireball.explosion-power", 2.0);

        Location eye = player.getEyeLocation();
        Fireball fireball = player.getWorld().spawn(eye, Fireball.class, fb -> {
            fb.setVelocity(eye.getDirection().multiply(speed));
            fb.setYield(power);
            fb.setIsIncendiary(true);
            fb.setShooter(player);
        });

        player.getWorld().playSound(eye, Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.0f);

        // Анимация полета
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (fireball.isDead() || ticks > 100) {
                    cancel();
                    return;
                }
                fireball.getWorld().spawnParticle(Particle.FLAME, fireball.getLocation(), 5, 0.2, 0.2, 0.2, 0.01);
                fireball.getWorld().spawnParticle(Particle.LAVA, fireball.getLocation(), 2, 0.1, 0.1, 0.1, 0);
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.FIREBALL; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}