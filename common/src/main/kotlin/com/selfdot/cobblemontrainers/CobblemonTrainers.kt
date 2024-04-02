package com.selfdot.cobblemontrainers

import com.mojang.brigadier.CommandDispatcher
import com.mojang.logging.LogUtils
import com.selfdot.cobblemontrainers.command.TrainerCommandTree
import com.selfdot.cobblemontrainers.screen.SpeciesSelectScreen
import com.selfdot.cobblemontrainers.trainer.*
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
    val CONFIG = Config(this)
    val TRAINER_REGISTRY = TrainerRegistry(this)
    val TRAINER_WIN_TRACKER = TrainerWinTracker(this)
    val TRAINER_COOLDOWN_TRACKER = TrainerCooldownTracker(this)
    private var disabled = false
    private val LOGGER = LogUtils.getLogger()
    fun initialize() {
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

    fun enable() {
        disabled = false
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
        CONFIG.load()
        TRAINER_REGISTRY.load()
        TRAINER_WIN_TRACKER.load()
        TRAINER_COOLDOWN_TRACKER.load()
        TrainerBattleListener.getInstance().setServer(server)
        Generation5AI.initialiseTypeChart()
        TrainerPokemon.registerPokemonSendOutListener()
    }

    private fun onServerStop(server: MinecraftServer) {
        if (!disabled) {
            CobblemonTrainersLog.LOGGER.info("Storing trainer data")
            TRAINER_REGISTRY.save()
            TRAINER_WIN_TRACKER.save()
            TRAINER_COOLDOWN_TRACKER.save()
        }
    }

}
