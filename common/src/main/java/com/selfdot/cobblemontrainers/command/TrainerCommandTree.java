package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.command.argument.PartySlotArgumentType;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.util.CommandUtils;
import com.selfdot.cobblemontrainers.util.DataKeys;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Predicate;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class TrainerCommandTree {

    private static Predicate<ServerCommandSource> sourceWithPermission(String permission, CobblemonTrainers mod) {
        return source ->
            !mod.isDisabled() &&
            (
                CommandUtils.hasPermission(source, "selfdot.op.trainers") ||
                CommandUtils.hasPermission(source, "selfdot.trainers." + permission)
            );
    }

    private static Predicate<ServerCommandSource> playerWithPermission(String permission, CobblemonTrainers mod) {
        return source -> source.isExecutedByPlayer() && sourceWithPermission(permission, mod).test(source);
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CobblemonTrainers mod) {

        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("reload")
                .requires(sourceWithPermission(DataKeys.RELOAD_COMMAND_PERMISSION, mod))
                .executes(new ReloadCommand())
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("battle")
                .requires(playerWithPermission(DataKeys.BATTLE_COMMAND_PERMISSION, mod))
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .executes(new BattleTrainerCommand())
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("makebattle")
                .requires(sourceWithPermission(DataKeys.MAKEBATTLE_COMMAND_PERMISSION, mod))
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
                .requires(playerWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
                .executes(new SetupCommand())
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("add")
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
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
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .executes(new RemoveTrainerCommand())
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("rename")
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
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
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
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
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
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
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
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
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
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
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
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
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
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
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
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
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
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
                literal("setmoneyreward")
                .requires(sourceWithPermission(DataKeys.EDIT_COMMAND_PERMISSION, mod))
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, Long>
                        argument("moneyReward", longArg())
                        .executes(new SetMoneyRewardCommand())
                    )
                )
            )
        );

        // && CommandUtils.hasPermission(source, "selfdot.trainers.battle")
    }

}
