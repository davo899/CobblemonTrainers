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
            .then(
                LiteralArgumentBuilder.<ServerCommandSource>literal("reload")
                .requires(new CommandRequirementBuilder().needsPermission(TrainersPermissions.RELOAD).build())
                .executes(new ReloadCommand())
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("resetwintracker")
                .requires(new CommandRequirementBuilder()
                    .modEnabled().needsPermission(TrainersPermissions.RELOAD).build()
                )
                .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>
                    argument("player", EntityArgumentType.player())
                    .suggests((context, builder) -> EntityArgumentType.player().listSuggestions(context, builder))
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("trainer", string())
                        .suggests(new TrainerNameSuggestionProvider())
                        .executes(new ResetWinTrackerCommand())
                    )
                )
            )
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
                literal("makebattle")
                .requires(new CommandRequirementBuilder()
                    .modEnabled().needsPermission(TrainersPermissions.MAKEBATTLE).build()
                )
                .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>
                    argument("player", EntityArgumentType.player())
                    .suggests((context, builder) -> EntityArgumentType.player().listSuggestions(context, builder))
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("trainer", string())
                        .suggests(new TrainerNameSuggestionProvider())
                        .executes(new MakeBattleCommand())
                        .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>
                            argument("entity", EntityArgumentType.entity())
                            .executes(new MakeBattleWithEntityCommand())
                        )
                    )
                )
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
                literal("remove")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .executes(new RemoveTrainerCommand())
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("rename")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("newName", string())
                        .executes(new RenameTrainerCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setgroup")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("group", string())
                        .suggests(new TrainerGroupSuggestionProvider())
                        .executes(new SetGroupCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setwincommand")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("winCommand", string())
                        .executes(new SetWinCommandCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setlosscommand")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("lossCommand", string())
                        .executes(new SetLossCommandCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setcanonlybeatonce")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, Boolean>
                        argument("canOnlyBeatOnce", bool())
                        .executes(new SetCanOnlyBeatOnceCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("addfromparty")
                .requires(new CommandRequirementBuilder()
                    .modEnabled().needsPermission(TrainersPermissions.EDIT).executedByPlayer().build()
                )
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, Integer>
                        argument("pokemon", PartySlotArgumentType.Companion.partySlot())
                        .executes(new AddFromPartyCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("addpokemon")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, PokemonProperties>
                        argument("pokemon", PokemonPropertiesArgumentType.Companion.properties())
                        .executes(new AddPokemonCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setcooldownseconds")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, Long>
                        argument("cooldownSeconds", longArg())
                        .executes(new SetCooldownSecondsCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setpartymaximumlevel")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, Integer>
                        argument("partyMaximumLevel", integer(1, 100))
                        .executes(new SetPartyMaximumLevelCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("adddefeatrequirement")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("defeatRequirement", string())
                        .suggests(new TrainerNameSuggestionProvider())
                        .executes(new AddDefeatRequirementCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("removedefeatrequirement")
                .requires(editCommandRequirement)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("defeatRequirement", string())
                        .suggests((context, builder) -> {
                            Trainer trainer = CobblemonTrainers.INSTANCE.getTrainerRegistry()
                                .getTrainer(StringArgumentType.getString(context, "trainer"));
                            if (trainer == null) return builder.buildFuture();
                            trainer.getDefeatRequiredTrainers().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(new RemoveDefeatRequirementCommand())
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
