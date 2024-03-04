package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetCooldownSecondsCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        long cooldownSeconds = LongArgumentType.getLong(context, "cooldownSeconds");
        trainer.setCooldownSeconds(cooldownSeconds);
        context.getSource().sendMessage(Text.literal(
            "Set cooldown for trainer " + trainer.getName() + " to " + cooldownSeconds + " seconds"
        ));
        return SINGLE_SUCCESS;
    }

}
