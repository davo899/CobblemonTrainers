package com.selfdot.cobblemontrainers

import com.mojang.brigadier.CommandDispatcher
import com.mojang.logging.LogUtils
import com.selfdot.cobblemontrainers.command.TrainerCommandTree
import com.selfdot.cobblemontrainers.config.CobblemonConfig
import com.selfdot.cobblemontrainers.screen.SpeciesSelectScreen
import com.selfdot.cobblemontrainers.trainer.Generation5AI
import com.selfdot.cobblemontrainers.trainer.TrainerBattleListener
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry
import com.selfdot.cobblemontrainers.trainer.TrainerWinTracker
import com.selfdot.cobblemontrainers.util.CobblemonTrainersLog
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.LifecycleEvent
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.slf4j.Logger

object CobblemonTrainers {
    const val MODID = "cobblemontrainers"
    val TRAINER_REGISTRY = TrainerRegistry(this)
    val TRAINER_WIN_TRACKER = TrainerWinTracker(this)
    private var disabled = false
    private val LOGGER = LogUtils.getLogger()
    fun initialize() {
        // Load official Cobblemon's config.
        CobblemonConfig()

        LifecycleEvent.SERVER_STARTING.register(CobblemonTrainers::onServerStart)
        LifecycleEvent.SERVER_STOPPING.register(CobblemonTrainers::onServerStop)

        CommandRegistrationEvent.EVENT.register(CobblemonTrainers::registerCommands)
    }

    fun isDisabled(): Boolean {
        return disabled
    }

    fun getLogger(): Logger {
        return LOGGER
    }

    fun disable() {
        disabled = true
    }

    private fun registerCommands(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        registry: CommandRegistryAccess,
        selection: CommandManager.RegistrationEnvironment
    ) {
        TrainerCommandTree().register(dispatcher, this)
    }

    private fun onServerStart(server: MinecraftServer) {
        SpeciesSelectScreen.loadSpecies()
        CobblemonTrainersLog.LOGGER.info("Loading trainer data")
        TRAINER_REGISTRY.load()
        TRAINER_WIN_TRACKER.load()
        TrainerBattleListener.getInstance().setServer(server);
        Generation5AI.initialiseTypeChart();
    }

    private fun onServerStop(server: MinecraftServer) {
        if (!disabled) {
            CobblemonTrainersLog.LOGGER.info("Storing trainer data")
            TRAINER_REGISTRY.save()
            TRAINER_WIN_TRACKER.save()
        }
    }

}
