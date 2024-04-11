package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class AddTrainerWithGroupCommand implements Command<ServerCommandSource> {

    public int run(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        String group = StringArgumentType.getString(context, "group");
        if (!CobblemonTrainers.INSTANCE.getTrainerRegistry()
            .addTrainer(new Trainer(CobblemonTrainers.INSTANCE, name, group))
        ) {
            context.getSource().sendError(Text.literal("Trainer " + name + " already exists"));
            return -1;
        }
        context.getSource().sendMessage(Text.literal("Added new trainer " + name + " to group " + group));
        return SINGLE_SUCCESS;
    }

}
