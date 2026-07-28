package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReflectAbility implements AbilityExecutor, Listener {

    private final K3EditItem plugin;
    private final Map<UUID, Long> activeReflects = new HashMap<>();
    private final Map<UUID, Double> reflectChances = new HashMap<>();

    public ReflectAbility(K3EditItem plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int duration = abilityData.getDuration();
        double chance = plugin.getConfig().getDouble("abilities.reflect.reflect-chance", 0.7);

        activeReflects.put(player.getUniqueId(), System.currentTimeMillis() + duration * 50L);
        reflectChances.put(player.getUniqueId(), chance);

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 2.0f);

        // Визуальный эффект зеркала
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= duration || !player.isOnline()) {
                    activeReflects.remove(player.getUniqueId());
                    reflectChances.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                Location loc = player.getLocation().add(0, 1, 0);
                for (int i = 0; i < 6; i++) {
                    double angle = ticks * 0.3 + (Math.PI * 2 * i / 6);
                    double x = Math.cos(angle) * 1.0;
                    double z = Math.sin(angle) * 1.0;
                    player.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(x, 0, z), 1, 0, 0, 0, 0);
                }
                ticks += 2;
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        Long endTime = activeReflects.get(player.getUniqueId());
        if (endTime == null || System.currentTimeMillis() > endTime) return;

        double chance = reflectChances.getOrDefault(player.getUniqueId(), 0.7);
        if (Math.random() > chance) return;

        if (event.getDamager() instanceof LivingEntity) {
            LivingEntity damager = (LivingEntity) event.getDamager();
            damager.damage(event.getDamage(), player);
            event.setCancelled(true);

            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 2.0f);
            player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.5);
        }
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        Long endTime = activeReflects.get(player.getUniqueId());
        return endTime == null || System.currentTimeMillis() > endTime;
    }

    @Override
    public AbilityType getType() { return AbilityType.REFLECT; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}