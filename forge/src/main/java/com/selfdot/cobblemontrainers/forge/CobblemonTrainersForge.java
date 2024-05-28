package com.selfdot.cobblemontrainers.forge;

import com.selfdot.cobblemontrainers.CobblemonTrainers;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(CobblemonTrainers.MODID)
public class CobblemonTrainersForge {

    public CobblemonTrainersForge() {
        EventBuses.registerModEventBus(CobblemonTrainers.MODID, MinecraftForge.EVENT_BUS);
        MinecraftForge.EVENT_BUS.addListener(this::initialize);
    }

    private void initialize(FMLCommonSetupEvent event) {
        CobblemonTrainers.INSTANCE.initialize();
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, SetPermissionValidatorRunnable::new);
        System.out.println("CobblemonTrainers Forge initialized");
    }

}
