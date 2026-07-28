package ru.k3.edititem.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.abilities.AbilityData;
import ru.k3.edititem.items.CustomItem;

public class ItemListener implements Listener {

    private final K3EditItem plugin;

    public ItemListener(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        String id = CustomItem.getItemId(item);
        if (id == null) return;

        CustomItem customItem = plugin.getItemManager().getItem(id);
        if (customItem == null) return;

        Action action = event.getAction();
        boolean sneaking = player.isSneaking();

        for (AbilityData ability : customItem.getAbilities()) {
            if (plugin.getAbilityManager().executeAbility(player, customItem, ability, action, sneaking)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        // Убираем анимацию со старого предмета
        plugin.getAnimationManager().removePlayerAnimation(player);

        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null) return;

        String id = CustomItem.getItemId(item);
        if (id == null) return;

        CustomItem customItem = plugin.getItemManager().getItem(id);
        if (customItem == null) return;

        if (customItem.getAnimation() != null) {
            plugin.getAnimationManager().setPlayerAnimation(player, customItem.getAnimation());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getAbilityManager().clearCooldowns(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getAnimationManager().removePlayerAnimation(event.getPlayer());
        plugin.getAbilityManager().clearCooldowns(event.getPlayer());
    }
}