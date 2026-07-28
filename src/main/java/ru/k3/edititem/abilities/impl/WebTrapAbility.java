package ru.k3.edititem.abilities.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityExecutor;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;

import java.util.ArrayList;
import java.util.List;

public class WebTrapAbility implements AbilityExecutor {

    private final K3EditItem plugin;

    public WebTrapAbility(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, CustomItem item, AbilityData abilityData) {
        Location target = player.getTargetBlockExact(32);
        if (target == null) target = player.getLocation();

        int radius = (int) abilityData.getRadius();
        int duration = abilityData.getDuration();

        List<Block> placedBlocks = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x*x + z*z <= radius*radius) {
                    Block block = target.clone().add(x, 0, z).getBlock();
                    Block blockAbove = block.getRelative(0, 1, 0);
                    if (block.getType().isSolid() && blockAbove.getType() == Material.AIR) {
                        blockAbove.setType(Material.COBWEB);
                        placedBlocks.add(blockAbove);
                    }
                }
            }
        }

        target.getWorld().playSound(target, Sound.BLOCK_SPIDER_STEP, 1.0f, 0.5f);
        target.getWorld().spawnParticle(Particle.CRIT, target, 50, radius, 1, radius, 0.1);

        // Удаление паутины через время
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : placedBlocks) {
                    if (block.getType() == Material.COBWEB) {
                        block.setType(Material.AIR);
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
    public AbilityType getType() { return AbilityType.WEB_TRAP; }

    @Override
    public int getCooldown(AbilityData abilityData) { return abilityData.getCooldown(); }
}