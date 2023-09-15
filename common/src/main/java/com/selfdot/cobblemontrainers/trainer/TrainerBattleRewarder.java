package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.cobblemontrainers.util.CobblemonTrainersLog;
import com.selfdot.cobblemontrainers.util.DataKeys;
import kotlin.Unit;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class TrainerBattleRewarder {

    private static final TrainerBattleRewarder INSTANCE = new TrainerBattleRewarder();
    public static TrainerBattleRewarder getInstance() { return INSTANCE; }
    private MinecraftServer server;
    private final Map<PokemonBattle, String> trainerBattleRewards = new HashMap<>();

    private TrainerBattleRewarder() {
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, battleVictoryEvent -> {
            PokemonBattle battle = battleVictoryEvent.getBattle();
            if (trainerBattleRewards.containsKey(battle)) {
                battleVictoryEvent.getWinners().forEach(battleActor -> {
                    battleActor.getPlayerUUIDs().forEach(uuid -> {
                        ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                        if (player != null) {
                            String rewardCommand = trainerBattleRewards.get(battle).replace(
                                DataKeys.PLAYER_TOKEN, player.getName().getString()
                            );

                            try {
                                server.getCommandManager().getDispatcher().execute(
                                    rewardCommand, server.getCommandSource()
                                );

                            } catch (Exception e) {
                                CobblemonTrainersLog.LOGGER.error("Could not run: " + rewardCommand);
                                CobblemonTrainersLog.LOGGER.error(e.getMessage());
                            }
                        }
                    });
                });
                trainerBattleRewards.remove(battle);
            }
            return Unit.INSTANCE;
        });
    }

    public void addBattleReward(PokemonBattle battle, String rewardCommand) {
        if (rewardCommand != null && !rewardCommand.isEmpty()) trainerBattleRewards.put(battle, rewardCommand);
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

}
