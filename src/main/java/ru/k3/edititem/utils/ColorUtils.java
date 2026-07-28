package ru.k3.edititem.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorUtils {

    public static String color(String text) {
        if (text == null) return "";
        return text.replace("&", "§");
    }

    public static Component component(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}