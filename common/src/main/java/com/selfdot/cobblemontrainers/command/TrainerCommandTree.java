package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.command.argument.PartySlotArgumentType;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.util.CommandUtils;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class TrainerCommandTree {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CobblemonTrainers mod) {

        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(source -> !mod.isDisabled() && CommandUtils.hasPermission(source, "selfdot.trainers.battle"))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("battle")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .executes(new BattleTrainerCommand())
                )
            )
        );

        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(source -> !mod.isDisabled() && CommandUtils.hasPermission(source, "selfdot.op.trainers"))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("add")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string())
                    .executes(new AddTrainerCommand())
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("add")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("group", string())
                        .executes(new AddTrainerWithGroupCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("makebattle")
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
                literal("reload")
                .executes(new ReloadCommand())
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("remove")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .executes(new RemoveTrainerCommand())
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("rename")
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
                literal("setup")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .executes(new SetupCommand())
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setwincommand")
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
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, PokemonProperties>
                        argument("pokemon", PokemonPropertiesArgumentType.Companion.properties())
                        .executes(new AddPokemonCommand())
                    )
                )
            )
        );
    }

}
