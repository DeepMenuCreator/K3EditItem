package ru.k3.edititem.abilities;

import org.bukkit.Material;

public enum AbilityType {

    FIREBALL("&cОгненный Шар", Material.FIRE_CHARGE, 
             "&7Выпускает огненный шар, наносящий урон", 
             ClickType.RIGHT),

    WEB_TRAP("&8Паутинная Ловушка", Material.COBWEB, 
             "&7Создает зону паутины вокруг цели", 
             ClickType.RIGHT),

    LIGHTNING("&eМолния", Material.NETHER_STAR, 
              "&7Призывает молнию по цепочке", 
              ClickType.RIGHT),

    TELEPORT("&bТелепорт", Material.ENDER_PEARL, 
             "&7Мгновенный телепорт в направлении взгляда", 
             ClickType.RIGHT),

    HEAL("&aИсцеление", Material.GOLDEN_APPLE, 
         "&7Восстанавливает здоровье себе и союзникам", 
         ClickType.RIGHT),

    EXPLOSION("&4Взрыв", Material.TNT, 
              "&7Создает мощный взрыв в точке попадания", 
              ClickType.RIGHT),

    FREEZE("&bЗаморозка", Material.PACKED_ICE, 
           "&7Замораживает врагов в радиусе", 
           ClickType.RIGHT),

    DASH("&fРывок", Material.FEATHER, 
         "&7Мгновенный рывок вперед с уроном", 
         ClickType.RIGHT),

    GRAPPLE("&6Гарпун", Material.FISHING_ROD, 
            "&7Цепляет цель и притягивает к себе", 
            ClickType.RIGHT),

    SHIELD("&7Энергетический Щит", Material.SHIELD, 
           "&7Создает защитный барьер", 
           ClickType.RIGHT),

    POISON("&2Ядовитое Облако", Material.SPIDER_EYE, 
           "&7Распространяет ядовитое облако", 
           ClickType.RIGHT),

    INVISIBILITY("&fНевидимость", Material.GLASS_BOTTLE, 
                 "&7Делает игрока невидимым", 
                 ClickType.RIGHT),

    LASER("&cЛазер", Material.REDSTONE, 
          "&7Выпускает пробивающий лазерный луч", 
          ClickType.LEFT),

    TORNADO("&7Торнадо", Material.HAY_BLOCK, 
            "&7Создает вихрь, притягивающий врагов", 
            ClickType.RIGHT),

    BLACK_HOLE("&8Черная Дыра", Material.OBSIDIAN, 
               "&7Создает аномалию, засасывающую все", 
               ClickType.RIGHT),

    SUMMON("&6Призыв", Material.BLAZE_ROD, 
           "&7Призывает союзных существ", 
           ClickType.RIGHT),

    TIME_STOP("&5Остановка Времени", Material.CLOCK, 
              "&7Останавливает время для врагов", 
              ClickType.RIGHT),

    METEOR("&4Метеорит", Material.MAGMA_BLOCK, 
           "&7Призывает падающий метеорит", 
            ClickType.RIGHT),

    CHAIN("&eЦепная Молния", Material.CHAIN, 
          "&7Прыгает от цели к цели нанося урон", 
          ClickType.RIGHT),

    SWAP("&dОбмен", Material.ENDER_EYE, 
         "&7Меняет местами с целью", 
         ClickType.RIGHT),

    REFLECT("&bОтражение", Material.PRISMARINE_SHARD, 
            "&7Отражает входящий урон обратно", 
            ClickType.RIGHT),

    BERSERK("&4Берсерк", Material.NETHERITE_AXE, 
            "&7Увеличивает урон и скорость", 
            ClickType.RIGHT),

    CLONE("&fКлонирование", Material.PLAYER_HEAD, 
          "&7Создает клонов игрока", 
          ClickType.RIGHT),

    PHOENIX("&6Феникс", Material.TOTEM_OF_UNDYING, 
            "&7Возрождает при смерти с огненным щитом", 
            ClickType.PASSIVE);

    private final String displayName;
    private final Material icon;
    private final String description;
    private final ClickType defaultClick;

    AbilityType(String displayName, Material icon, String description, ClickType defaultClick) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
        this.defaultClick = defaultClick;
    }

    public String getDisplayName() { return displayName; }
    public Material getIcon() { return icon; }
    public String getDescription() { return description; }
    public ClickType getDefaultClick() { return defaultClick; }

    public enum ClickType {
        LEFT("&cЛКМ"),
        RIGHT("&aПКМ"),
        SHIFT_LEFT("&cShift+ЛКМ"),
        SHIFT_RIGHT("&aShift+ПКМ"),
        PASSIVE("&eПассивно");

        private final String display;
        ClickType(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }
}