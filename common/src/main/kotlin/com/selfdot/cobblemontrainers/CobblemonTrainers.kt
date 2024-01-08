package com.selfdot.cobblemontrainers

import com.mojang.brigadier.CommandDispatcher
import com.selfdot.cobblemontrainers.command.*
import com.selfdot.cobblemontrainers.config.CobblemonConfig
import com.selfdot.cobblemontrainers.screen.SpeciesSelectScreen
import com.selfdot.cobblemontrainers.trainer.TrainerBattleListener
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry
import com.selfdot.cobblemontrainers.util.CobblemonTrainersLog
import com.selfdot.cobblemontrainers.util.DisableableMod
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.LifecycleEvent
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object CobblemonTrainers: DisableableMod() {
    const val MODID = "cobblemontrainers"
    val TRAINER_REGISTRY = TrainerRegistry(this)
    fun initialize() {
        // Load official Cobblemon's config.
        CobblemonConfig()

        LifecycleEvent.SERVER_STARTING.register(CobblemonTrainers::onServerStart)
        LifecycleEvent.SERVER_STOPPING.register(CobblemonTrainers::onServerStop)

        CommandRegistrationEvent.EVENT.register(CobblemonTrainers::registerCommands)
    }

    override fun onInitialize() {
        initialize()
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
        TrainerBattleListener.getInstance().setServer(server);
    }

    private fun onServerStop(server: MinecraftServer) {
        if (!isDisabled) {
            CobblemonTrainersLog.LOGGER.info("Storing trainer data")
            TRAINER_REGISTRY.save()
        }
    }

}
