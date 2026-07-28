package ru.k3.edititem.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;
import ru.k3.edititem.utils.ColorUtils;
import ru.k3.edititem.utils.ItemBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilitySettingsGUI {

    private final K3EditItem plugin;
    private final Map<UUID, AbilityEditContext> editingContexts;
    public static final String TITLE_PREFIX = ColorUtils.color("&8[&bНастройки&8] ");

    public AbilitySettingsGUI(K3EditItem plugin) {
        this.plugin = plugin;
        this.editingContexts = new HashMap<>();
    }

    public void open(Player player, String itemId, AbilityType abilityType) {
        CustomItem item = plugin.getItemManager().getItem(itemId);
        if (item == null) return;

        AbilityData ability = item.getAbilities().stream()
            .filter(a -> a.getType() == abilityType)
            .findFirst().orElse(null);
        if (ability == null) return;

        editingContexts.put(player.getUniqueId(), new AbilityEditContext(itemId, abilityType));

        Inventory inv = Bukkit.createInventory(null, 54, TITLE_PREFIX + abilityType.name());

        ItemStack pane = ItemBuilder.create(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) inv.setItem(i, pane);
        for (int i = 45; i < 54; i++) inv.setItem(i, pane);

        inv.setItem(4, ItemBuilder.create(abilityType.getIcon(), 
            abilityType.getDisplayName(),
            "&7" + abilityType.getDescription()));

        inv.setItem(10, ItemBuilder.create(ability.isEnabled() ? Material.LIME_CONCRETE : Material.RED_CONCRETE,
            "&aСтатус: " + (ability.isEnabled() ? "&aВКЛ" : "&cВЫКЛ"),
            "&7Клик для переключения"));

        inv.setItem(12, ItemBuilder.create(Material.STONE_BUTTON,
            "&eТип клика",
            "&7Текущий: " + ability.getClickType().getDisplay(),
            "&7Клик для смены"));

        inv.setItem(14, ItemBuilder.create(Material.CLOCK,
            "&bКулдаун",
            "&7Текущий: &f" + ability.getCooldown() + " тиков (" + String.format("%.1f", ability.getCooldown()/20.0) + " сек)",
            "&7ЛКМ - +10 тиков",
            "&7ПКМ - -10 тиков",
            "&7Shift+ЛКМ - +100 тиков",
            "&7Shift+ПКМ - -100 тиков"));

        inv.setItem(16, ItemBuilder.create(Material.IRON_SWORD,
            "&cУрон",
            "&7Текущий: &f" + ability.getDamage(),
            "&7ЛКМ - +0.5",
            "&7ПКМ - -0.5",
            "&7Shift+ЛКМ - +5",
            "&7Shift+ПКМ - -5"));

        inv.setItem(20, ItemBuilder.create(Material.COMPASS,
            "&6Радиус",
            "&7Текущий: &f" + ability.getRadius(),
            "&7ЛКМ - +0.5",
            "&7ПКМ - -0.5"));

        inv.setItem(22, ItemBuilder.create(Material.HONEYCOMB,
            "&dДлительность",
            "&7Текущая: &f" + ability.getDuration() + " тиков",
            "&7ЛКМ - +10",
            "&7ПКМ - -10",
            "&7Shift+ЛКМ - +100",
            "&7Shift+ПКМ - -100"));

        inv.setItem(24, ItemBuilder.create(Material.EXPERIENCE_BOTTLE,
            "&5Уровень",
            "&7Текущий: &f" + ability.getLevel(),
            "&7ЛКМ - +1",
            "&7ПКМ - -1"));

        inv.setItem(29, ItemBuilder.create(Material.CHEST,
            "&6Кастомные значения",
            "&7Клик для просмотра",
            "&7и редактирования"));

        inv.setItem(31, ItemBuilder.create(Material.BOOK,
            "&9Уникальные параметры",
            "&7Специфичные для этой",
            "&7способности настройки"));

        inv.setItem(49, ItemBuilder.create(Material.LIME_CONCRETE,
            "&a&lСОХРАНИТЬ",
            "&7Сохранить настройки"));

        inv.setItem(45, ItemBuilder.create(Material.ARROW, "&7Назад"));

        player.openInventory(inv);
    }

    public AbilityEditContext getContext(Player player) {
        return editingContexts.get(player.getUniqueId());
    }

    public static boolean isAbilitySettings(String title) {
        return title.startsWith(TITLE_PREFIX);
    }

    public static class AbilityEditContext {
        public final String itemId;
        public final AbilityType abilityType;

        public AbilityEditContext(String itemId, AbilityType abilityType) {
            this.itemId = itemId;
            this.abilityType = abilityType;
        }
    }
}