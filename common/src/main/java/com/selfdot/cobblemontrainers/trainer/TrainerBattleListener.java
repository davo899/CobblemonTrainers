package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
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
    private final Map<PokemonBattle, String> onBattleVictory = new HashMap<>();
    private final Map<PokemonBattle, String> onBattleLoss = new HashMap<>();

    private TrainerBattleListener() {
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, battleVictoryEvent -> {
            PokemonBattle battle = battleVictoryEvent.getBattle();
            if (onBattleVictory.containsKey(battle)) {
                battleVictoryEvent.getWinners().forEach(battleActor -> battleActor.getPlayerUUIDs().forEach(uuid -> {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                    if (player != null) {
                        CommandUtils.executeCommandAsServer(
                            onBattleVictory.get(battle).replace(
                                DataKeys.PLAYER_TOKEN, player.getName().getString()
                            ),
                            server
                        );
                    }
                }));
            }
            if (onBattleLoss.containsKey(battle)) {
                battleVictoryEvent.getLosers().forEach(battleActor -> battleActor.getPlayerUUIDs().forEach(uuid -> {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                    if (player != null) {
                        CommandUtils.executeCommandAsServer(
                            onBattleLoss.get(battle).replace(
                                DataKeys.PLAYER_TOKEN, player.getName().getString()
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

    public void addOnBattleVictory(PokemonBattle battle, String winCommand) {
        if (winCommand != null && !winCommand.isEmpty()) onBattleVictory.put(battle, winCommand);
    }

    public void addOnBattleLoss(PokemonBattle battle, String lossCommand) {
        if (lossCommand != null && !lossCommand.isEmpty()) onBattleLoss.put(battle, lossCommand);
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

}
