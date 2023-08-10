package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class AddTrainerCommand implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            LiteralArgumentBuilder.<ServerCommandSource>
                literal("trainers").then(literal("add")).executes(this)
        );
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) {
        return 0;
    }

}
