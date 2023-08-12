package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class AddTrainerCommand implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("add")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string()).executes(this)
                )
            )
        );
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) {
        String name = ctx.getArgument("name", String.class);

        List<TrainerPokemon> trainerTeam = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Pokemon pokemon = new Pokemon();
            pokemon.initializeMoveset(true);
            trainerTeam.add(TrainerPokemon.fromPokemon(pokemon));
        }

        if (!TrainerRegistry.getInstance().addTrainer(new Trainer(name, trainerTeam))) {
            ctx.getSource().sendError(Text.literal("Trainer " + name + " already exists"));
            return -1;
        }
        ctx.getSource().sendMessage(Text.literal("Added new trainer " + name));
        return 1;
    }

}
