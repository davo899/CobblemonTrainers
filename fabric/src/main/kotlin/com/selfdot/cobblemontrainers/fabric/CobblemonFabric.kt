package com.selfdot.cobblemontrainers.fabric

import com.selfdot.cobblemontrainers.CobblemonTrainers
import net.fabricmc.api.ModInitializer

class CobblemonFabric : ModInitializer {
    override fun onInitialize() {
        println("CobblemonTrainers Fabric initialized");
        CobblemonTrainers.initialize();
    }
}
