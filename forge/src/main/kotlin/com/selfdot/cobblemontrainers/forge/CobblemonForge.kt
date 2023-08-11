package com.selfdot.cobblemontrainers.forge

import dev.architectury.platform.forge.EventBuses
import com.selfdot.cobblemontrainers.CobblemonTrainers
import java.util.*
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.common.MinecraftForge

@Mod(CobblemonTrainers.MODID)
class CobblemonForge {
    init {
        with(thedarkcolour.kotlinforforge.forge.MOD_BUS) {
            EventBuses.registerModEventBus(CobblemonTrainers.MODID, this)
            addListener(this@CobblemonForge::initialize)
            addListener(this@CobblemonForge::serverInit)
        }
    }

    private fun serverInit(event: FMLDedicatedServerSetupEvent) { }

    private fun initialize(event: FMLCommonSetupEvent) {
        CobblemonTrainers.initialize()
        println("CobblemonTrainers Forge initialized")
    }

}