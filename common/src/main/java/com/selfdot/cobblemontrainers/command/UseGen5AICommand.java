package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class UseGen5AICommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        CobblemonTrainers.INSTANCE.getConfig().setStrongAILevel(-1);
        CobblemonTrainers.INSTANCE.getConfig().save();
        context.getSource().sendMessage(Text.literal("Trainers will use Gen 5 AI"));
        return SINGLE_SUCCESS;
    }

}
