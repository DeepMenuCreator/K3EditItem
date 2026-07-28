package ru.k3.edititem.abilities;

import org.bukkit.configuration.ConfigurationSection;
import java.util.HashMap;
import java.util.Map;

public class AbilityData {

    private final AbilityType type;
    private AbilityType.ClickType clickType;
    private final Map<String, Object> customValues;
    private boolean enabled;
    private int cooldown;
    private double damage;
    private double radius;
    private int duration;
    private int level;

    public AbilityData(AbilityType type) {
        this.type = type;
        this.clickType = type.getDefaultClick();
        this.customValues = new HashMap<>();
        this.enabled = true;
        this.cooldown = 100;
        this.damage = 5.0;
        this.radius = 3.0;
        this.duration = 60;
        this.level = 1;
    }

    public AbilityType getType() { return type; }
    public AbilityType.ClickType getClickType() { return clickType; }
    public void setClickType(AbilityType.ClickType clickType) { this.clickType = clickType; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getCooldown() { return cooldown; }
    public void setCooldown(int cooldown) { this.cooldown = cooldown; }
    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Map<String, Object> getCustomValues() { return customValues; }
    public void setCustomValue(String key, Object value) { customValues.put(key, value); }
    public Object getCustomValue(String key) { return customValues.get(key); }

    public void saveToConfig(ConfigurationSection section) {
        section.set("type", type.name());
        section.set("click-type", clickType.name());
        section.set("enabled", enabled);
        section.set("cooldown", cooldown);
        section.set("damage", damage);
        section.set("radius", radius);
        section.set("duration", duration);
        section.set("level", level);
        for (Map.Entry<String, Object> entry : customValues.entrySet()) {
            section.set("custom." + entry.getKey(), entry.getValue());
        }
    }

    public static AbilityData loadFromConfig(ConfigurationSection section) {
        AbilityType type = AbilityType.valueOf(section.getString("type"));
        AbilityData data = new AbilityData(type);
        data.clickType = AbilityType.ClickType.valueOf(section.getString("click-type", type.getDefaultClick().name()));
        data.enabled = section.getBoolean("enabled", true);
        data.cooldown = section.getInt("cooldown", 100);
        data.damage = section.getDouble("damage", 5.0);
        data.radius = section.getDouble("radius", 3.0);
        data.duration = section.getInt("duration", 60);
        data.level = section.getInt("level", 1);

        if (section.contains("custom")) {
            ConfigurationSection customSection = section.getConfigurationSection("custom");
            if (customSection != null) {
                for (String key : customSection.getKeys(false)) {
                    data.customValues.put(key, customSection.get(key));
                }
            }
        }
        return data;
    }
}