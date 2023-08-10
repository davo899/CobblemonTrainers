package com.selfdot.cobblemontrainers.forge

import dev.architectury.platform.forge.EventBuses
import com.selfdot.cobblemontrainers.CobblemonTrainers
import java.util.*
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent

@Mod(CobblemonTrainers.MODID)
class CobblemonTrainers {
    init {
        with(thedarkcolour.kotlinforforge.forge.MOD_BUS) {
            EventBuses.registerModEventBus(CobblemonTrainers.MODID, this)
            addListener(this@CobblemonTrainers::initialize)
            addListener(this@CobblemonTrainers::serverInit)
        }
    }

    private fun serverInit(event: FMLDedicatedServerSetupEvent) { }

    private fun initialize(event: FMLCommonSetupEvent) {
        CobblemonTrainers.initialize()
        println("CobblemonTrainers Forge initialized")
    }

}