package com.yourname.questplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final QuestManager questManager;
    private final QuestDataManager questDataManager;

    public PlayerJoinListener(QuestManager questManager, QuestDataManager questDataManager) {
        this.questManager = questManager;
        this.questDataManager = questDataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Load the player's quest data when they join the server
        questDataManager.loadPlayerQuestData(event.getPlayer(), questManager);
    }
}
