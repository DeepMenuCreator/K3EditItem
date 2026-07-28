package ru.k3.edititem.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import ru.k3.edititem.items.CustomItem;

public interface AbilityExecutor {

    void execute(Player player, CustomItem item, AbilityData abilityData);

    boolean canExecute(Player player, CustomItem item, AbilityData abilityData);

    AbilityType getType();

    int getCooldown(AbilityData abilityData);

    default boolean requiresTarget() { return false; }

    default boolean matchesClick(Action action, boolean sneaking, AbilityType.ClickType clickType) {
        return switch (clickType) {
            case LEFT -> action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
            case RIGHT -> action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
            case SHIFT_LEFT -> (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) && sneaking;
            case SHIFT_RIGHT -> (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && sneaking;
            case PASSIVE -> false;
        };
    }
}