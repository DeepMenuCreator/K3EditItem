package ru.k3.edititem.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.animations.AnimationType;
import ru.k3.edititem.items.CustomItem;
import ru.k3.edititem.utils.ColorUtils;
import ru.k3.edititem.utils.ItemBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnimationSettingsGUI {

    private final K3EditItem plugin;
    private final Map<UUID, String> editingItems;
    public static final String TITLE_PREFIX = ColorUtils.color("&8[&bАнимации&8] ");

    public AnimationSettingsGUI(K3EditItem plugin) {
        this.plugin = plugin;
        this.editingItems = new HashMap<>();
    }

    public void open(Player player, String itemId) {
        editingItems.put(player.getUniqueId(), itemId);
        CustomItem item = plugin.getItemManager().getItem(itemId);
        if (item == null) return;

        Inventory inv = Bukkit.createInventory(null, 36, TITLE_PREFIX + itemId);

        ItemStack pane = ItemBuilder.create(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) inv.setItem(i, pane);
        for (int i = 27; i < 36; i++) inv.setItem(i, pane);

        inv.setItem(4, ItemBuilder.create(Material.FIREWORK_ROCKET,
            "&d&lАнимации предмета",
            "&7Текущая: &f" + (item.getAnimation() != null ? item.getAnimation().getDisplayName() : "&cНет")));

        inv.setItem(8, ItemBuilder.create(Material.BARRIER,
            "&cУдалить анимацию",
            "&7Клик для удаления"));

        int slot = 10;
        for (AnimationType type : AnimationType.values()) {
            if (slot >= 26) break;
            boolean active = item.getAnimation() == type;

            inv.setItem(slot, ItemBuilder.create(active ? Material.LIME_DYE : type.getIcon(),
                type.getDisplayName() + (active ? " &a&l[АКТИВНО]" : ""),
                "&7" + type.getDescription(),
                "",
                active ? "&aУже выбрано" : "&aКлик для выбора"));
            slot++;
            if (slot % 9 == 8) slot += 2;
        }

        inv.setItem(31, ItemBuilder.create(Material.ARROW, "&7Назад"));

        player.openInventory(inv);
    }

    public String getEditingItem(Player player) {
        return editingItems.get(player.getUniqueId());
    }

    public static boolean isAnimationSettings(String title) {
        return title.startsWith(TITLE_PREFIX);
    }
}