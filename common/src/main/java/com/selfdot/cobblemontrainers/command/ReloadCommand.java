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
        context.getSource().sendMessage(Text.literal("Reloaded CobblemonTrainers files"));
        return SINGLE_SUCCESS;
    }

}
