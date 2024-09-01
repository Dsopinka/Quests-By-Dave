package com.yourname.questplugin.commands;

import com.yourname.questplugin.QuestManager;
import com.yourname.questplugin.QuestDataManager; // Import the QuestDataManager
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestsCommand implements CommandExecutor {

    private final QuestManager questManager;
    private final QuestDataManager questDataManager; // Add QuestDataManager as a field

    // Update the constructor to accept both QuestManager and QuestDataManager
    public QuestsCommand(QuestManager questManager, QuestDataManager questDataManager) {
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
        if (questManager.hasActiveQuest(player)) {
            sender.sendMessage(ChatColor.GOLD + "Active Quest: " + questManager.getActiveQuest(player).getName());
        } else {
            sender.sendMessage(ChatColor.RED + "You have no active quests.");
        }

        // Save the player's quest data
        questDataManager.savePlayerQuestData(player, questManager.getActiveQuest(player));

        return true;
    }
}
