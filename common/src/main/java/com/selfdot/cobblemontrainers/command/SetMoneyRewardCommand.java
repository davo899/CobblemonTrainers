package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetMoneyRewardCommand extends TrainerCommand {
    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        long moneyReward = LongArgumentType.getLong(context, "moneyReward");
        trainer.setMoneyReward(moneyReward);
        context.getSource().sendMessage(Text.literal(
                String.format("Set money reward for trainer %s to %,d", trainer.getName(), moneyReward)
        ));
        return SINGLE_SUCCESS;
    }
}
