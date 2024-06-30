package com.selfdot.cobblemontrainers.trainer;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.libs.minecraft.CommandExecutionBuilder;
import com.selfdot.cobblemontrainers.libs.minecraft.permissions.PermissionLevel;
import kotlin.Unit;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

import static com.selfdot.cobblemontrainers.libs.minecraft.CommandExecutionBuilder.execute;

public class TrainerBattleListener {

    private static final TrainerBattleListener INSTANCE = new TrainerBattleListener();
    public static TrainerBattleListener getInstance() { return INSTANCE; }
    private MinecraftServer server;
    private final Map<PokemonBattle, Trainer> onBattleVictory = new HashMap<>();
    private final Map<PokemonBattle, String> onBattleLoss = new HashMap<>();

    private static void runCommand(String command, ServerPlayerEntity player) {
        // Because Mohist doesn't allow executing commands as console, this option is needed.
        CommandExecutionBuilder executeCommand = execute(command).withPlayer(player);
        switch (CobblemonTrainers.INSTANCE.getConfig().getCommandExecutor()) {
            case PLAYER -> executeCommand.withLevel(PermissionLevel.ALL_COMMANDS).as(player);
            case CONSOLE -> executeCommand.as(player.getServer());
        }
    }

    private TrainerBattleListener() {
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, battleVictoryEvent -> {
            PokemonBattle battle = battleVictoryEvent.getBattle();
            if (onBattleVictory.containsKey(battle)) {
                Trainer trainer = onBattleVictory.get(battle);
                battleVictoryEvent.getWinners().forEach(battleActor -> battleActor.getPlayerUUIDs().forEach(uuid -> {
                    CobblemonTrainers.INSTANCE.getTrainerWinTracker().add(trainer, uuid);
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                    if (player == null) return;
                    String winCommand = trainer.getWinCommand();
                    if (winCommand != null && !winCommand.isEmpty()) runCommand(winCommand, player);
                }));
            }
            if (onBattleLoss.containsKey(battle)) {
                battleVictoryEvent.getLosers().forEach(battleActor -> battleActor.getPlayerUUIDs().forEach(uuid -> {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                    if (player != null) runCommand(onBattleLoss.get(battle), player);
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
