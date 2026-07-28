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

public class TornadoAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public TornadoAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int duration = abilityData.getDuration();
        double radius = abilityData.getRadius();
        double pullStrength = plugin.getConfig().getDouble("abilities.tornado.pull-strength", 0.3);

        Location target = player.getTargetBlockExact(32);
        if (target == null) target = player.getLocation().add(player.getLocation().getDirection().multiply(10));

        player.getWorld().playSound(target, Sound.ENTITY_BREEZE_WIND_BURST, 1.0f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration) {
                    cancel();
                    return;
                }

                // Вихрь частиц
                for (int i = 0; i < 12; i++) {
                    double angle = ticks * 0.3 + (Math.PI * 2 * i / 12);
                    double r = (ticks % 20) / 20.0 * radius;
                    double x = Math.cos(angle) * r;
                    double z = Math.sin(angle) * r;
                    double y = (ticks % 20) / 5.0;
                    target.getWorld().spawnParticle(Particle.CLOUD, target.clone().add(x, y, z), 1, 0, 0, 0, 0);
                }

                // Притягивание
                for (Entity entity : target.getWorld().getNearbyEntities(target, radius, radius, radius)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        Vector pull = target.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(pullStrength);
                        pull.setY(0.3);
                        entity.setVelocity(entity.getVelocity().add(pull));
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
    public AbilityType getType() { return AbilityType.TORNADO; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}