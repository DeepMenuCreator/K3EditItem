package ru.k3.edititem.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.animations.AnimationType;
import ru.k3.edititem.items.CustomItem;
import ru.k3.edititem.utils.ColorUtils;
import ru.k3.edititem.utils.ItemBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemEditorGUI {

    private final K3EditItem plugin;
    private final Map<UUID, String> editingItems;
    private static final String TITLE_PREFIX = ColorUtils.color("&8[&bРедактор&8] ");

    public ItemEditorGUI(K3EditItem plugin) {
        this.plugin = plugin;
        this.editingItems = new HashMap<>();
    }

    public void open(Player player, String itemId) {
        editingItems.put(player.getUniqueId(), itemId);
        CustomItem item = plugin.getItemManager().getItem(itemId);
        if (item == null) return;

        Inventory inv = Bukkit.createInventory(null, 54, TITLE_PREFIX + itemId);

        // Панели
        ItemStack pane = ItemBuilder.create(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) inv.setItem(i, pane);
        for (int i = 45; i < 54; i++) inv.setItem(i, pane);

        // Инфо предмета
        inv.setItem(4, item.buildItem());

        // Настройки
        inv.setItem(10, ItemBuilder.create(Material.NAME_TAG, 
            "&aИзменить название", 
            "&7Текущее: &f" + item.getDisplayName(),
            "&7Клик для изменения"));

        inv.setItem(12, ItemBuilder.create(Material.BOOK, 
            "&aИзменить лор", 
            "&7Строк: &f" + item.getLore().size(),
            "&7Клик для редактирования"));

        inv.setItem(14, ItemBuilder.create(Material.DIAMOND, 
            "&aМатериал", 
            "&7Текущий: &f" + item.getMaterial(),
            "&7Клик для изменения"));

        inv.setItem(16, ItemBuilder.create(Material.GLOWSTONE_DUST, 
            "&aСвечение: " + (item.isGlow() ? "&aВКЛ" : "&cВЫКЛ"), 
            "&7Клик для переключения"));

        inv.setItem(19, ItemBuilder.create(Material.BEDROCK, 
            "&aНеразрушимость: " + (item.isUnbreakable() ? "&aВКЛ" : "&cВЫКЛ"), 
            "&7Клик для переключения"));

        inv.setItem(21, ItemBuilder.create(Material.ITEM_FRAME, 
            "&aCustom Model Data", 
            "&7Текущее: &f" + item.getCustomModelData(),
            "&7Клик для изменения"));

        // Способности
        inv.setItem(29, ItemBuilder.create(Material.BLAZE_POWDER, 
            "&6&lСпособности", 
            "&7Клик для управления",
            "&7способностями предмета"));

        // Анимации
        inv.setItem(31, ItemBuilder.create(Material.FIREWORK_ROCKET, 
            "&d&lАнимации", 
            "&7Текущая: &f" + (item.getAnimation() != null ? item.getAnimation().getDisplayName() : "&cНет"),
            "&7Клик для настройки"));

        // Предпросмотр
        inv.setItem(33, ItemBuilder.create(Material.ENDER_CHEST, 
            "&b&lПредпросмотр", 
            "&7Клик для обновления",
            "&7предмета в руке"));

        // Сохранить
        inv.setItem(49, ItemBuilder.create(Material.LIME_CONCRETE, 
            "&a&lСОХРАНИТЬ", 
            "&7Сохранить изменения"));

        // Назад
        inv.setItem(45, ItemBuilder.create(Material.ARROW, "&7Назад"));

        player.openInventory(inv);
    }

    public String getEditingItem(Player player) {
        return editingItems.get(player.getUniqueId());
    }

    public static boolean isItemEditor(Inventory inv) {
        return inv.getView().getTitle().startsWith(TITLE_PREFIX);
    }
}