package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RenameTrainerCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        String oldName = trainer.getName();
        String newName = StringArgumentType.getString(context, "newName");
        if (CobblemonTrainers.INSTANCE.getTRAINER_REGISTRY().getTrainer(newName) != null) {
            context.getSource().sendError(Text.literal("Trainer " + newName + " already exists"));
            return -1;
        }
        CobblemonTrainers.INSTANCE.getTRAINER_REGISTRY().removeTrainer(oldName);
        trainer.setName(newName);
        CobblemonTrainers.INSTANCE.getTRAINER_REGISTRY().addTrainer(trainer);
        CobblemonTrainers.INSTANCE.getTRAINER_WIN_TRACKER().rename(oldName, newName);
        context.getSource().sendMessage(Text.literal("Renamed trainer " + oldName + " to " + newName));
        return SINGLE_SUCCESS;
    }

}
