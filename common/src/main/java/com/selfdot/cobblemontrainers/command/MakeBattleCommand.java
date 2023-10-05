package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.cobblemon.mod.common.battles.*;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.TrainerBattleActor;
import com.cobblemon.mod.common.battles.ai.RandomBattleAI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerBattleRewarder;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import com.selfdot.cobblemontrainers.util.PokemonUtility;
import kotlin.Unit;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class MakeBattleCommand extends TrainerCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(src -> CobblemonTrainersPermissions.checkPermission(
                src, new CobblemonPermission("", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            ))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("makebattle")
                .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>
                    argument("player", EntityArgumentType.player())
                    .suggests((context, builder) -> EntityArgumentType.player().listSuggestions(context, builder))
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("trainer", string())
                        .suggests(new TrainerNameSuggestionProvider())
                        .executes(this::execute)
                    )
                )
            )
        );
    }

    protected int run(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();

        ServerPlayerEntity player;
        try {
            player = EntityArgumentType.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            return 0;
        }

        String name = ctx.getArgument("trainer", String.class);
        Trainer trainer = TrainerRegistry.getInstance().getTrainer(name);

        if (trainer == null) {
            source.sendError(Text.literal("Trainer " + name + " does not exist"));
            return -1;
        }

        if (trainer.getBattleTeam().isEmpty()) {
            source.sendError(Text.literal("Trainer " + name + " has no Pokémon"));
            return -1;
        }

        PokemonUtility.startBattle(player, trainer, BattleFormat.Companion.getGEN_9_SINGLES())
            .ifErrored(error -> {
                error.sendTo(player, t -> t);
                return Unit.INSTANCE;
            })
            .ifSuccessful(battle -> {
                TrainerBattleRewarder.getInstance().addBattleReward(battle, trainer.getWinCommand());
                return Unit.INSTANCE;
            });
        return 1;
    }

}
