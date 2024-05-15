package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Slf4j
public class MakeBattleCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        log.debug("Starting to execute: " + context.getInput());

        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        if (trainer.getBattleTeam().isEmpty()) {
            context.getSource().sendError(Text.literal("Trainer " + trainer.getName() + " has no Pokémon"));
            return -1;
        }
        PokemonUtility.startTrainerBattle(
            player, trainer,
            context.getSource().getEntity() instanceof LivingEntity livingEntity ? livingEntity : null
        );
        return SINGLE_SUCCESS;
    }

}
