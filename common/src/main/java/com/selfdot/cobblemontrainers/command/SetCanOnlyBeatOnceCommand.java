package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetCanOnlyBeatOnceCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        boolean canOnlyBeatOnce = BoolArgumentType.getBool(context, "canOnlyBeatOnce");
        trainer.setCanOnlyBeatOnce(canOnlyBeatOnce);
        context.getSource().sendMessage(Text.literal(
            "Set trainer " + trainer.getName() + " can only beat once to " + canOnlyBeatOnce
        ));
        return SINGLE_SUCCESS;
    }

}
