package ru.k3.edititem.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.animations.AnimationType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CustomItemManager {

    private final K3EditItem plugin;
    private final Map<String, CustomItem> items;
    private final NamespacedKey itemKey;
    private final File itemsFile;

    public CustomItemManager(K3EditItem plugin) {
        this.plugin = plugin;
        this.items = new HashMap<>();
        this.itemKey = new NamespacedKey(plugin, "custom_item_id");
        this.itemsFile = new File(plugin.getDataFolder(), "items.yml");
        loadItems();
    }

    public NamespacedKey getItemKey() { return itemKey; }

    public CustomItem createItem(String id, Material material) {
        if (items.containsKey(id)) return null;
        CustomItem item = new CustomItem(id, material);
        items.put(id, item);
        saveItems();
        return item;
    }

    public CustomItem getItem(String id) {
        return items.get(id);
    }

    public void deleteItem(String id) {
        items.remove(id);
        saveItems();
    }

    public Collection<CustomItem> getAllItems() {
        return items.values();
    }

    public boolean hasItem(String id) {
        return items.containsKey(id);
    }

    public CustomItem getHeldCustomItem(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        String id = CustomItem.getItemId(hand);
        if (id == null) return null;
        return items.get(id);
    }

    public void giveItem(Player player, String id) {
        CustomItem item = items.get(id);
        if (item == null) {
            player.sendMessage(plugin.getConfig().getString("settings.prefix", "&8[&bK3&3EditItem&8] &r") + "&cПредмет не найден!");
            return;
        }
        player.getInventory().addItem(item.buildItem());
        player.sendMessage(plugin.getConfig().getString("settings.prefix", "&8[&bK3&3EditItem&8] &r") + "&aПредмет &e" + id + " &aвыдан!");
    }

    public void saveItems() {
        FileConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, CustomItem> entry : items.entrySet()) {
            ConfigurationSection section = config.createSection(entry.getKey());
            CustomItem item = entry.getValue();

            section.set("material", item.getMaterial().name());
            section.set("display-name", item.getDisplayName());
            section.set("lore", item.getLore());
            section.set("glow", item.isGlow());
            section.set("unbreakable", item.isUnbreakable());
            section.set("custom-model-data", item.getCustomModelData());
            if (item.getAnimation() != null) {
                section.set("animation", item.getAnimation().name());
            }

            // Способности
            ConfigurationSection abilitiesSection = section.createSection("abilities");
            for (int i = 0; i < item.getAbilities().size(); i++) {
                item.getAbilities().get(i).saveToConfig(abilitiesSection.createSection(String.valueOf(i)));
            }

            // Кастомные значения
            for (Map.Entry<String, Object> cv : item.getCustomValues().entrySet()) {
                section.set("custom." + cv.getKey(), cv.getValue());
            }
        }

        try {
            config.save(itemsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сохранения предметов: " + e.getMessage());
        }
    }

    public void loadItems() {
        if (!itemsFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(itemsFile);
        for (String id : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(id);
            if (section == null) continue;

            Material material = Material.valueOf(section.getString("material", "DIAMOND_SWORD"));
            CustomItem item = new CustomItem(id, material);
            item.setDisplayName(section.getString("display-name", "&b" + id));
            item.setLore(section.getStringList("lore"));
            item.setGlow(section.getBoolean("glow", false));
            item.setUnbreakable(section.getBoolean("unbreakable", false));
            item.setCustomModelData(section.getInt("custom-model-data", 0));

            if (section.contains("animation")) {
                try {
                    item.setAnimation(AnimationType.valueOf(section.getString("animation")));
                } catch (IllegalArgumentException ignored) {}
            }

            // Способности
            ConfigurationSection abilitiesSection = section.getConfigurationSection("abilities");
            if (abilitiesSection != null) {
                for (String key : abilitiesSection.getKeys(false)) {
                    item.addAbility(AbilityData.loadFromConfig(abilitiesSection.getConfigurationSection(key)));
                }
            }

            // Кастомные значения
            ConfigurationSection customSection = section.getConfigurationSection("custom");
            if (customSection != null) {
                for (String key : customSection.getKeys(false)) {
                    item.setCustomValue(key, customSection.get(key));
                }
            }

            items.put(id, item);
        }
    }

    public void saveAllItems() {
        saveItems();
    }
}