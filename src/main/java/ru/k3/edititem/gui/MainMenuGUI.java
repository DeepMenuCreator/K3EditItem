package ru.k3.edititem.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.items.CustomItem;
import ru.k3.edititem.utils.ColorUtils;
import ru.k3.edititem.utils.ItemBuilder;

import java.util.Arrays;

public class MainMenuGUI {

    private final K3EditItem plugin;
    private static final String TITLE = ColorUtils.color("&8[&bK3&3EditItem&8] &7Главное меню");

    public MainMenuGUI(K3EditItem plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        // Декоративные панели
        ItemStack pane = ItemBuilder.create(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) inv.setItem(i, pane);
        for (int i = 45; i < 54; i++) inv.setItem(i, pane);
        inv.setItem(9, pane); inv.setItem(17, pane);
        inv.setItem(36, pane); inv.setItem(44, pane);

        // Заголовок
        inv.setItem(4, ItemBuilder.create(Material.NETHER_STAR, 
            "&6&lK3EditItem", 
            "&7Версия: &f1.0.0",
            "&7Предметов: &f" + plugin.getItemManager().getAllItems().size()));

        // Кнопки
        inv.setItem(20, ItemBuilder.create(Material.ANVIL, 
            "&aСоздать предмет", 
            "&7Клик для создания нового",
            "&7кастомного предмета"));

        inv.setItem(22, ItemBuilder.create(Material.CHEST, 
            "&eСписок предметов", 
            "&7Просмотр всех созданных",
            "&7предметов и их редактирование"));

        inv.setItem(24, ItemBuilder.create(Material.BOOK, 
            "&bСправка", 
            "&7/k3ei create <id> <type>",
            "&7/k3ei claim <id>",
            "&7/k3ei open"));

        inv.setItem(38, ItemBuilder.create(Material.REDSTONE, 
            "&cУдалить предмет", 
            "&7Клик для удаления предмета"));

        inv.setItem(42, ItemBuilder.create(Material.COMMAND_BLOCK, 
            "&6Перезагрузить", 
            "&7Перезагрузка конфигурации"));

        // Список предметов (слоты 28-34)
        int slot = 28;
        for (CustomItem item : plugin.getItemManager().getAllItems()) {
            if (slot > 34) break;
            inv.setItem(slot, ItemBuilder.create(item.getMaterial(), 
                "&b" + item.getId(),
                "&7Материал: &f" + item.getMaterial(),
                "&7Способностей: &f" + item.getAbilities().size(),
                "&7Клик для редактирования"));
            slot++;
        }

        player.openInventory(inv);
    }

    public static boolean isMainMenu(Inventory inv) {
        return inv.getView().getTitle().equals(TITLE);
    }
}