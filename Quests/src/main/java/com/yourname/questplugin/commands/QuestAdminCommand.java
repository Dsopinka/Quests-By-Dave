package com.yourname.questplugin.commands;

import com.yourname.questplugin.QuestManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class QuestAdminCommand implements CommandExecutor {

    private final QuestManager questManager;

    public QuestAdminCommand(QuestManager questManager) {
        this.questManager = questManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            showUsageMessage(sender);
            return true; // Return true to prevent the default usage message from showing
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 7) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " +  ChatColor.YELLOW + "/questadmin create <name> <type> <target> <amount> <reward> <rewardAmount>");
                return true; // Return true to prevent the default usage message from showing
            }
            String name = args[1];
            String type = args[2];
            String target = args[3];
            int amount;
            int rewardAmount;
            try {
                amount = Integer.parseInt(args[4]);
                rewardAmount = Integer.parseInt(args[6]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.GOLD + "Amount and rewardAmount must be numbers.");
                return true; // Return true to prevent the default usage message from showing
            }
            String reward = args[5];

            questManager.createQuest(name, type, target, amount, reward, rewardAmount);
            showSuccessMessage(sender, name);
            return true;
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " +  ChatColor.YELLOW + "/questadmin remove <name>");
                return true; // Return true to prevent the default usage message from showing
            }
            String name = args[1];
            questManager.removeQuest(name);
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.GREEN + "Quest " + ChatColor.AQUA + name + ChatColor.GREEN + " removed successfully!");
            return true;
        } else if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(ChatColor.GOLD + "Available Quests:");
            for (String questName : questManager.getAllQuestNames()) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.YELLOW + "- " + ChatColor.AQUA + questName);
            }
            return true;
        } else {
            showUsageMessage(sender);
            return true; // Return true to prevent the default usage message from showing
        }
    }

    private void showUsageMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "QuestAdmin Command Usage:");
        sender.sendMessage(ChatColor.YELLOW + " - " + ChatColor.RED + "/questadmin create <name> <type> <target> <amount> <reward> <rewardAmount>");
        sender.sendMessage(ChatColor.YELLOW + " - " + ChatColor.RED + "/questadmin list");
        sender.sendMessage(ChatColor.YELLOW + " - " + ChatColor.RED + "/questadmin remove <name>");
    }

    private void showSuccessMessage(CommandSender sender, String questName) {
        sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "QUESTS: " + ChatColor.GREEN + "Quest " + ChatColor.AQUA + questName + ChatColor.GREEN + " created successfully!");
    }
}
