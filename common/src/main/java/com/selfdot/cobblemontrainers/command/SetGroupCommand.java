package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetGroupCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        String group = StringArgumentType.getString(context, "group");
        trainer.setGroup(group);
        context.getSource().sendMessage(Text.literal(
            "Set group for trainer " + trainer.getName() + " to " + group
        ));
        return SINGLE_SUCCESS;
    }

}
