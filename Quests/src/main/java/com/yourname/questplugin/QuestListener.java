package com.yourname.questplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class QuestListener implements Listener {

    private final QuestManager questManager;
    private final QuestDataManager questDataManager;

    public QuestListener(QuestManager questManager, QuestDataManager questDataManager) {
        this.questManager = questManager;
        this.questDataManager = questDataManager;
    }

    // Handle entity kills (mob killing)
    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player != null) {
            Quest activeQuest = questManager.getActiveQuest(player);
            if (activeQuest != null && activeQuest.getType().equalsIgnoreCase("kill") && activeQuest.getTargetEntity() == event.getEntityType()) {
                if (questManager.isActiveQuest(player, activeQuest)) {
                    activeQuest.addProgress(player, 1);

                    // Send progress update message
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.GOLD + "You have killed " + activeQuest.getProgress(player) + "/" + activeQuest.getAmount() + " " + formatEntityName(activeQuest.getTargetEntity()) + ".");

                    if (activeQuest.isClaimable(player)) {
                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.GOLD + "You have completed the quest: " + activeQuest.getName() + "! Type /quests to claim your reward.");
                        refreshQuestGUI(player);
                        questDataManager.savePlayerQuestData(player, activeQuest); // Save quest data
                    }
                }
            }
        }
    }

    // Handle block breaking (mining)
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();

        Quest activeQuest = questManager.getActiveQuest(player);
        if (activeQuest != null && activeQuest.getType().equalsIgnoreCase("mine") && activeQuest.getTargetMaterial() == blockType) {
            if (questManager.isActiveQuest(player, activeQuest)) {
                activeQuest.addProgress(player, 1);

                // Send progress update message
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.GOLD + "You have mined " + activeQuest.getProgress(player) + "/" + activeQuest.getAmount() + " " + formatMaterialName(activeQuest.getTargetMaterial()) + ".");

                if (activeQuest.isClaimable(player)) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.GOLD + "You have completed the quest: " + activeQuest.getName() + "! Type /quests to claim your reward.");
                    refreshQuestGUI(player);
                    questDataManager.savePlayerQuestData(player, activeQuest); // Save quest data
                }
            }
        }
    }

    // Handle item collection (picking up items)
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack itemStack = event.getItem().getItemStack();
            Material itemType = itemStack.getType();
            int itemAmount = itemStack.getAmount();

            Quest activeQuest = questManager.getActiveQuest(player);
            if (activeQuest != null && activeQuest.getType().equalsIgnoreCase("collect") && activeQuest.getTargetMaterial() == itemType) {
                if (questManager.isActiveQuest(player, activeQuest)) {
                    activeQuest.addProgress(player, itemAmount);

                    // Send progress update message
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.GOLD + "You have collected " + activeQuest.getProgress(player) + "/" + activeQuest.getAmount() + " " + formatMaterialName(activeQuest.getTargetMaterial()) + ".");

                    if (activeQuest.isClaimable(player)) {
                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.GOLD + "You have completed the quest: " + activeQuest.getName() + "! Type /quests to claim your reward.");
                        refreshQuestGUI(player);
                        questDataManager.savePlayerQuestData(player, activeQuest); // Save quest data
                    }
                }
            }
        }
    }

    // Format the entity name to be more user-friendly
    private String formatEntityName(EntityType entityType) {
        return entityType.name().toLowerCase().replace("_", " ");
    }

    // Format the material name to be more user-friendly
    private String formatMaterialName(Material material) {
        return material.name().toLowerCase().replace("_", " ");
    }

    // Refresh the player's quest GUI
    private void refreshQuestGUI(Player player) {
        if (player.getOpenInventory().getTitle().equals("Available Quests")) {
            player.closeInventory();
            player.performCommand("quests");
        }
    }
}
