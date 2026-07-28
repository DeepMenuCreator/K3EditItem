package ru.k3.edititem.utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.k3.edititem.K3EditItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputManager {

    private final K3EditItem plugin;
    private final Map<UUID, Consumer<String>> pendingInputs;
    private final Map<UUID, String> inputTypes;

    public ChatInputManager(K3EditItem plugin) {
        this.plugin = plugin;
        this.pendingInputs = new HashMap<>();
        this.inputTypes = new HashMap<>();
    }

    public void requestInput(Player player, String type, Consumer<String> callback) {
        pendingInputs.put(player.getUniqueId(), callback);
        inputTypes.put(player.getUniqueId(), type);
        player.sendMessage("§6Введите значение в чат (или 'cancel' для отмены):");

        // Автоотмена через 30 сек
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pendingInputs.containsKey(player.getUniqueId())) {
                    pendingInputs.remove(player.getUniqueId());
                    inputTypes.remove(player.getUniqueId());
                    player.sendMessage("§cВремя ввода истекло!");
                }
            }
        }.runTaskLater(plugin, 600);
    }

    public boolean hasPendingInput(Player player) {
        return pendingInputs.containsKey(player.getUniqueId());
    }

    public void processInput(Player player, String input) {
        Consumer<String> callback = pendingInputs.remove(player.getUniqueId());
        inputTypes.remove(player.getUniqueId());

        if (input.equalsIgnoreCase("cancel")) {
            player.sendMessage("§cОтменено.");
            return;
        }

        if (callback != null) {
            callback.accept(input);
        }
    }

    public String getInputType(Player player) {
        return inputTypes.get(player.getUniqueId());
    }
}