package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetWinCommandCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        String winCommand = StringArgumentType.getString(context, "winCommand");
        trainer.setWinCommand(winCommand);
        context.getSource().sendMessage(Text.literal(
            "Set win command for trainer " + trainer.getName() + " to '" + winCommand + "'"
        ));
        return SINGLE_SUCCESS;
    }

}
