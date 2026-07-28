package ru.k3.edititem.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.items.CustomItem;
import ru.k3.edititem.utils.ColorUtils;
import ru.k3.edititem.utils.ItemBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilitySelectorGUI {

    private final K3EditItem plugin;
    private final Map<UUID, String> editingItems;
    public static final String TITLE_PREFIX = ColorUtils.color("&8[&bСпособности&8] ");

    public AbilitySelectorGUI(K3EditItem plugin) {
        this.plugin = plugin;
        this.editingItems = new HashMap<>();
    }

    public void open(Player player, String itemId) {
        editingItems.put(player.getUniqueId(), itemId);
        CustomItem item = plugin.getItemManager().getItem(itemId);
        if (item == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, TITLE_PREFIX + itemId);

        ItemStack pane = ItemBuilder.create(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) inv.setItem(i, pane);
        for (int i = 45; i < 54; i++) inv.setItem(i, pane);

        inv.setItem(4, ItemBuilder.create(Material.BLAZE_POWDER, 
            "&6&lСпособности предмета", 
            "&7Предмет: &f" + itemId,
            "&7Слотов: &f" + item.getAbilities().size() + "/" + plugin.getConfig().getInt("settings.max-abilities-per-item", 5)));

        AbilityType[] abilities = AbilityType.values();
        int slot = 10;
        for (AbilityType type : abilities) {
            if (slot >= 44) break;

            boolean hasAbility = item.getAbilities().stream().anyMatch(a -> a.getType() == type);
            String status = hasAbility ? "&a&l[ДОБАВЛЕНО]" : "&7[Не добавлено]";
            Material icon = hasAbility ? Material.LIME_DYE : type.getIcon();

            inv.setItem(slot, ItemBuilder.create(icon, 
                type.getDisplayName() + " " + status,
                "&7" + type.getDescription(),
                "&7Тип клика: " + type.getDefaultClick().getDisplay(),
                "",
                hasAbility ? "&cКлик ПКМ - удалить" : "&aКлик ЛКМ - добавить",
                hasAbility ? "&eКлик ЛКМ - настройки" : ""));

            slot++;
            if (slot % 9 == 8) slot += 2;
        }

        inv.setItem(49, ItemBuilder.create(Material.ARROW, "&7Назад к редактору"));

        player.openInventory(inv);
    }

    public String getEditingItem(Player player) {
        return editingItems.get(player.getUniqueId());
    }

    public static boolean isAbilitySelector(String title) {
        return title.startsWith(TITLE_PREFIX);
    }
}