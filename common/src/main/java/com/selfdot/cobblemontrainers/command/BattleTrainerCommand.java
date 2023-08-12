package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.*;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.TrainerBattleActor;
import com.cobblemon.mod.common.battles.ai.RandomBattleAI;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import kotlin.Unit;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class BattleTrainerCommand implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("battle")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string()).executes(this)
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

        trainer.healTeam();
        startBattle(source.getPlayer(), trainer, BattleFormat.Companion.getGEN_9_SINGLES())
            .ifErrored(error -> {
                source.sendError(Text.literal("Failed to start battle"));
                error.sendTo(source.getPlayer(), t -> t);
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
            trainer.getName(), UUID.randomUUID(), trainer.getTeam(), new RandomBattleAI()
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
