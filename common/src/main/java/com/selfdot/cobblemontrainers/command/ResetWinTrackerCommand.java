package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ResetWinTrackerCommand extends TrainerCommand {
    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        CobblemonTrainers.INSTANCE.getTRAINER_WIN_TRACKER().reset(player, trainer);
        player.sendMessage(Text.literal(String.format(
            "Reset win tracker of %s for trainer %s", player.getGameProfile().getName(), trainer.getName())
        ));
        return SINGLE_SUCCESS;
    }
}
