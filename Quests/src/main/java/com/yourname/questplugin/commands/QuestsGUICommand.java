package com.yourname.questplugin.commands;

import com.yourname.questplugin.Quest;
import com.yourname.questplugin.QuestDataManager;
import com.yourname.questplugin.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class QuestsGUICommand implements CommandExecutor, Listener {

    private final QuestManager questManager;
    private final QuestDataManager questDataManager;

    public QuestsGUICommand(QuestManager questManager, QuestDataManager questDataManager) {
        this.questManager = questManager;
        this.questDataManager = questDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        openQuestsGUI(player, 1);  // Open the first page of the quests GUI

        return true;
    }

    private void openQuestsGUI(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.LIGHT_PURPLE + "Available Quests - Page " + page);

        int startIndex = (page - 1) * 18;
        int endIndex = startIndex + 18;
        int questIndex = 0;

        for (Quest quest : questManager.getAllQuests()) {
            if (questIndex >= startIndex && questIndex < endIndex) {
                ItemStack itemStack;
                if (quest.isClaimed(player)) {
                    itemStack = new ItemStack(Material.NETHER_STAR);
                } else if (questManager.isActiveQuest(player, quest)) {
                    itemStack = new ItemStack(Material.ENCHANTED_BOOK);  // Enchanted book for active quests
                } else {
                    itemStack = new ItemStack(Material.BOOK);
                }

                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(ChatColor.GOLD + quest.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.YELLOW + "Progress: " + quest.getProgress(player) + "/" + quest.getAmount());
                lore.add(ChatColor.AQUA + "Reward: " + quest.getRewardAmount() + " " + quest.getReward().name());
                if (quest.isClaimed(player)) {
                    lore.add(ChatColor.GREEN + "Claimed");
                } else if (quest.isCompleted(player)) {
                    lore.add(ChatColor.YELLOW + "Click to claim your reward.");
                } else if (questManager.isActiveQuest(player, quest)) {
                    lore.add(ChatColor.RED + "Active Quest");
                } else {
                    lore.add(ChatColor.YELLOW + "Click to start this quest.");
                }
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                inventory.addItem(itemStack);
            }
            questIndex++;
        }

        // Add navigation items (Previous Page, Next Page)
        if (page > 1) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta previousMeta = previousPage.getItemMeta();
            previousMeta.setDisplayName(ChatColor.RED + "Previous Page");
            previousPage.setItemMeta(previousMeta);
            inventory.setItem(18, previousPage);
        }

        if (questIndex > endIndex) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(ChatColor.GREEN + "Next Page");
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(26, nextPage);
        }

        // Add exit button
        ItemStack exitButton = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitButton.getItemMeta();
        exitMeta.setDisplayName(ChatColor.RED + "Exit");
        exitButton.setItemMeta(exitMeta);
        inventory.setItem(22, exitButton);

        // Add deactivate button
        ItemStack deactivateButton = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta deactivateMeta = deactivateButton.getItemMeta();
        deactivateMeta.setDisplayName(ChatColor.RED + "Deactivate Active Quest");
        List<String> deactivateLore = new ArrayList<>();
        deactivateLore.add(ChatColor.YELLOW + "Click to deactivate your current quest.");
        deactivateMeta.setLore(deactivateLore);
        deactivateButton.setItemMeta(deactivateMeta);
        inventory.setItem(21, deactivateButton);

        player.openInventory(inventory);
    }

    @org.bukkit.event.EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith(ChatColor.LIGHT_PURPLE + "Available Quests")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String displayName = clickedItem.getItemMeta().getDisplayName();

            if (displayName.equals(ChatColor.RED + "Exit")) {
                player.closeInventory();
                return;
            }

            if (displayName.equals(ChatColor.RED + "Deactivate Active Quest")) {
                Quest activeQuest = questManager.getActiveQuest(player);
                if (activeQuest != null) {
                    questManager.clearActiveQuest(player);
                    player.sendMessage(ChatColor.RED + "You have deactivated the quest: " + activeQuest.getName());
                    openQuestsGUI(player, 1);  // Reopen the first page
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have an active quest to deactivate.");
                }
                return;
            }

            // Handle page navigation
            if (displayName.equals(ChatColor.GREEN + "Next Page")) {
                int currentPage = Integer.parseInt(event.getView().getTitle().split("Page ")[1]);
                openQuestsGUI(player, currentPage + 1);
                return;
            } else if (displayName.equals(ChatColor.RED + "Previous Page")) {
                int currentPage = Integer.parseInt(event.getView().getTitle().split("Page ")[1]);
                openQuestsGUI(player, currentPage - 1);
                return;
            }

            // Handle quest item clicks
            for (Quest quest : questManager.getAllQuests()) {
                if (displayName.equals(ChatColor.GOLD + quest.getName())) {
                    if (quest.isCompleted(player)) {
                        if (!quest.isClaimed(player)) {
                            player.getInventory().addItem(new ItemStack(quest.getReward(), quest.getRewardAmount()));
                            quest.setClaimed(player, true);
                            questManager.clearActiveQuest(player);
                            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.GREEN + "You claimed the reward for " + quest.getName() + "!");

                            // Update the item in the GUI to a nether star after claiming
                            ItemStack claimedItem = new ItemStack(Material.NETHER_STAR);
                            ItemMeta claimedMeta = claimedItem.getItemMeta();
                            claimedMeta.setDisplayName(ChatColor.GOLD + quest.getName());
                            List<String> claimedLore = new ArrayList<>();
                            claimedLore.add(ChatColor.YELLOW + "Progress: " + quest.getProgress(player) + "/" + quest.getAmount());
                            claimedLore.add(ChatColor.AQUA + "Reward: " + quest.getRewardAmount() + " " + quest.getReward().name());
                            claimedLore.add(ChatColor.GREEN + "Claimed");
                            claimedMeta.setLore(claimedLore);
                            claimedItem.setItemMeta(claimedMeta);

                            // Update the item in the inventory view
                            event.getInventory().setItem(event.getSlot(), claimedItem);

                            questDataManager.savePlayerQuestData(player, quest); // Save quest data
                        } else {
                            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.RED + "You have already claimed the reward for this quest.");
                        }
                    } else if (questManager.isActiveQuest(player, quest)) {
                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.RED + "You are already working on this quest.");
                    } else {
                        questManager.setActiveQuest(player, quest);
                        player.sendMessage(ChatColor.GOLD + "You have started the quest: " + quest.getName() + "!");
                        player.sendMessage(ChatColor.YELLOW + "Objective: " + quest.getObjective());
                        player.sendMessage(ChatColor.AQUA + "Reward: " + quest.getRewardAmount() + " " + quest.getReward().name());

                        // Update the item in the GUI to an enchanted book for active quest
                        ItemStack activeItem = new ItemStack(Material.ENCHANTED_BOOK);
                        ItemMeta activeMeta = activeItem.getItemMeta();
                        activeMeta.setDisplayName(ChatColor.GOLD + quest.getName());
                        List<String> activeLore = new ArrayList<>();
                        activeLore.add(ChatColor.YELLOW + "Progress: " + quest.getProgress(player) + "/" + quest.getAmount());
                        activeLore.add(ChatColor.AQUA + "Reward: " + quest.getRewardAmount() + " " + quest.getReward().name());
                        activeLore.add(ChatColor.RED + "Active Quest");
                        activeMeta.setLore(activeLore);
                        activeItem.setItemMeta(activeMeta);

                        // Update the item in the inventory view
                        event.getInventory().setItem(event.getSlot(), activeItem);

                        questDataManager.savePlayerQuestData(player, quest); // Save quest data
                        refreshQuestGUI(player);
                    }
                    break;
                }
            }
        }
    }

    // Refresh the player's quest GUI
    private void refreshQuestGUI(Player player) {
        int currentPage = Integer.parseInt(player.getOpenInventory().getTitle().split("Page ")[1]);
        openQuestsGUI(player, currentPage);
    }
}
