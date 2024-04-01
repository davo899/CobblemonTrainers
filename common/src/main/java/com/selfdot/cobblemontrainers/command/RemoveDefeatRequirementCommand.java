package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RemoveDefeatRequirementCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String defeatRequirement = StringArgumentType.getString(context, "defeatRequirement");
        if (!trainer.removeDefeatRequirement(defeatRequirement)) {
            context.getSource().sendError(Text.literal(
                "Trainer " + trainer.getName() + " did not require having defeated " + defeatRequirement
            ));
            return -1;
        }

        context.getSource().sendMessage(Text.literal(
            "Made trainer " + trainer.getName() + " not require having defeated " + defeatRequirement
        ));
        return SINGLE_SUCCESS;
    }

}
