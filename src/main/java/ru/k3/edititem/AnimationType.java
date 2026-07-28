package ru.k3.edititem.animations;

import org.bukkit.Material;

public enum AnimationType {
    CIRCLE("&bКруговая", Material.COMPASS, "&7Частицы вокруг игрока"),
    SPIRAL("&dСпиральная", Material.ENDER_PEARL, "&7Восходящая спираль"),
    PULSE("&cПульсация", Material.REDSTONE, "&7Ритмичные вспышки"),
    BEAM("&eЛуч", Material.BEACON, "&7Вертикальный луч"),
    AURA("&aАура", Material.EXPERIENCE_BOTTLE, "&7Цветная аура вокруг"),
    TRAIL("&fСлед", Material.FEATHER, "&7След из частиц при движении"),
    RINGS("&6Кольца", Material.GOLD_NUGGET, "&7Расширяющиеся кольца"),
    STAR("&6Звезда", Material.NETHER_STAR, "&7Вращающаяся звезда");

    private final String displayName;
    private final Material icon;
    private final String description;

    AnimationType(String displayName, Material icon, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public Material getIcon() { return icon; }
    public String getDescription() { return description; }
}