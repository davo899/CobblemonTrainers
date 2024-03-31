package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetPartyMaximumLevelCommand extends TrainerCommand {
    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // trainers setpartymaximumlevel <trainer> <partyMaximumLevel>
        int partyMaximumLevel = IntegerArgumentType.getInteger(context, "partyMaximumLevel");
        trainer.setPartyMaximumLevel(partyMaximumLevel);
        context.getSource().sendMessage(Text.literal(
                "Set party maximum level for trainer " + trainer.getName() + " to " + partyMaximumLevel
        ));

        return SINGLE_SUCCESS;
    }
}