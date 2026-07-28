package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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

public class PhoenixAbility implements AbilityExecutor, Listener {

    private final K3EditItem plugin;
    private final Map<UUID, Boolean> hasPhoenix = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public PhoenixAbility(K3EditItem plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        int cooldown = plugin.getConfig().getInt("abilities.phoenix.cooldown", 1200);

        Long cdEnd = cooldowns.get(player.getUniqueId());
        if (cdEnd != null && System.currentTimeMillis() < cdEnd) {
            player.sendMessage("§cФеникс на перезарядке!");
            return;
        }

        hasPhoenix.put(player.getUniqueId(), true);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldown * 50L);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0f, 0.5f);
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.2);
        player.sendMessage("§6§lФЕНИКС АКТИВИРОВАН! §7Вы воскреснете при смерти.");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!Boolean.TRUE.equals(hasPhoenix.get(player.getUniqueId()))) return;

        hasPhoenix.put(player.getUniqueId(), false);

        Location deathLoc = player.getLocation();
        double health = plugin.getConfig().getDouble("abilities.phoenix.revive-health", 10.0);
        int fireResist = plugin.getConfig().getInt("abilities.phoenix.fire-resistance-duration", 400);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().respawn();
                player.teleport(deathLoc);
                player.setHealth(Math.min(health, player.getAttribute(Attribute.MAX_HEALTH).getValue()));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, fireResist, 0, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2, false, true));

                player.getWorld().playSound(deathLoc, Sound.ENTITY_BLAZE_SHOOT, 2.0f, 0.5f);
                player.getWorld().spawnParticle(Particle.LAVA, deathLoc.add(0, 1, 0), 50, 1, 1, 1, 0.5);
                player.getWorld().spawnParticle(Particle.FLAME, deathLoc, 100, 1, 1, 1, 0.3);

                player.sendMessage("§6§lВОЗРОЖДЕНИЕ ФЕНИКСА! §7Вы воскресли из пепла!");
            }
        }.runTaskLater(plugin, 1);
    }

    @Override
    public boolean canExecute(Player player, CustomItem item, AbilityData abilityData) {
        Long cdEnd = cooldowns.get(player.getUniqueId());
        return cdEnd == null || System.currentTimeMillis() > cdEnd;
    }

    @Override
    public AbilityType getType() { return AbilityType.PHOENIX; }

    @Override
    public int getCooldown(AbilityData abilityData) { 
        return plugin.getConfig().getInt("abilities.phoenix.cooldown", 1200);
    }
}