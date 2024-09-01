
package com.yourname.questplugin.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuestAdminTabCompleter implements TabCompleter {

    private static final List<String> QUEST_TYPES = Arrays.asList("Mine", "Kill", "Collect");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Second argument is the quest type (Mine, Kill, Collect)
        if (args.length == 3) {
            return QUEST_TYPES.stream()
                    .filter(type -> type.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Third argument is the target (item for Mine/Collect, mob for Kill)
        if (args.length == 4) {
            if (args[2].equalsIgnoreCase("Kill")) {
                return Arrays.stream(EntityType.values())
                        .map(Enum::name)
                        .filter(name -> name.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
            } else {
                return Arrays.stream(Material.values())
                        .map(Enum::name)
                        .filter(name -> name.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        // Fifth argument is the reward
        if (args.length == 6) {
            return Arrays.stream(Material.values())
                    .map(Enum::name)
                    .filter(name -> name.toLowerCase().startsWith(args[5].toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Skip auto-completion for the reward amount (sixth argument)
        return new ArrayList<>();
    }
}
