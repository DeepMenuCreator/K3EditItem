package ru.k3.edititem.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.utils.ChatInputManager;

public class ChatListener implements Listener {

    private final K3EditItem plugin;
    private final ChatInputManager chatInputManager;

    public ChatListener(K3EditItem plugin) {
        this.plugin = plugin;
        this.chatInputManager = new ChatInputManager(plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!chatInputManager.hasPendingInput(player)) return;

        event.setCancelled(true);
        String input = event.getMessage();

        // Выполняем в главном потоке
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            chatInputManager.processInput(player, input);
        });
    }

    public ChatInputManager getChatInputManager() {
        return chatInputManager;
    }
}