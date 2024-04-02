package com.selfdot.cobblemontrainers.fabric

import com.selfdot.cobblemontrainers.CobblemonTrainers
import net.fabricmc.api.ModInitializer

class CobblemonTrainersFabric : ModInitializer {

    override fun onInitialize() {
        CobblemonTrainers.initialize()
        CobblemonTrainers.permissionValidator = FabricPermissionValidator()
        println("CobblemonTrainers Fabric initialized")
    }

}
