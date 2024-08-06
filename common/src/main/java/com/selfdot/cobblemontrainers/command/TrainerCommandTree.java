package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.command.argument.PartySlotArgumentType;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.command.permission.CommandRequirementBuilder;
import com.selfdot.cobblemontrainers.command.permission.TrainersPermissions;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Predicate;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

@Slf4j
public class TrainerCommandTree {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        Predicate<ServerCommandSource> editCommandRequirement = new CommandRequirementBuilder()
            .modEnabled().needsPermission(TrainersPermissions.EDIT).build();

        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("battle")
                .requires(new CommandRequirementBuilder()
                    .modEnabled().needsPermission(TrainersPermissions.BATTLE).executedByPlayer().build()
                )
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .executes(new BattleTrainerCommand())
                )
                .executes(new OpenBattleTrainerMenuCommand())
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setup")
                .requires(new CommandRequirementBuilder()
                    .modEnabled().needsPermission(TrainersPermissions.EDIT).executedByPlayer().build()
                )
                .executes(new SetupCommand())
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("add")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string())
                    .executes(new AddTrainerCommand())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("group", string())
                        .executes(new AddTrainerWithGroupCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("usegen5ai")
                .requires(editCommandRequirement)
                .executes(new UseGen5AICommand())
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("usestrongai")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, Integer>
                    argument("level", integer())
                    .suggests((context, builder) -> {
                        for (int i = 0; i <= 5; i++) builder.suggest(i);
                        return builder.buildFuture();
                    })
                    .executes(new UseStrongAICommand())
                )
            )
        );
    }

}
