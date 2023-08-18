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
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerBattleRewarder;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import kotlin.Unit;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class BattleTrainerCommand implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(src -> CobblemonTrainersPermissions.checkPermission(
                src, new CobblemonPermission("", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            ))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("battle")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string())
                    .suggests(new TrainerNameSuggestionProvider())
                    .executes(this)
                )
            )
        );
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        if (!source.isExecutedByPlayer() || source.getPlayer() == null) {
            source.sendError(Text.literal("Must be a player to battle trainers"));
            return -1;
        }

        String name = ctx.getArgument("name", String.class);
        Trainer trainer = TrainerRegistry.getInstance().getTrainer(name);

        if (trainer == null) {
            source.sendError(Text.literal("Trainer " + name + " does not exist"));
            return -1;
        }

        if (trainer.getBattleTeam().isEmpty()) {
            source.sendError(Text.literal("Trainer " + name + " has no Pokémon"));
            return -1;
        }

        startBattle(source.getPlayer(), trainer, BattleFormat.Companion.getGEN_9_SINGLES())
            .ifErrored(error -> {
                error.sendTo(source.getPlayer(), t -> t);
                return Unit.INSTANCE;
            })
            .ifSuccessful(battle -> {
                int moneyReward = trainer.getMoneyReward();
                if (moneyReward > 0) TrainerBattleRewarder.getInstance().addBattleReward(battle, moneyReward);
                return Unit.INSTANCE;
            });
        return 1;
    }

    private BattleStartResult startBattle(
        ServerPlayerEntity player,
        Trainer trainer,
        BattleFormat battleFormat
    ) {
        BattleActor playerActor = new PlayerBattleActor(
            player.getUuid(), Cobblemon.INSTANCE.getStorage().getParty(player).toBattleTeam(
                false, true, null
            )
        );
        BattleActor trainerActor = new TrainerBattleActor(
            trainer.getName(), UUID.randomUUID(), trainer.getBattleTeam(), new RandomBattleAI()
        );

        ErroredBattleStart errors = new ErroredBattleStart();

        if (playerActor.getPokemonList().size() < battleFormat.getBattleType().getSlotsPerActor()) {
            errors.getParticipantErrors().get(playerActor).add(BattleStartError.Companion.insufficientPokemon(
                player,
                battleFormat.getBattleType().getSlotsPerActor(),
                playerActor.getPokemonList().size()
            ));
        }

        if (Cobblemon.INSTANCE.getBattleRegistry().getBattleByParticipatingPlayer(player) != null) {
            errors.getParticipantErrors().get(playerActor).add(BattleStartError.Companion.alreadyInBattle(player));
        }

        if (errors.isEmpty()) {
            return new SuccessfulBattleStart(Cobblemon.INSTANCE.getBattleRegistry().startBattle(
                BattleFormat.Companion.getGEN_9_SINGLES(),
                new BattleSide(playerActor),
                new BattleSide(trainerActor)
            ));
        }
        return errors;
    }

}
