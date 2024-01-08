package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public abstract class TwoLayerCommand implements Command<ServerCommandSource> {

    protected abstract int runSuperCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
    protected abstract int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int state = runSuperCommand(context);
        if (state != SINGLE_SUCCESS) return state;
        return runSubCommand(context);
    }

}
