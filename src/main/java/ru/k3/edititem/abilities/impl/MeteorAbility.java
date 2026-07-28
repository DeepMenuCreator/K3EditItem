package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

public class MeteorAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public MeteorAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        double damage = abilityData.getDamage();
        float power = (float) plugin.getConfig().getDouble("abilities.meteor.explosion-power", 4.0);
        int count = plugin.getConfig().getInt("abilities.meteor.count", 1);

        org.bukkit.block.Block targetBlock = player.getTargetBlockExact(48);
        Location target = targetBlock != null ? targetBlock.getLocation() : player.getLocation().add(player.getLocation().getDirection().multiply(20));

        World world = target.getWorld();

        for (int m = 0; m < count; m++) {
            final Location meteorTarget = target.clone().add(
                (Math.random() - 0.5) * 10,
                0,
                (Math.random() - 0.5) * 10
            );
            Location meteorStart = meteorTarget.clone().add(0, 50, 0);

            world.playSound(meteorTarget, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.5f);

            new BukkitRunnable() {
                Location current = meteorStart.clone();
                @Override
                public void run() {
                    if (current.getY() <= meteorTarget.getY() || current.getBlock().getType().isSolid()) {
                        world.createExplosion(current, power, true, true, player);
                        world.spawnParticle(Particle.EXPLOSION_EMITTER, current, 2, 2, 2, 2, 0);
                        world.playSound(current, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);

                        for (Entity entity : world.getNearbyEntities(current, 5, 5, 5)) {
                            if (entity instanceof LivingEntity && entity != player) {
                                ((LivingEntity) entity).damage(damage, player);
                                entity.setFireTicks(100);
                            }
                        }
                        cancel();
                        return;
                    }

                    current.subtract(0, 1, 0);
                    world.spawnParticle(Particle.LAVA, current, 5, 0.5, 0.5, 0.5, 0.1);
                    world.spawnParticle(Particle.FLAME, current, 10, 1, 1, 1, 0.2);
                    world.spawnParticle(Particle.SMOKE, current, 5, 0.5, 0.5, 0.5, 0.05);
                }
            }.runTaskTimer(plugin, m * 10, 1);
        }
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.METEOR; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}