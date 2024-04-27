package com.selfdot.cobblemontrainers

import com.mojang.brigadier.CommandDispatcher
import com.selfdot.cobblemontrainers.command.TrainerCommandTree
import com.selfdot.cobblemontrainers.command.permission.PermissionValidator
import com.selfdot.cobblemontrainers.command.permission.VanillaPermissionValidator
import com.selfdot.cobblemontrainers.trainer.*
import com.selfdot.cobblemontrainers.util.DataKeys
import com.selfdot.libs.minecraft.DisableableMod
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.LifecycleEvent
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object CobblemonTrainers : DisableableMod(DataKeys.MOD_NAMESPACE, false) {
    const val MODID = DataKeys.MOD_NAMESPACE
    private lateinit var server: MinecraftServer
    var permissionValidator: PermissionValidator = VanillaPermissionValidator()

    val config = Config(this)
    val trainerRegistry = TrainerRegistry(this)
    val trainerWinTracker = TrainerWinTracker(this)
    val trainerCooldownTracker = TrainerCooldownTracker(this)

    fun initialize() {
        onInitialize()

        addConfigFile(config)
        addDataFile(trainerRegistry)
        addDataFile(trainerWinTracker)
        addDataFile(trainerCooldownTracker)

        LifecycleEvent.SERVER_STARTING.register(CobblemonTrainers::onServerStarting)
        CommandRegistrationEvent.EVENT.register(CobblemonTrainers::registerCommands)
    }

    private fun registerCommands(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        registry: CommandRegistryAccess,
        selection: CommandManager.RegistrationEnvironment
    ) {
        TrainerCommandTree().register(dispatcher)
    }

    private fun onServerStarting(server: MinecraftServer) {
        this.server = server
        TrainerBattleListener.getInstance().setServer(server)
        Generation5AI.initialiseTypeChart()
    }

}
