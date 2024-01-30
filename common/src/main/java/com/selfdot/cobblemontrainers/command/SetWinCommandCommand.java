package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

public class SetWinCommandCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        List<String> commandList = CommandListArgumentType.getCommands(context, "commandList");
        trainer.setWinCommandList(commandList);
        context.getSource().sendMessage(Text.literal(
            "Set trainer " + trainer.getName() + "'s win command list to:"
        ));
        commandList.forEach(command -> context.getSource().sendMessage(Text.literal("  " + command)));
        return SINGLE_SUCCESS;
    }

}
