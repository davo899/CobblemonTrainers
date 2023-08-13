package com.selfdot.cobblemontrainers

import com.mojang.brigadier.CommandDispatcher
import com.selfdot.cobblemontrainers.command.AddTrainerCommand
import com.selfdot.cobblemontrainers.command.BattleTrainerCommand
import com.selfdot.cobblemontrainers.command.ReloadCommand
import com.selfdot.cobblemontrainers.command.RemoveTrainerCommand
import com.selfdot.cobblemontrainers.command.SetupCommand
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry
import dev.architectury.event.events.common.CommandRegistrationEvent
import com.selfdot.cobblemontrainers.config.CobblemonConfig
import com.selfdot.cobblemontrainers.util.CobblemonTrainersLog
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import dev.architectury.event.events.common.LifecycleEvent
import net.minecraft.server.MinecraftServer

object CobblemonTrainers {
    lateinit var permissions: CobblemonTrainersPermissions
    const val MODID = "cobblemontrainers"
    const val TRAINER_DATA_FILENAME = "config/trainers/trainers.json"
    fun initialize() {
        permissions = CobblemonTrainersPermissions()

        // Load official Cobblemon's config.
        CobblemonConfig()

        CommandRegistrationEvent.EVENT.register(CobblemonTrainers::registerCommands)

        LifecycleEvent.SERVER_STARTING.register(CobblemonTrainers::onServerStart)
        LifecycleEvent.SERVER_STOPPING.register(CobblemonTrainers::onServerStop)
    }

    fun registerCommands(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        registry: CommandRegistryAccess,
        selection: CommandManager.RegistrationEnvironment
    ) {
        AddTrainerCommand().register(dispatcher)
        BattleTrainerCommand().register(dispatcher)
        RemoveTrainerCommand().register(dispatcher)
        SetupCommand().register(dispatcher)
        ReloadCommand().register(dispatcher)
    }

    fun onServerStart(server: MinecraftServer) {
        CobblemonTrainersLog.LOGGER.info("Loading trainer data")
        TrainerRegistry.getInstance().loadTrainersFromFile(TRAINER_DATA_FILENAME)
    }

    fun onServerStop(server: MinecraftServer) {
        CobblemonTrainersLog.LOGGER.info("Storing trainer data")
        TrainerRegistry.getInstance().storeTrainersToFile(TRAINER_DATA_FILENAME)
    }

}
