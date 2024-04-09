package com.selfdot.cobblemontrainers.fabric.server

import com.selfdot.cobblemontrainers.CobblemonTrainers
import com.selfdot.cobblemontrainers.fabric.FabricPermissionValidator
import net.fabricmc.api.DedicatedServerModInitializer

class CobblemonTrainersFabricServer : DedicatedServerModInitializer {

    override fun onInitializeServer() {
        CobblemonTrainers.permissionValidator = FabricPermissionValidator()
    }

}
