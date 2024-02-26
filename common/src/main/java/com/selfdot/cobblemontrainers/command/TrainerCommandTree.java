package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.util.CommandUtils;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class TrainerCommandTree {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CobblemonTrainers mod) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(source -> !mod.isDisabled())
            .requires(source -> CommandUtils.hasPermission(source, "selfdot.op.trainers"))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("add")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string())
                    .executes(new AddTrainerCommand())
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("battle")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .executes(new BattleTrainerCommand())
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
                literal("setwincommandlist")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, List<String>>
                        argument("commandList", new CommandListArgumentType())
                        .executes(new SetWinCommandCommand())
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("setlosscommandlist")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("trainer", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .then(RequiredArgumentBuilder.<ServerCommandSource, List<String>>
                        argument("commandList", new CommandListArgumentType())
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
        );
    }

}
