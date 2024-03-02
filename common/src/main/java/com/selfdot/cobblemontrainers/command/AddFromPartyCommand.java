package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.command.argument.PartySlotArgumentType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class AddFromPartyCommand extends TrainerCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (trainer.getTeam().size() >= 6) {
            context.getSource().sendError(Text.literal("Trainer's team is full"));
            return -1;
        }
        Pokemon pokemon = PartySlotArgumentType.Companion.getPokemon(context, "pokemon");
        trainer.getTeam().add(TrainerPokemon.fromPokemon(pokemon));
        context.getSource().sendMessage(Text.literal(
            "Added " + pokemon.getDisplayName().getString() + " to " + trainer.getName() + "'s party"
        ));
        return SINGLE_SUCCESS;
    }

}
