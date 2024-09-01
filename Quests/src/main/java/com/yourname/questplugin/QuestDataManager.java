package com.yourname.questplugin;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class QuestDataManager {

    private final QuestPlugin plugin;
    private File questDataFile;
    private FileConfiguration questDataConfig;

    public QuestDataManager(QuestPlugin plugin) {
        this.plugin = plugin;
        createQuestDataFile();
    }

    private void createQuestDataFile() {
        questDataFile = new File(plugin.getDataFolder(), "questData.yml");
        if (!questDataFile.exists()) {
            try {
                questDataFile.getParentFile().mkdirs();
                questDataFile.createNewFile(); // Create the file if it doesn't exist
            } catch (IOException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to create questData.yml file!");
            }
        }
        questDataConfig = YamlConfiguration.loadConfiguration(questDataFile);
    }

    public void savePlayerQuestData(Player player, Quest quest) {
        String path = "players." + player.getUniqueId() + "." + quest.getName();
        questDataConfig.set(path + ".progress", quest.getProgress(player));
        questDataConfig.set(path + ".completed", quest.isCompleted(player));
        questDataConfig.set(path + ".claimed", quest.isClaimed(player));
        plugin.getLogger().info("Saving quest data for player: " + player.getName() + ", Quest: " + quest.getName());
        saveConfig();
    }

    private void saveConfig() {
        try {
            questDataConfig.save(questDataFile);
            plugin.getLogger().info("Quest data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Failed to save questData.yml file!");
        }
    }

    public void loadPlayerQuestData(Player player, QuestManager questManager) {
        String path = "players." + player.getUniqueId();
        if (questDataConfig.contains(path)) {
            plugin.getLogger().info("Loading quest data for player: " + player.getName());
            for (String questName : questDataConfig.getConfigurationSection(path).getKeys(false)) {
                Quest quest = questManager.getQuestByName(questName);
                if (quest != null) {
                    int progress = questDataConfig.getInt(path + "." + questName + ".progress");
                    boolean completed = questDataConfig.getBoolean(path + "." + questName + ".completed");
                    boolean claimed = questDataConfig.getBoolean(path + "." + questName + ".claimed");
                    quest.setProgress(player, progress);
                    quest.setCompleted(player, completed);
                    quest.setClaimed(player, claimed);
                    if (completed && !claimed) {
                        questManager.setActiveQuest(player, quest); // Automatically mark the quest as active if completed but not claimed
                    }
                }
            }
        }
    }

    public void saveAllData(QuestManager questManager) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Quest quest = questManager.getActiveQuest(player);
            if (quest != null) {
                savePlayerQuestData(player, quest);
            }
        }

        // Save all quests to the questData.yml
        Map<String, Quest> allQuests = questManager.getAllQuestsAsMap();
        for (Map.Entry<String, Quest> entry : allQuests.entrySet()) {
            String questName = entry.getKey();
            Quest quest = entry.getValue();

            String questPath = "quests." + questName;
            questDataConfig.set(questPath + ".type", quest.getType());
            questDataConfig.set(questPath + ".targetEntity", quest.getTargetEntity() != null ? quest.getTargetEntity().name() : null);
            questDataConfig.set(questPath + ".targetMaterial", quest.getTargetMaterial() != null ? quest.getTargetMaterial().name() : null);
            questDataConfig.set(questPath + ".amount", quest.getAmount());
            questDataConfig.set(questPath + ".reward", quest.getReward().name());
            questDataConfig.set(questPath + ".rewardAmount", quest.getRewardAmount());
        }

        saveConfig();
    }

    public void loadAllQuests(QuestManager questManager) {
        if (questDataConfig.contains("quests")) {
            plugin.getLogger().info("Loading all quests from questData.yml");
            for (String questName : questDataConfig.getConfigurationSection("quests").getKeys(false)) {
                String type = questDataConfig.getString("quests." + questName + ".type");
                String targetEntityStr = questDataConfig.getString("quests." + questName + ".targetEntity");
                String targetMaterialStr = questDataConfig.getString("quests." + questName + ".targetMaterial");
                int amount = questDataConfig.getInt("quests." + questName + ".amount");
                String rewardStr = questDataConfig.getString("quests." + questName + ".reward");
                int rewardAmount = questDataConfig.getInt("quests." + questName + ".rewardAmount");

                EntityType targetEntity = targetEntityStr != null ? EntityType.valueOf(targetEntityStr) : null;
                Material targetMaterial = targetMaterialStr != null ? Material.valueOf(targetMaterialStr) : null;
                Material reward = Material.valueOf(rewardStr);

                Quest quest;
                if (type.equalsIgnoreCase("kill")) {
                    quest = new Quest(questName, type, targetEntity, amount, reward, rewardAmount);
                } else {
                    quest = new Quest(questName, type, targetMaterial, amount, reward, rewardAmount);
                }

                questManager.loadQuest(questName, quest);
            }
        }
    }
}
