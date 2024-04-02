package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadCommand implements Command<ServerCommandSource> {

    public int run(CommandContext<ServerCommandSource> context) {
        CobblemonTrainers.INSTANCE.enable();
        CobblemonTrainers.INSTANCE.getTRAINER_REGISTRY().load();
        context.getSource().sendMessage(Text.literal("Reloaded trainer file"));
        return SINGLE_SUCCESS;
    }

}
