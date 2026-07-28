package ru.k3.edititem.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.impl.*;
import ru.k3.edititem.animations.AnimationManager;
import ru.k3.edititem.items.CustomItem;

import java.util.*;

public class AbilityManager {

    private final K3EditItem plugin;
    private final Map<AbilityType, AbilityExecutor> executors;
    private final Map<UUID, Map<AbilityType, Long>> cooldowns;
    private final Map<UUID, Set<AbilityType>> activePassives;

    public AbilityManager(K3EditItem plugin) {
        this.plugin = plugin;
        this.executors = new HashMap<>();
        this.cooldowns = new HashMap<>();
        this.activePassives = new HashMap<>();
        registerAbilities();
        startPassiveChecker();
    }

    private void registerAbilities() {
        executors.put(AbilityType.FIREBALL, new FireballAbility(plugin));
        executors.put(AbilityType.WEB_TRAP, new WebTrapAbility(plugin));
        executors.put(AbilityType.LIGHTNING, new LightningAbility(plugin));
        executors.put(AbilityType.TELEPORT, new TeleportAbility(plugin));
        executors.put(AbilityType.HEAL, new HealAbility(plugin));
        executors.put(AbilityType.EXPLOSION, new ExplosionAbility(plugin));
        executors.put(AbilityType.FREEZE, new FreezeAbility(plugin));
        executors.put(AbilityType.DASH, new DashAbility(plugin));
        executors.put(AbilityType.GRAPPLE, new GrappleAbility(plugin));
        executors.put(AbilityType.SHIELD, new ShieldAbility(plugin));
        executors.put(AbilityType.POISON, new PoisonAbility(plugin));
        executors.put(AbilityType.INVISIBILITY, new InvisibilityAbility(plugin));
        executors.put(AbilityType.LASER, new LaserAbility(plugin));
        executors.put(AbilityType.TORNADO, new TornadoAbility(plugin));
        executors.put(AbilityType.BLACK_HOLE, new BlackHoleAbility(plugin));
        executors.put(AbilityType.SUMMON, new SummonAbility(plugin));
        executors.put(AbilityType.TIME_STOP, new TimeStopAbility(plugin));
        executors.put(AbilityType.METEOR, new MeteorAbility(plugin));
        executors.put(AbilityType.CHAIN, new ChainAbility(plugin));
        executors.put(AbilityType.SWAP, new SwapAbility(plugin));
        executors.put(AbilityType.REFLECT, new ReflectAbility(plugin));
        executors.put(AbilityType.BERSERK, new BerserkAbility(plugin));
        executors.put(AbilityType.CLONE, new CloneAbility(plugin));
        executors.put(AbilityType.PHOENIX, new PhoenixAbility(plugin));
    }

    private void startPassiveChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    CustomItem item = plugin.getItemManager().getHeldCustomItem(player);
                    if (item != null) {
                        for (AbilityData ability : item.getAbilities()) {
                            if (ability.getClickType() == AbilityType.ClickType.PASSIVE && ability.isEnabled()) {
                                AbilityExecutor executor = executors.get(ability.getType());
                                if (executor != null && executor.canExecute(player, item, ability)) {
                                    executor.execute(player, item, ability);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public boolean executeAbility(Player player, CustomItem item, AbilityData ability, 
                                   org.bukkit.event.block.Action action, boolean sneaking) {
        AbilityExecutor executor = executors.get(ability.getType());
        if (executor == null) return false;

        if (!ability.isEnabled()) return false;

        if (!executor.matchesClick(action, sneaking, ability.getClickType())) return false;

        if (isOnCooldown(player, ability)) {
            long remaining = getCooldownRemaining(player, ability);
            player.sendMessage(plugin.getConfig().getString("settings.prefix", "&8[&bK3&3EditItem&8] &r") 
                + "&cКулдаун: &e" + (remaining / 20.0) + " &cсек.");
            return false;
        }

        if (!executor.canExecute(player, item, ability)) return false;

        executor.execute(player, item, ability);
        setCooldown(player, ability);

        // Запуск анимации
        AnimationManager animManager = plugin.getAnimationManager();
        animManager.playAbilityAnimation(player, ability.getType());

        return true;
    }

    public boolean isOnCooldown(Player player, AbilityData ability) {
        Map<AbilityType, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return false;

        Long endTime = playerCooldowns.get(ability.getType());
        if (endTime == null) return false;

        return System.currentTimeMillis() < endTime;
    }

    public long getCooldownRemaining(Player player, AbilityData ability) {
        Map<AbilityType, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return 0;

        Long endTime = playerCooldowns.get(ability.getType());
        if (endTime == null) return 0;

        return Math.max(0, endTime - System.currentTimeMillis());
    }

    public void setCooldown(Player player, AbilityData ability) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                 .put(ability.getType(), System.currentTimeMillis() + (ability.getCooldown() * 50L));
    }

    public void clearCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    public AbilityExecutor getExecutor(AbilityType type) {
        return executors.get(type);
    }

    public Collection<AbilityExecutor> getAllExecutors() {
        return executors.values();
    }
}