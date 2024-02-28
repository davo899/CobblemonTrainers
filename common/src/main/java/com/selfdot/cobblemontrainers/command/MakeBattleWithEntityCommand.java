package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MakeBattleWithEntityCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        if (trainer.getBattleTeam().isEmpty()) {
            context.getSource().sendError(Text.literal("Trainer " + trainer.getName() + " has no Pokémon"));
            return -1;
        }
        PokemonUtility.startTrainerBattle(
            player, trainer,
            (EntityArgumentType.getEntity(context, "entity") instanceof LivingEntity livingEntity) ?
                livingEntity : null
        );
        return SINGLE_SUCCESS;
    }

}
