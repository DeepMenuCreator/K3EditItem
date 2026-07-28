package ru.k3.edititem.abilities.impl;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BerserkAbility implements AbilityExecutor {

    private final K3EditItem plugin;
    private final Map<UUID, Long> activeBerserks = new HashMap<>();

    public BerserkAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int duration = abilityData.getDuration();
        double dmgMult = plugin.getConfig().getDouble("abilities.berserk.damage-multiplier", 2.0);
        double speedMult = plugin.getConfig().getDouble("abilities.berserk.speed-multiplier", 1.5);

        activeBerserks.put(player.getUniqueId(), System.currentTimeMillis() + duration * 50L);

        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, (int) (dmgMult - 1), false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, (int) (speedMult - 1), false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 0, false, true));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_ANGRY, 1.0f, 0.5f);

        // Красная аура
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration || !player.isOnline()) {
                    activeBerserks.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0,
                    new org.bukkit.Particle.DustOptions(org.bukkit.Color.RED, 1.5f));
                ticks += 5;
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        Long endTime = activeBerserks.get(player.getUniqueId());
        return endTime == null || System.currentTimeMillis() > endTime;
    }

    @Override
    public AbilityType getType() { return AbilityType.BERSERK; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}