package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadCommand implements Command<ServerCommandSource> {

    public int run(CommandContext<ServerCommandSource> context) {
        CobblemonTrainers.INSTANCE.enable();
        CobblemonTrainers.INSTANCE.loadFiles();
        if (CobblemonTrainers.INSTANCE.isDisabled()) {
            context.getSource().sendError(Text.literal(
                "An error occurred while reloading, check the server log."
            ));
        } else {
            context.getSource().sendMessage(Text.literal("Reloaded CobblemonTrainers files successfully"));
        }
        return SINGLE_SUCCESS;
    }

}
