package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public abstract class TrainerCommand extends TwoLayerCommand {

    protected Trainer trainer;

    @Override
    protected int runSuperCommand(CommandContext<ServerCommandSource> context) {
        String trainerName = StringArgumentType.getString(context, "trainer");
        trainer = CobblemonTrainers.INSTANCE.getTrainerRegistry().getTrainer(trainerName);
        if (trainer == null) {
            context.getSource().sendError(Text.literal("Trainer " + trainerName + " does not exist"));
            return -1;
        }
        return SINGLE_SUCCESS;
    }

}
