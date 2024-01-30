package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;

import java.util.List;
import java.util.stream.Collectors;

public class CommandListArgumentType implements ArgumentType<List<String>> {

    @Override
    public List<String> parse(StringReader reader) {
        String string = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return List.of(string.split(">>"));
    }

    public static List<String> getCommands(final CommandContext<?> context, final String name) {
        List<?> list = context.getArgument(name, List.class);
        if (!list.isEmpty()) {
            if (list.get(0) instanceof String) {
                return list.stream().map(s -> ((String)s).trim()).collect(Collectors.toList());
            }
        }
        return List.of();
    }

}
