package com.selfdot.cobblemontrainers;

import com.mojang.brigadier.CommandDispatcher;
import com.selfdot.cobblemontrainers.command.TrainerCommandTree;
import com.selfdot.cobblemontrainers.command.permission.PermissionValidator;
import com.selfdot.cobblemontrainers.command.permission.VanillaPermissionValidator;
import com.selfdot.cobblemontrainers.libs.minecraft.DisableableMod;
import com.selfdot.cobblemontrainers.menu.SetupMenu;
import com.selfdot.cobblemontrainers.trainer.*;
import com.selfdot.cobblemontrainers.util.DataKeys;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

@Slf4j
@Getter
public class CobblemonTrainers extends DisableableMod {

    public static final CobblemonTrainers INSTANCE = new CobblemonTrainers();
    public static final String MODID = DataKeys.MOD_NAMESPACE;

    private MinecraftServer server;
    @Setter
    private PermissionValidator permissionValidator_ = new VanillaPermissionValidator();

    private final Config config = new Config(this);
    private final TrainerRegistry trainerRegistry = new TrainerRegistry(this);
    private final TrainerWinTracker trainerWinTracker = new TrainerWinTracker(this);
    private final TrainerCooldownTracker trainerCooldownTracker = new TrainerCooldownTracker(this);

    private CobblemonTrainers() {
        super(DataKeys.MOD_NAMESPACE, false);
    }

    public void initialize() {
        super.onInitialize();

        addConfigFile(config);
        addDataFile(trainerRegistry);
        addDataFile(trainerWinTracker);
        addDataFile(trainerCooldownTracker);

        LifecycleEvent.SERVER_STARTING.register(this::onServerStarting);
        CommandRegistrationEvent.EVENT.register(this::registerCommands);
    }

    private void registerCommands(
        CommandDispatcher<ServerCommandSource> dispatcher,
        CommandRegistryAccess registry,
        CommandManager.RegistrationEnvironment selection
    ) {
        new TrainerCommandTree().register(dispatcher);
    }

    private void onServerStarting(MinecraftServer server) {
        this.server = server;
        TrainerBattleListener.getInstance().setServer(server);
        Generation5AI.initialiseTypeChart();
        SetupMenu.initialiseBattleItems();
        TrainerPokemon.registerPokemonSendOutListener();
    }

}
