package com.selfdot.cobblemontrainers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.selfdot.cobblemontrainers.menu.SetupMenu;
import com.selfdot.cobblemontrainers.trainer.Generation5AI;
import com.selfdot.cobblemontrainers.trainer.TrainerBattleListener;
import com.selfdot.cobblemontrainers.trainer.TrainerPokemon;
import com.selfdot.libs.io.JsonUtils;
import com.selfdot.libs.minecraft.MinecraftMod;
import dev.architectury.event.events.common.LifecycleEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Getter
public class CobblemonTrainers extends MinecraftMod {

    @Getter
    private static CobblemonTrainers instance;

    private MinecraftServer server;
    private Config config;
    private final CobblemonTrainersPlayerDataRegistry playerDataRegistry = new CobblemonTrainersPlayerDataRegistry(
        this
    );
    private final Gson gson = new GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();

    private CobblemonTrainers() {
        super("cobblemontrainers", false);
    }

    @Override
    public void onInitialize() {
        instance = this;
        super.onInitialize();
        config = JsonUtils.loadWithDefault(gson, "config/trainers/config.json", Config.class, new Config());

        LifecycleEvent.SERVER_STARTING.register(server -> {
            this.server = server;
            TrainerBattleListener.getInstance().setServer(server);
            Generation5AI.initialiseTypeChart();
            SetupMenu.initialiseBattleItems();
            TrainerPokemon.registerPokemonSendOutListener();
        });

        String LEGACY_TRAINER_WINS_FILE = "trainers/trainerWins.json";
        Map<String, Set<UUID>> legacyTrainerWinMap = JsonUtils.loadWithDefault(
            gson,
            LEGACY_TRAINER_WINS_FILE,
            new TypeToken<Map<String, Set<UUID>>>(){}.getType(),
            Map.of()
        );
        try {
            Files.delete(Path.of(LEGACY_TRAINER_WINS_FILE));
        } catch (IOException ignored) { }
        legacyTrainerWinMap.forEach(
            (trainer, players) -> players.forEach(
                playerId -> playerDataRegistry.getOrCreate(
                    playerId, playerData -> playerData.getTrainersBeaten().add(trainer)
                )
            )
        );

        String LEGACY_TIMES_LAST_BATTLED_FILE = "trainers/timesLastBattled.json";
        Map<String, Map<UUID, Long>> legacyLastBattledMap = JsonUtils.loadWithDefault(
            gson,
            LEGACY_TRAINER_WINS_FILE,
            new TypeToken<Map<String, Map<UUID, Long>>>(){}.getType(),
            Map.of()
        );
        try {
            Files.delete(Path.of(LEGACY_TIMES_LAST_BATTLED_FILE));
        } catch (IOException ignored) { }
        legacyLastBattledMap.forEach(
            (trainer, times) -> times.forEach(
                (playerId, time) -> playerDataRegistry.getOrCreate(
                    playerId, playerData -> {
                        playerData.getTimesLastBattled().put(trainer, time);
                        return true;
                    }
                )
            )
        );
    }

}
