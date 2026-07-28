package ru.k3.edititem.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.abilities.AbilityType;
import ru.k3.edititem.animations.AnimationType;
import ru.k3.edititem.utils.ColorUtils;

import java.util.*;

public class CustomItem {

    private final String id;
    private String displayName;
    private Material material;
    private List<String> lore;
    private List<AbilityData> abilities;
    private AnimationType animation;
    private boolean glow;
    private boolean unbreakable;
    private int customModelData;
    private Map<String, Object> customValues;

    public CustomItem(String id, Material material) {
        this.id = id;
        this.material = material;
        this.displayName = "&b" + id;
        this.lore = new ArrayList<>();
        this.abilities = new ArrayList<>();
        this.animation = null;
        this.glow = false;
        this.unbreakable = false;
        this.customModelData = 0;
        this.customValues = new HashMap<>();
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }
    public List<String> getLore() { return lore; }
    public void setLore(List<String> lore) { this.lore = lore; }
    public void addLoreLine(String line) { this.lore.add(line); }
    public List<AbilityData> getAbilities() { return abilities; }
    public void addAbility(AbilityData ability) { this.abilities.add(ability); }
    public void removeAbility(AbilityType type) { abilities.removeIf(a -> a.getType() == type); }
    public AnimationType getAnimation() { return animation; }
    public void setAnimation(AnimationType animation) { this.animation = animation; }
    public boolean isGlow() { return glow; }
    public void setGlow(boolean glow) { this.glow = glow; }
    public boolean isUnbreakable() { return unbreakable; }
    public void setUnbreakable(boolean unbreakable) { this.unbreakable = unbreakable; }
    public int getCustomModelData() { return customModelData; }
    public void setCustomModelData(int customModelData) { this.customModelData = customModelData; }
    public Map<String, Object> getCustomValues() { return customValues; }
    public void setCustomValue(String key, Object value) { customValues.put(key, value); }

    public ItemStack buildItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(ColorUtils.color(displayName));

        List<String> coloredLore = new ArrayList<>();
        coloredLore.add(ColorUtils.color("&8ID: &7" + id));
        coloredLore.add("");
        for (String line : lore) {
            coloredLore.add(ColorUtils.color(line));
        }
        if (!abilities.isEmpty()) {
            coloredLore.add("");
            coloredLore.add(ColorUtils.color("&6✦ Способности:"));
            for (AbilityData ability : abilities) {
                String status = ability.isEnabled() ? "&a✓" : "&c✗";
                coloredLore.add(ColorUtils.color("  " + status + " &7" + ability.getType().getDisplayName() + " &8(" + ability.getClickType().getDisplay() + "&8)"));
            }
        }
        if (animation != null) {
            coloredLore.add("");
            coloredLore.add(ColorUtils.color("&d✧ Анимация: &7" + animation.getDisplayName()));
        }

        meta.setLore(coloredLore);
        meta.setUnbreakable(unbreakable);
        if (customModelData > 0) meta.setCustomModelData(customModelData);

        // PDC для идентификации
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(K3EditItem.getInstance().getItemManager().getItemKey(), PersistentDataType.STRING, id);

        item.setItemMeta(meta);

        if (glow) {
            item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.UNBREAKING, 1);
        }

        return item;
    }

    public static boolean isCustomItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(K3EditItem.getInstance().getItemManager().getItemKey(), PersistentDataType.STRING);
    }

    public static String getItemId(ItemStack item) {
        if (!isCustomItem(item)) return null;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.get(K3EditItem.getInstance().getItemManager().getItemKey(), PersistentDataType.STRING);
    }
}