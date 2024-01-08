package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RenameTrainerCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        String oldName = trainer.getName();
        String newName = StringArgumentType.getString(context, "newName");
        if (TrainerRegistry.getInstance().getTrainer(newName) != null) {
            context.getSource().sendError(Text.literal("Trainer " + newName + " already exists"));
            return -1;
        }
        trainer.setName(newName);
        TrainerRegistry.getInstance().removeTrainer(oldName);
        TrainerRegistry.getInstance().addTrainer(trainer);
        context.getSource().sendMessage(Text.literal("Renamed trainer " + oldName + " to " + newName));
        return SINGLE_SUCCESS;
    }

}
