package ru.k3.edititem.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.k3.edititem.K3EditItem;
import ru.k3.edititem.items.CustomItem;
import ru.k3.edititem.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EditItemCommand implements CommandExecutor, TabCompleter {

    private final K3EditItem plugin;

    public EditItemCommand(K3EditItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только для игроков!");
            return true;
        }

        Player player = (Player) sender;
        String prefix = ColorUtils.color(plugin.getConfig().getString("settings.prefix", "&8[&bK3&3EditItem&8] &r"));

        if (args.length == 0) {
            sendHelp(player, prefix);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "open" -> {
                if (!player.hasPermission("k3edititem.use")) {
                    player.sendMessage(prefix + "&cНет прав!");
                    return true;
                }
                plugin.getMainMenuGUI().open(player);
            }
            case "create" -> {
                if (!player.hasPermission("k3edititem.admin")) {
                    player.sendMessage(prefix + "&cНет прав!");
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage(prefix + "&cИспользование: /k3ei create <id> <material>");
                    return true;
                }
                String id = args[1];
                Material material;
                try {
                    material = Material.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage(prefix + "&cНеверный материал!");
                    return true;
                }
                if (plugin.getItemManager().hasItem(id)) {
                    player.sendMessage(prefix + "&cПредмет с таким ID уже существует!");
                    return true;
                }
                CustomItem item = plugin.getItemManager().createItem(id, material);
                player.sendMessage(prefix + "&aПредмет &e" + id + " &aсоздан!");
                plugin.getItemEditorGUI().open(player, id);
            }
            case "claim" -> {
                if (!player.hasPermission("k3edititem.use")) {
                    player.sendMessage(prefix + "&cНет прав!");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(prefix + "&cИспользование: /k3ei claim <id>");
                    return true;
                }
                plugin.getItemManager().giveItem(player, args[1]);
            }
            case "delete" -> {
                if (!player.hasPermission("k3edititem.admin")) {
                    player.sendMessage(prefix + "&cНет прав!");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(prefix + "&cИспользование: /k3ei delete <id>");
                    return true;
                }
                plugin.getItemManager().deleteItem(args[1]);
                player.sendMessage(prefix + "&aПредмет &e" + args[1] + " &aудален!");
            }
            case "list" -> {
                if (!player.hasPermission("k3edititem.use")) {
                    player.sendMessage(prefix + "&cНет прав!");
                    return true;
                }
                player.sendMessage(prefix + "&6Список предметов:");
                for (CustomItem item : plugin.getItemManager().getAllItems()) {
                    player.sendMessage(ColorUtils.color("  &7- &b" + item.getId() + " &7(" + item.getMaterial() + ")"));
                }
            }
            case "reload" -> {
                if (!player.hasPermission("k3edititem.admin")) {
                    player.sendMessage(prefix + "&cНет прав!");
                    return true;
                }
                plugin.reloadConfig();
                player.sendMessage(prefix + "&aКонфигурация перезагружена!");
            }
            default -> sendHelp(player, prefix);
        }

        return true;
    }

    private void sendHelp(Player player, String prefix) {
        player.sendMessage(prefix + "&6&lПомощь:");
        player.sendMessage(ColorUtils.color("  &7/k3ei open &f- Открыть меню"));
        player.sendMessage(ColorUtils.color("  &7/k3ei create <id> <material> &f- Создать предмет"));
        player.sendMessage(ColorUtils.color("  &7/k3ei claim <id> &f- Получить предмет"));
        player.sendMessage(ColorUtils.color("  &7/k3ei delete <id> &f- Удалить предмет"));
        player.sendMessage(ColorUtils.color("  &7/k3ei list &f- Список предметов"));
        player.sendMessage(ColorUtils.color("  &7/k3ei reload &f- Перезагрузить конфиг"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("open", "create", "claim", "delete", "list", "reload").stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("claim") || args[0].equalsIgnoreCase("delete"))) {
            return plugin.getItemManager().getAllItems().stream()
                .map(CustomItem::getId)
                .filter(s -> s.startsWith(args[1]))
                .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            return Arrays.stream(Material.values())
                .map(Material::name)
                .filter(s -> s.startsWith(args[2].toUpperCase()))
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}