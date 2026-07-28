package ru.k3.edititem.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import ru.k3.edititem.items.CustomItem;

public interface AbilityExecutor {

    /**
     * Выполняет способность
     * @param player Игрок, использующий способность
     * @param item Кастомный предмет
     * @param abilityData Данные способности
     */
    void execute(Player player, CustomItem item, AbilityData abilityData);

    /**
     * Проверяет, может ли способность быть использована
     */
    boolean canExecute(Player player, CustomItem item, AbilityData abilityData);

    /**
     * Возвращает тип способности
     */
    AbilityType getType();

    /**
     * Возвращает кулдаун способности в тиках
     */
    int getCooldown(AbilityData abilityData);

    /**
     * Проверяет, требуется ли цель для способности
     */
    default boolean requiresTarget() { return false; }

    /**
     * Проверяет, совместимо ли действие с типом клика способности
     */
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