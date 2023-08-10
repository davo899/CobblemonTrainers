package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.Trainer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class BattleTrainerCommand implements Command<ServerCommandSource> {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            LiteralArgumentBuilder.<ServerCommandSource>
                literal("trainers").then(literal("battle")).executes(this)
        );
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) { return 0; }

    /*
    //TODO: options for preheal, battle format
    private BattleStartResult startBattle(
        ServerPlayerEntity player,
        Trainer trainer
    ) {
        PartyStore team1 = player.party().toBattleTeam(clone = cloneParties, checkHealth = !healFirst)
        val team2 = partyAccessor(player2).toBattleTeam(clone = cloneParties, checkHealth = !healFirst)

        val player1Actor = PlayerBattleActor(player1.uuid, team1)
        val player2Actor = PlayerBattleActor(player2.uuid, team2)

        val errors = ErroredBattleStart()

        for ((player, actor) in arrayOf(player1 to player1Actor, player2 to player2Actor)) {
            if (actor.pokemonList.size < battleFormat.battleType.slotsPerActor) {
                errors.participantErrors[actor] += BattleStartError.insufficientPokemon(
                    player = player,
                    requiredCount = battleFormat.battleType.slotsPerActor,
                    hadCount = actor.pokemonList.size
                )
            }

            if (BattleRegistry.getBattleByParticipatingPlayer(player) != null) {
                errors.participantErrors[actor] += BattleStartError.alreadyInBattle(player)
            }
        }

        return if (errors.isEmpty) {
            CobblemonEvents.BATTLE_STARTED_PRE.postThen(
                BattleStartedPreEvent(listOf(player1Actor, player2Actor), battleFormat, true, false, false))
            {
                SuccessfulBattleStart(
                    BattleRegistry.startBattle(
                        battleFormat = battleFormat,
                        side1 = BattleSide(player1Actor),
                        side2 = BattleSide(player2Actor)
                    )
                )
            }
            errors
        } else {
            errors
        }
    }*/

}
