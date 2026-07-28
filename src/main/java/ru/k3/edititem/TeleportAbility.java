package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

public class TeleportAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public TeleportAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        double distance = plugin.getConfig().getDouble("abilities.teleport.distance", 32);
        boolean safe = plugin.getConfig().getBoolean("abilities.teleport.safe-landing", true);

        Location from = player.getLocation();
        Location to = from.clone().add(from.getDirection().multiply(distance));

        if (safe) {
            to = to.getWorld().getHighestBlockAt(to).getLocation().add(0, 1, 0);
        }

        player.getWorld().spawnParticle(Particle.PORTAL, from, 50, 0.5, 1, 0.5, 1);
        player.getWorld().playSound(from, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        player.teleport(to);

        player.getWorld().spawnParticle(Particle.PORTAL, to, 50, 0.5, 1, 0.5, 1);
        player.getWorld().playSound(to, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.TELEPORT; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}