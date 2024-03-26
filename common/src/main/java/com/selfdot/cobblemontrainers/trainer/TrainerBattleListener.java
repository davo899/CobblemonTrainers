package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.util.CommandUtils;
import com.selfdot.cobblemontrainers.util.DataKeys;
import kotlin.Unit;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class TrainerBattleListener {

    private static final TrainerBattleListener INSTANCE = new TrainerBattleListener();
    public static TrainerBattleListener getInstance() { return INSTANCE; }
    private MinecraftServer server;
    private final Map<PokemonBattle, Trainer> onBattleVictory = new HashMap<>();
    private final Map<PokemonBattle, String> onBattleLoss = new HashMap<>();

    private TrainerBattleListener() {
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, battleVictoryEvent -> {
            PokemonBattle battle = battleVictoryEvent.getBattle();
            if (onBattleVictory.containsKey(battle)) {
                Trainer trainer = onBattleVictory.get(battle);
                battleVictoryEvent.getWinners().forEach(battleActor -> battleActor.getPlayerUUIDs().forEach(uuid -> {
                    CobblemonTrainers.INSTANCE.getTRAINER_WIN_TRACKER().add(trainer, uuid);
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                    if (player != null) {
                        String winCommand = trainer.getWinCommand();
                        if (winCommand != null && !winCommand.isEmpty()) {
                            CommandUtils.executeCommandAsServer(
                                winCommand.replace(DataKeys.PLAYER_TOKEN, player.getGameProfile().getName()),
                                server
                            );
                        }
                        TrainerReward.giveMoneyRewardToPlayer(player, trainer);
                    }
                }));
            }
            if (onBattleLoss.containsKey(battle)) {
                battleVictoryEvent.getLosers().forEach(battleActor -> battleActor.getPlayerUUIDs().forEach(uuid -> {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                    if (player != null) {
                        CommandUtils.executeCommandAsServer(
                            onBattleLoss.get(battle).replace(
                                DataKeys.PLAYER_TOKEN, player.getGameProfile().getName()
                            ),
                            server
                        );
                    }
                }));
                onBattleLoss.remove(battle);
            }
            return Unit.INSTANCE;
        });
    }

    public void addOnBattleVictory(PokemonBattle battle, Trainer trainer) {
        onBattleVictory.put(battle, trainer);
    }

    public void addOnBattleLoss(PokemonBattle battle, String lossCommand) {
        if (lossCommand != null && !lossCommand.isEmpty()) onBattleLoss.put(battle, lossCommand);
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }
}
