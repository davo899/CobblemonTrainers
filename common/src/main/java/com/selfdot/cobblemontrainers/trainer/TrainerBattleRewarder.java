package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.util.CobblemonTrainersLog;
import kotlin.Unit;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class TrainerBattleRewarder {

    private static final TrainerBattleRewarder INSTANCE = new TrainerBattleRewarder();
    public static TrainerBattleRewarder getInstance() { return INSTANCE; }
    private CommandDispatcher<ServerCommandSource> dispatcher;
    private MinecraftServer server;
    private final Map<PokemonBattle, Integer> trainerBattleRewards = new HashMap<>();

    private TrainerBattleRewarder() {
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, battleVictoryEvent -> {
            PokemonBattle battle = battleVictoryEvent.getBattle();
            if (trainerBattleRewards.containsKey(battle)) {
                battleVictoryEvent.getWinners().forEach(battleActor -> {
                    battleActor.getPlayerUUIDs().forEach(uuid -> {
                        ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                        if (player != null) {
                            String rewardCommand = "eco give " +
                                player.getName().getString() + " " +
                                trainerBattleRewards.get(battle);

                            try {
                                dispatcher.execute(rewardCommand, server.getCommandSource());

                            } catch (CommandSyntaxException e) {
                                CobblemonTrainersLog.LOGGER.error("Could not run: " + rewardCommand);
                                e.printStackTrace();
                            }
                        }
                    });
                });
                trainerBattleRewards.remove(battle);
            }
            return Unit.INSTANCE;
        });
    }

    public void addBattleReward(PokemonBattle battle, int reward) {
        trainerBattleRewards.put(battle, reward);
    }

    public void setDispatcher(CommandDispatcher<ServerCommandSource> dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

}
