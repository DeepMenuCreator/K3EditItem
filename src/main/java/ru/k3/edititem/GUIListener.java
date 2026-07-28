package ru.k3.edititem.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.animations.AnimationType;
import ru.k3.edititem.gui.*;
import ru.k3.edititem.items.CustomItem;

public class GUIListener implements Listener {

    private final K3EditItem plugin;

    public GUIListener(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (MainMenuGUI.isMainMenu(inv)) {
            event.setCancelled(true);
            handleMainMenu(player, clicked);
            return;
        }

        if (ItemEditorGUI.isItemEditor(inv)) {
            event.setCancelled(true);
            handleItemEditor(player, clicked);
            return;
        }

        if (AbilitySelectorGUI.isAbilitySelector(inv)) {
            event.setCancelled(true);
            handleAbilitySelector(player, clicked, event);
            return;
        }

        if (AbilitySettingsGUI.isAbilitySettings(inv)) {
            event.setCancelled(true);
            handleAbilitySettings(player, clicked, event);
            return;
        }

        if (AnimationSettingsGUI.isAnimationSettings(inv)) {
            event.setCancelled(true);
            handleAnimationSettings(player, clicked);
            return;
        }
    }

    private void handleMainMenu(Player player, ItemStack clicked) {
        Material type = clicked.getType();

        switch (type) {
            case ANVIL -> {
                player.closeInventory();
                player.sendMessage("§6Введите команду: §e/k3ei create <id> <material>");
            }
            case CHEST -> plugin.getMainMenuGUI().open(player);
            case REDSTONE -> {
                player.closeInventory();
                player.sendMessage("§6Введите команду: §e/k3ei delete <id>");
            }
            case COMMAND_BLOCK -> {
                plugin.reloadConfig();
                player.sendMessage("§aКонфигурация перезагружена!");
                player.closeInventory();
            }
            default -> {
                if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                    String name = clicked.getItemMeta().getDisplayName();
                    String id = name.replaceAll("§[0-9a-fk-or]", "").trim();
                    if (plugin.getItemManager().hasItem(id)) {
                        plugin.getItemEditorGUI().open(player, id);
                    }
                }
            }
        }
    }

    private void handleItemEditor(Player player, ItemStack clicked) {
        Material type = clicked.getType();
        String itemId = plugin.getItemEditorGUI().getEditingItem(player);
        if (itemId == null) return;
        CustomItem item = plugin.getItemManager().getItem(itemId);
        if (item == null) return;

        switch (type) {
            case NAME_TAG -> {
                player.closeInventory();
                plugin.getChatListener().getChatInputManager().requestInput(player, "rename", input -> {
                    item.setDisplayName(input);
                    plugin.getItemManager().saveItems();
                    player.sendMessage("§aНазвание изменено на: §r" + input);
                    plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getItemEditorGUI().open(player, itemId));
                });
            }
            case BOOK -> {
                player.closeInventory();
                plugin.getChatListener().getChatInputManager().requestInput(player, "lore", input -> {
                    item.addLoreLine(input);
                    plugin.getItemManager().saveItems();
                    player.sendMessage("§aСтрока лора добавлена!");
                    plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getItemEditorGUI().open(player, itemId));
                });
            }
            case DIAMOND -> {
                player.closeInventory();
                plugin.getChatListener().getChatInputManager().requestInput(player, "material", input -> {
                    try {
                        Material mat = Material.valueOf(input.toUpperCase());
                        item.setMaterial(mat);
                        plugin.getItemManager().saveItems();
                        player.sendMessage("§aМатериал изменен на: §e" + mat);
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("§cНеверный материал!");
                    }
                    plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getItemEditorGUI().open(player, itemId));
                });
            }
            case GLOWSTONE_DUST -> {
                item.setGlow(!item.isGlow());
                plugin.getItemManager().saveItems();
                plugin.getItemEditorGUI().open(player, itemId);
            }
            case BEDROCK -> {
                item.setUnbreakable(!item.isUnbreakable());
                plugin.getItemManager().saveItems();
                plugin.getItemEditorGUI().open(player, itemId);
            }
            case ITEM_FRAME -> {
                player.closeInventory();
                plugin.getChatListener().getChatInputManager().requestInput(player, "cmd", input -> {
                    try {
                        int cmd = Integer.parseInt(input);
                        item.setCustomModelData(cmd);
                        plugin.getItemManager().saveItems();
                        player.sendMessage("§aCustom Model Data установлен: §e" + cmd);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cВведите число!");
                    }
                    plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getItemEditorGUI().open(player, itemId));
                });
            }
            case BLAZE_POWDER -> plugin.getAbilitySelectorGUI().open(player, itemId);
            case FIREWORK_ROCKET -> plugin.getAnimationSettingsGUI().open(player, itemId);
            case ENDER_CHEST -> {
                player.getInventory().setItemInMainHand(item.buildItem());
                player.sendMessage("§aПредмет обновлен в руке!");
            }
            case LIME_CONCRETE -> {
                if (clicked.hasItemMeta() && clicked.getItemMeta().getDisplayName().contains("СОХРАНИТЬ")) {
                    plugin.getItemManager().saveItems();
                    player.sendMessage("§aИзменения сохранены!");
                    player.closeInventory();
                }
            }
            case ARROW -> plugin.getMainMenuGUI().open(player);
        }
    }

    private void handleAbilitySelector(Player player, ItemStack clicked, InventoryClickEvent event) {
        String itemId = plugin.getAbilitySelectorGUI().getEditingItem(player);
        if (itemId == null) return;
        CustomItem item = plugin.getItemManager().getItem(itemId);
        if (item == null) return;

        if (clicked.getType() == Material.ARROW) {
            plugin.getItemEditorGUI().open(player, itemId);
            return;
        }

        AbilityType selected = null;
        for (AbilityType type : AbilityType.values()) {
            if (type.getIcon() == clicked.getType() || 
                (clicked.getType() == Material.LIME_DYE && item.getAbilities().stream().anyMatch(a -> a.getType() == type))) {
                selected = type;
                break;
            }
        }

        if (selected == null) return;

        boolean hasAbility = item.getAbilities().stream().anyMatch(a -> a.getType() == selected);

        if (event.isLeftClick() && hasAbility) {
            plugin.getAbilitySettingsGUI().open(player, itemId, selected);
        } else if (event.isLeftClick() && !hasAbility) {
            if (item.getAbilities().size() >= plugin.getConfig().getInt("settings.max-abilities-per-item", 5)) {
                player.sendMessage("§cДостигнут лимит способностей!");
                return;
            }
            item.addAbility(new AbilityData(selected));
            plugin.getItemManager().saveItems();
            player.sendMessage("§aСпособность добавлена!");
            plugin.getAbilitySelectorGUI().open(player, itemId);
        } else if (event.isRightClick() && hasAbility) {
            item.removeAbility(selected);
            plugin.getItemManager().saveItems();
            player.sendMessage("§cСпособность удалена!");
            plugin.getAbilitySelectorGUI().open(player, itemId);
        }
    }

    private void handleAbilitySettings(Player player, ItemStack clicked, InventoryClickEvent event) {
        AbilitySettingsGUI.AbilityEditContext context = plugin.getAbilitySettingsGUI().getContext(player);
        if (context == null) return;

        CustomItem item = plugin.getItemManager().getItem(context.itemId);
        if (item == null) return;

        AbilityData ability = item.getAbilities().stream()
            .filter(a -> a.getType() == context.abilityType)
            .findFirst().orElse(null);
        if (ability == null) return;

        Material type = clicked.getType();
        boolean shift = event.isShiftClick();

        switch (type) {
            case LIME_CONCRETE, RED_CONCRETE -> {
                if (clicked.hasItemMeta() && clicked.getItemMeta().getDisplayName().contains("Статус")) {
                    ability.setEnabled(!ability.isEnabled());
                    plugin.getItemManager().saveItems();
                    plugin.getAbilitySettingsGUI().open(player, context.itemId, context.abilityType);
                }
            }
            case STONE_BUTTON -> {
                AbilityType.ClickType[] types = AbilityType.ClickType.values();
                int nextIndex = (ability.getClickType().ordinal() + 1) % types.length;
                ability.setClickType(types[nextIndex]);
                plugin.getItemManager().saveItems();
                plugin.getAbilitySettingsGUI().open(player, context.itemId, context.abilityType);
            }
            case CLOCK -> {
                int change = shift ? (event.isLeftClick() ? 100 : -100) : (event.isLeftClick() ? 10 : -10);
                ability.setCooldown(Math.max(0, ability.getCooldown() + change));
                plugin.getItemManager().saveItems();
                plugin.getAbilitySettingsGUI().open(player, context.itemId, context.abilityType);
            }
            case IRON_SWORD -> {
                double change = shift ? (event.isLeftClick() ? 5 : -5) : (event.isLeftClick() ? 0.5 : -0.5);
                ability.setDamage(Math.max(0, ability.getDamage() + change));
                plugin.getItemManager().saveItems();
                plugin.getAbilitySettingsGUI().open(player, context.itemId, context.abilityType);
            }
            case COMPASS -> {
                double change = event.isLeftClick() ? 0.5 : -0.5;
                ability.setRadius(Math.max(0.5, ability.getRadius() + change));
                plugin.getItemManager().saveItems();
                plugin.getAbilitySettingsGUI().open(player, context.itemId, context.abilityType);
            }
            case HONEYCOMB -> {
                int change = shift ? (event.isLeftClick() ? 100 : -100) : (event.isLeftClick() ? 10 : -10);
                ability.setDuration(Math.max(0, ability.getDuration() + change));
                plugin.getItemManager().saveItems();
                plugin.getAbilitySettingsGUI().open(player, context.itemId, context.abilityType);
            }
            case EXPERIENCE_BOTTLE -> {
                int change = event.isLeftClick() ? 1 : -1;
                ability.setLevel(Math.max(1, ability.getLevel() + change));
                plugin.getItemManager().saveItems();
                plugin.getAbilitySettingsGUI().open(player, context.itemId, context.abilityType);
            }
            case LIME_CONCRETE -> {
                if (clicked.hasItemMeta() && clicked.getItemMeta().getDisplayName().contains("СОХРАНИТЬ")) {
                    plugin.getItemManager().saveItems();
                    player.sendMessage("§aНастройки сохранены!");
                    plugin.getAbilitySelectorGUI().open(player, context.itemId);
                }
            }
            case ARROW -> plugin.getAbilitySelectorGUI().open(player, context.itemId);
        }
    }

    private void handleAnimationSettings(Player player, ItemStack clicked) {
        String itemId = plugin.getAnimationSettingsGUI().getEditingItem(player);
        if (itemId == null) return;
        CustomItem item = plugin.getItemManager().getItem(itemId);
        if (item == null) return;

        Material type = clicked.getType();

        if (type == Material.ARROW) {
            plugin.getItemEditorGUI().open(player, itemId);
            return;
        }

        if (type == Material.BARRIER) {
            item.setAnimation(null);
            plugin.getItemManager().saveItems();
            player.sendMessage("§cАнимация удалена!");
            plugin.getAnimationSettingsGUI().open(player, itemId);
            return;
        }

        for (AnimationType anim : AnimationType.values()) {
            if (anim.getIcon() == type || (type == Material.LIME_DYE && item.getAnimation() == anim)) {
                item.setAnimation(anim);
                plugin.getItemManager().saveItems();
                player.sendMessage("§aАнимация установлена: " + anim.getDisplayName());
                plugin.getAnimationSettingsGUI().open(player, itemId);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        // Автосохранение при закрытии редакторов
    }
}