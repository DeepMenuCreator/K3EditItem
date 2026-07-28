package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

public class ShieldAbility implements AbilityExecutor {

    private final K3EditItem plugin;
    private final Map<UUID, Long> activeShields = new HashMap<>();

    public ShieldAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int duration = abilityData.getDuration();

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 2, false, true));
        activeShields.put(player.getUniqueId(), System.currentTimeMillis() + duration * 50L);

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 2.0f);

        // Визуальный щит
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration || !player.isOnline()) {
                    cancel();
                    activeShields.remove(player.getUniqueId());
                    return;
                }
                Location loc = player.getLocation().add(0, 1, 0);
                double angle = ticks * 0.2;
                for (int i = 0; i < 8; i++) {
                    double a = angle + (Math.PI * 2 * i / 8);
                    double x = Math.cos(a) * 1.2;
                    double z = Math.sin(a) * 1.2;
                    player.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(x, 0, z), 1, 0, 0, 0, 0);
                }
                ticks += 2;
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        return !activeShields.containsKey(player.getUniqueId()) || 
               System.currentTimeMillis() > activeShields.get(player.getUniqueId());
    }

    @Override
    public AbilityType getType() { return AbilityType.SHIELD; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}