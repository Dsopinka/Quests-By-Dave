package com.yourname.questplugin;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Quest {
    private final String name;
    private final String type;
    private final EntityType targetEntity;
    private final Material targetMaterial;
    private final int amount;
    private final Material reward;
    private final int rewardAmount;
    private final Map<Player, Integer> progressMap = new HashMap<>();
    private final Map<Player, Boolean> completedMap = new HashMap<>();
    private final Map<Player, Boolean> claimedMap = new HashMap<>();

    // Constructor for a kill quest
    public Quest(String name, String type, EntityType targetEntity, int amount, Material reward, int rewardAmount) {
        this.name = name;
        this.type = type;
        this.targetEntity = targetEntity;
        this.targetMaterial = null;
        this.amount = amount;
        this.reward = reward;
        this.rewardAmount = rewardAmount;
    }

    // Constructor for a collect quest
    public Quest(String name, String type, Material targetMaterial, int amount, Material reward, int rewardAmount) {
        this.name = name;
        this.type = type;
        this.targetEntity = null;
        this.targetMaterial = targetMaterial;
        this.amount = amount;
        this.reward = reward;
        this.rewardAmount = rewardAmount;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public EntityType getTargetEntity() {
        return targetEntity;
    }

    public Material getTargetMaterial() {
        return targetMaterial;
    }

    public int getAmount() {
        return amount;
    }

    public Material getReward() {
        return reward;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public int getProgress(Player player) {
        return progressMap.getOrDefault(player, 0);
    }

    public void setProgress(Player player, int progress) {
        progressMap.put(player, progress);
    }

    public boolean isCompleted(Player player) {
        return completedMap.getOrDefault(player, false);
    }

    public void setCompleted(Player player, boolean completed) {
        completedMap.put(player, completed);
    }

    public boolean isClaimed(Player player) {
        return claimedMap.getOrDefault(player, false);
    }

    public void setClaimed(Player player, boolean claimed) {
        claimedMap.put(player, claimed);
    }

    public String getObjective() {
        if (type.equalsIgnoreCase("kill") && targetEntity != null) {
            return "Kill " + amount + " " + targetEntity.name().toLowerCase().replace("_", " ") + "(s)";
        } else if (targetMaterial != null) {
            return "Collect " + amount + " " + targetMaterial.name().toLowerCase().replace("_", " ") + "(s)";
        }
        return "Objective not defined";
    }

    // Adds progress to the quest for the player
    public void addProgress(Player player, int amount) {
        int currentProgress = progressMap.getOrDefault(player, 0);
        currentProgress += amount;
        setProgress(player, currentProgress);

        if (currentProgress >= this.amount) {
            setCompleted(player, true);
        }
    }

    // Checks if the quest is claimable by the player
    public boolean isClaimable(Player player) {
        return isCompleted(player) && !isClaimed(player);
    }
}
