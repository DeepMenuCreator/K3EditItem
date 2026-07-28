package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

import java.util.ArrayList;
import java.util.List;

public class ChainAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public ChainAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int maxTargets = plugin.getConfig().getInt("abilities.chain.max-targets", 5);
        double damage = abilityData.getDamage();
        double bounceDist = plugin.getConfig().getDouble("abilities.chain.bounce-distance", 8);

        Entity currentTarget = player.getTargetEntity(32);
        if (!(currentTarget instanceof LivingEntity)) {
            player.sendMessage("§cНет цели для цепной молнии!");
            return;
        }

        List<LivingEntity> hit = new ArrayList<>();
        hit.add((LivingEntity) currentTarget);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 2.0f);

        // Первая цель
        ((LivingEntity) currentTarget).damage(damage, player);
        drawChain(player.getLocation(), currentTarget.getLocation());

        // Цепочка
        for (int i = 1; i < maxTargets; i++) {
            LivingEntity next = findNearest((LivingEntity) currentTarget, bounceDist, hit);
            if (next == null) break;

            next.damage(damage * (1 - i * 0.15), player);
            drawChain(currentTarget.getLocation(), next.getLocation());
            hit.add(next);
            currentTarget = next;
        }
    }

    private LivingEntity findNearest(LivingEntity from, double radius, List<LivingEntity> exclude) {
        LivingEntity nearest = null;
        double closest = Double.MAX_VALUE;

        for (Entity entity : from.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof LivingEntity && !exclude.contains(entity)) {
                double dist = entity.getLocation().distance(from.getLocation());
                if (dist < closest) {
                    closest = dist;
                    nearest = (LivingEntity) entity;
                }
            }
        }
        return nearest;
    }

    private void drawChain(Location from, Location to) {
        Location current = from.clone();
        Vector dir = to.toVector().subtract(from.toVector()).normalize();
        double dist = from.distance(to);

        for (double d = 0; d < dist; d += 0.5) {
            current.add(dir.clone().multiply(0.5));
            current.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, current, 2, 0.1, 0.1, 0.1, 0);
            current.getWorld().spawnParticle(Particle.END_ROD, current, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return player.getTargetEntity(32) instanceof LivingEntity;
    }

    @Override
    public AbilityType getType() { return AbilityType.CHAIN; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}