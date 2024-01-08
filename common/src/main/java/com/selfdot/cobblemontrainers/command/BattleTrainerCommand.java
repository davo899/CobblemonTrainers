package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.battles.*;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.trainer.TrainerBattleRewarder;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import kotlin.Unit;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class BattleTrainerCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        if (trainer.getBattleTeam().isEmpty()) {
            context.getSource().sendError(Text.literal("Trainer " + trainer.getName() + " has no Pokémon"));
            return -1;
        }
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        PokemonUtility.startBattle(player, trainer, BattleFormat.Companion.getGEN_9_SINGLES())
            .ifErrored(error -> {
                error.sendTo(context.getSource().getPlayer(), t -> t);
                return Unit.INSTANCE;
            })
            .ifSuccessful(battle -> {
                TrainerBattleRewarder.getInstance().addBattleReward(battle, trainer.getWinCommand());
                return Unit.INSTANCE;
            });
        return SINGLE_SUCCESS;
    }

}
