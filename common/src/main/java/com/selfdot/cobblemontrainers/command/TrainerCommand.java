package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public abstract class TrainerCommand {

    protected int execute(CommandContext<ServerCommandSource> ctx) {
        if (CobblemonTrainers.INSTANCE.getDisabled()) {
            ctx.getSource().sendError(Text.literal(
                "CobblemonTrainers has been disabled due to an error, tell davo899 to fix!"
            ));
            return -1;
        } else {
            return run(ctx);
        }
    }

    protected abstract int run(CommandContext<ServerCommandSource> ctx);

}
