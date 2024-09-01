package com.yourname.questplugin;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class QuestManager {
    private Map<String, Quest> availableQuests;
    private Map<Player, Quest> activeQuests; // Map to track active quests per player

    public QuestManager() {
        availableQuests = new LinkedHashMap<>();  // Maintain insertion order
        activeQuests = new LinkedHashMap<>();     // Maintain insertion order
    }

    // Return all available quests
    public Collection<Quest> getAllQuests() {
        return availableQuests.values();
    }

    // Method to get all quests as a map (to be used for saving)
    public Map<String, Quest> getAllQuestsAsMap() {
        return new LinkedHashMap<>(availableQuests);
    }

    // Method to create a quest
    public void createQuest(String name, String type, String target, int amount, String reward, int rewardAmount) {
        Quest newQuest;
        if (type.equalsIgnoreCase("kill")) {
            newQuest = new Quest(name, type, EntityType.valueOf(target), amount, Material.valueOf(reward), rewardAmount);
        } else {
            newQuest = new Quest(name, type, Material.valueOf(target), amount, Material.valueOf(reward), rewardAmount);
        }
        availableQuests.put(name, newQuest);
    }

    // Method to get all quest names
    public Collection<String> getAllQuestNames() {
        return availableQuests.keySet();
    }

    // Method to get a quest by name
    public Quest getQuestByName(String name) {
        return availableQuests.get(name);
    }

    // Method to remove a quest by name
    public void removeQuest(String name) {
        availableQuests.remove(name);
    }

    // Check if a player has an active quest
    public boolean hasActiveQuest(Player player) {
        return activeQuests.containsKey(player);
    }

    // Get the active quest for a player
    public Quest getActiveQuest(Player player) {
        return activeQuests.get(player);
    }

    // Set the active quest for a player
    public void setActiveQuest(Player player, Quest quest) {
        activeQuests.put(player, quest);
    }

    // Check if the specified quest is the active quest for the player
    public boolean isActiveQuest(Player player, Quest quest) {
        return quest.equals(activeQuests.get(player));
    }

    // Clear the active quest for a player (after quest completion)
    public void clearActiveQuest(Player player) {
        activeQuests.remove(player);
    }

    // Clear all active quests (for example, when a player logs out)
    public void clearAllActiveQuests() {
        activeQuests.clear();
    }

    // Get all available quests that are not yet active for the player
    public Collection<Quest> getAvailableQuestsForPlayer(Player player) {
        Map<String, Quest> available = new LinkedHashMap<>(availableQuests);
        if (getActiveQuest(player) != null) {
            available.remove(getActiveQuest(player).getName());
        }
        return available.values();
    }

    // Method to load quests from storage
    public void loadQuest(String name, Quest quest) {
        availableQuests.put(name, quest);
    }
}
