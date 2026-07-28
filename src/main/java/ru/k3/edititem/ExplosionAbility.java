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

public class ExplosionAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public ExplosionAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        float power = (float) plugin.getConfig().getDouble("abilities.explosion.power", 3.0);
        boolean breakBlocks = plugin.getConfig().getBoolean("abilities.explosion.break-blocks", false);
        boolean fire = plugin.getConfig().getBoolean("abilities.explosion.fire", false);

        Location target = player.getTargetBlockExact(32);
        if (target == null) target = player.getLocation().add(player.getLocation().getDirection().multiply(8));

        target.getWorld().createExplosion(target, power, fire, breakBlocks, player);
        target.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, target, 1, 0, 0, 0, 0);
        target.getWorld().playSound(target, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.EXPLOSION; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}