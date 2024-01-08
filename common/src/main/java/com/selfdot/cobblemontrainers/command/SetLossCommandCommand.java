package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetLossCommandCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        String lossCommand = StringArgumentType.getString(context, "lossCommand");
        trainer.setLossCommand(lossCommand);
        context.getSource().sendMessage(Text.literal(
            "Set loss command for trainer " + trainer.getName() + " to '" + lossCommand + "'"
        ));
        return SINGLE_SUCCESS;
    }

}
