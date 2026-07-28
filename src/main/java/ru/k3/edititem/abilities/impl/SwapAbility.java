package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

public class SwapAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public SwapAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        double maxDist = plugin.getConfig().getDouble("abilities.swap.max-distance", 16);

        Entity target = player.getTargetEntity((int) maxDist);
        if (!(target instanceof LivingEntity)) {
            player.sendMessage("§cНет цели для обмена!");
            return;
        }

        Location playerLoc = player.getLocation();
        Location targetLoc = target.getLocation();

        player.getWorld().playSound(playerLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
        player.getWorld().spawnParticle(Particle.PORTAL, playerLoc, 30, 0.5, 1, 0.5, 1);
        player.getWorld().spawnParticle(Particle.PORTAL, targetLoc, 30, 0.5, 1, 0.5, 1);

        player.teleport(targetLoc);
        target.teleport(playerLoc);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return player.getTargetEntity(16) instanceof LivingEntity;
    }

    @Override
    public AbilityType getType() { return AbilityType.SWAP; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}