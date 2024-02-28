package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
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
        PokemonUtility.startTrainerBattle(player, trainer, null);
        return SINGLE_SUCCESS;
    }

}
