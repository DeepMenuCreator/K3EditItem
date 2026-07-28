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

public class LightningAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public LightningAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        double damage = abilityData.getDamage();
        int chainCount = plugin.getConfig().getInt("abilities.lightning.chain-count", 3);
        double radius = abilityData.getRadius();

        org.bukkit.block.Block targetBlock = player.getTargetBlockExact(32);
        Location targetLoc = targetBlock != null ? targetBlock.getLocation() : player.getLocation().add(player.getLocation().getDirection().multiply(10));

        List<LivingEntity> hitEntities = new ArrayList<>();
        Location currentLoc = targetLoc;

        for (int i = 0; i < chainCount; i++) {
            currentLoc.getWorld().strikeLightningEffect(currentLoc);
            currentLoc.getWorld().playSound(currentLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);

            for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 2, 2, 2)) {
                if (entity instanceof LivingEntity && entity != player && !hitEntities.contains(entity)) {
                    LivingEntity living = (LivingEntity) entity;
                    living.damage(damage, player);
                    hitEntities.add(living);
                    currentLoc = living.getLocation();
                    break;
                }
            }

            currentLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, currentLoc, 30, 1, 1, 1, 0.5);
        }
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.LIGHTNING; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}