package com.yourname.questplugin;

import com.yourname.questplugin.commands.QuestAdminCommand;
import com.yourname.questplugin.commands.QuestAdminTabCompleter;
import com.yourname.questplugin.commands.QuestsCommand;
import com.yourname.questplugin.commands.QuestsGUICommand;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestPlugin extends JavaPlugin {

    private QuestManager questManager;
    private QuestDataManager questDataManager;

    @Override
    public void onEnable() {
        questManager = new QuestManager();
        questDataManager = new QuestDataManager(this); // Initialize QuestDataManager

        // Register commands
        this.getCommand("questadmin").setExecutor(new QuestAdminCommand(questManager));
        this.getCommand("questadmin").setTabCompleter(new QuestAdminTabCompleter());
        this.getCommand("quests").setExecutor(new QuestsCommand(questManager, questDataManager));
        this.getCommand("quests").setExecutor(new QuestsGUICommand(questManager, questDataManager));

        // Register the QuestListener and QuestsGUICommand as event listeners
        getServer().getPluginManager().registerEvents(new QuestListener(questManager, questDataManager), this);
        getServer().getPluginManager().registerEvents(new QuestsGUICommand(questManager, questDataManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(questManager, questDataManager), this); // Register PlayerJoinListener

        // Load all quests from the questData.yml
        questDataManager.loadAllQuests(questManager);

        // Load data for all online players (in case of a server reload)
        getServer().getOnlinePlayers().forEach(player -> questDataManager.loadPlayerQuestData(player, questManager));

        getLogger().info("QuestPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save all data when the server stops
        questDataManager.saveAllData(questManager);
        getLogger().info("QuestPlugin has been disabled!");
    }

    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }
}
