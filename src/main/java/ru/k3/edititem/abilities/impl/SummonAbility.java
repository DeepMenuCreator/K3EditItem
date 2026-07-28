package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

import java.util.ArrayList;
import java.util.List;

public class SummonAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public SummonAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        String entityTypeStr = plugin.getConfig().getString("abilities.summon.entity-type", "ZOMBIE");
        int count = plugin.getConfig().getInt("abilities.summon.count", 3);
        int duration = plugin.getConfig().getInt("abilities.summon.duration", 300);

        EntityType type;
        try {
            type = EntityType.valueOf(entityTypeStr);
        } catch (IllegalArgumentException e) {
            type = EntityType.ZOMBIE;
        }

        Location spawnLoc = player.getLocation();
        List<Mob> summons = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Location offset = spawnLoc.clone().add(
                Math.cos(Math.PI * 2 * i / count) * 2,
                0,
                Math.sin(Math.PI * 2 * i / count) * 2
            );

            if (type.getEntityClass() != null && Mob.class.isAssignableFrom(type.getEntityClass())) {
                Mob mob = (Mob) player.getWorld().spawnEntity(offset, type);
                mob.setCustomName("§6Призыв §7" + player.getName());
                mob.setCustomNameVisible(true);

                // Установка цели
                if (player.getTargetEntity(16) instanceof LivingEntity target && target != player) {
                    mob.setTarget(target);
                }
                summons.add(mob);
            }
        }

        spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.0f);
        spawnLoc.getWorld().spawnParticle(Particle.ENCHANT, spawnLoc, 50, 2, 1, 2, 0.5);

        // Исчезновение через время
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Mob mob : summons) {
                    if (mob.isValid()) {
                        mob.getWorld().spawnParticle(Particle.SMOKE, mob.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
                        mob.remove();
                    }
                }
            }
        }.runTaskLater(plugin, duration);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return true;
    }

    @Override
    public AbilityType getType() { return AbilityType.SUMMON; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}