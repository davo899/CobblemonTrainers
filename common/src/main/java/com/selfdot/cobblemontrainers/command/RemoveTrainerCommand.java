package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RemoveTrainerCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        if (!CobblemonTrainers.INSTANCE.getTrainerRegistry().removeTrainer(trainer.getName())) {
            context.getSource().sendError(Text.literal("Trainer " + trainer.getName() + " does not exist"));
            return -1;
        }
        context.getSource().sendMessage(Text.literal("Removed trainer " + trainer.getName()));
        return SINGLE_SUCCESS;
    }

}
