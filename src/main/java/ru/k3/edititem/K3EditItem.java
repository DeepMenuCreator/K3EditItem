package ru.k3.edititem;

import org.bukkit.plugin.java.JavaPlugin;
import ru.k3.edititem.abilities.AbilityManager;
import ru.k3.edititem.animations.AnimationManager;
import ru.k3.edititem.commands.EditItemCommand;
import ru.k3.edititem.gui.*;
import ru.k3.edititem.items.CustomItemManager;
import ru.k3.edititem.listeners.ChatListener;
import ru.k3.edititem.listeners.GUIListener;
import ru.k3.edititem.listeners.ItemListener;

public class K3EditItem extends JavaPlugin {

    private static K3EditItem instance;
    private CustomItemManager itemManager;
    private AbilityManager abilityManager;
    private AnimationManager animationManager;
    private ChatListener chatListener;
    private MainMenuGUI mainMenuGUI;
    private ItemEditorGUI itemEditorGUI;
    private AbilitySelectorGUI abilitySelectorGUI;
    private AbilitySettingsGUI abilitySettingsGUI;
    private AnimationSettingsGUI animationSettingsGUI;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        // Инициализация менеджеров
        itemManager = new CustomItemManager(this);
        abilityManager = new AbilityManager(this);
        animationManager = new AnimationManager(this);
        chatListener = new ChatListener(this);

        // Инициализация GUI
        mainMenuGUI = new MainMenuGUI(this);
        itemEditorGUI = new ItemEditorGUI(this);
        abilitySelectorGUI = new AbilitySelectorGUI(this);
        abilitySettingsGUI = new AbilitySettingsGUI(this);
        animationSettingsGUI = new AnimationSettingsGUI(this);

        // Регистрация команд
        getCommand("k3edititem").setExecutor(new EditItemCommand(this));
        getCommand("k3edititem").setTabCompleter(new EditItemCommand(this));

        // Регистрация слушателей
        getServer().getPluginManager().registerEvents(new ItemListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(chatListener, this);

        getLogger().info("╔══════════════════════════════════════╗");
        getLogger().info("║     K3EditItem v1.0.0 ЗАГРУЖЕН      ║");
        getLogger().info("║   24 способности | Анимации | GUI    ║");
        getLogger().info("╚══════════════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        if (itemManager != null) {
            itemManager.saveAllItems();
        }
        getLogger().info("K3EditItem выключен. Все данные сохранены.");
    }

    public static K3EditItem getInstance() {
        return instance;
    }

    public CustomItemManager getItemManager() { return itemManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
    public AnimationManager getAnimationManager() { return animationManager; }
    public ChatListener getChatListener() { return chatListener; }
    public MainMenuGUI getMainMenuGUI() { return mainMenuGUI; }
    public ItemEditorGUI getItemEditorGUI() { return itemEditorGUI; }
    public AbilitySelectorGUI getAbilitySelectorGUI() { return abilitySelectorGUI; }
    public AbilitySettingsGUI getAbilitySettingsGUI() { return abilitySettingsGUI; }
    public AnimationSettingsGUI getAnimationSettingsGUI() { return animationSettingsGUI; }
}