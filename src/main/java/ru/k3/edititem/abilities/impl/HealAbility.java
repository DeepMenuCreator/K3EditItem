package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

public class HealAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public HealAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        double amount = abilityData.getDamage(); // используем damage как heal amount
        double radius = abilityData.getRadius();

        Location center = player.getLocation();

        // Лечим себя
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(player.getHealth() + amount, maxHealth));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0);

        // Лечим союзников
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity living = (LivingEntity) entity;
                double theirMax = living.getAttribute(Attribute.MAX_HEALTH).getValue();
                living.setHealth(Math.min(living.getHealth() + amount * 0.5, theirMax));
                living.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, living.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0);
            }
        }

        player.getWorld().playSound(center, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.5f);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return player.getHealth() < player.getAttribute(Attribute.MAX_HEALTH).getValue();
    }

    @Override
    public AbilityType getType() { return AbilityType.HEAL; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}