package ru.k3.edititem.utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

public class SoundUtils {

    public static void play(World world, Location loc, Sound sound, float volume, float pitch) {
        world.playSound(loc, sound, volume, pitch);
    }

    public static void playSuccess(Location loc) {
        loc.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
    }

    public static void playError(Location loc) {
        loc.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
    }

    public static void playClick(Location loc) {
        loc.getWorld().playSound(loc, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
    }
}