package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadCommand implements Command<ServerCommandSource> {

    public int run(CommandContext<ServerCommandSource> context) {
        TrainerRegistry.getInstance().loadTrainersFromFile(CobblemonTrainers.TRAINER_DATA_FILENAME);
        context.getSource().sendMessage(Text.literal("Reloaded trainer file"));
        return SINGLE_SUCCESS;
    }

}
