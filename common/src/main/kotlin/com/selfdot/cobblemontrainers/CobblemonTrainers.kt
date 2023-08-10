package com.selfdot.cobblemontrainers

import com.mojang.brigadier.CommandDispatcher
import dev.architectury.event.events.common.CommandRegistrationEvent
import com.selfdot.cobblemontrainers.config.CobblemonConfig
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object CobblemonTrainers {
    lateinit var permissions: CobblemonTrainersPermissions
    const val MODID = "cobblemontrainers"
    fun initialize() {
        permissions = CobblemonTrainersPermissions()

        // Load official Cobblemon's config.
        CobblemonConfig()

        CommandRegistrationEvent.EVENT.register(CobblemonTrainers::registerCommands)
    }

    fun registerCommands(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        registry: CommandRegistryAccess,
        selection: CommandManager.RegistrationEnvironment
    ) { }
}
