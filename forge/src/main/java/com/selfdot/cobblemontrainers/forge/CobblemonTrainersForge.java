package com.selfdot.cobblemontrainers.forge;

import com.selfdot.cobblemontrainers.CobblemonTrainers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CobblemonTrainers.MODID)
public class CobblemonTrainersForge {

    public CobblemonTrainersForge() {
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CobblemonTrainers.INSTANCE.initialize();
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, SetPermissionValidatorRunnable::new);
        System.out.println("CobblemonTrainers Forge initialized");
    }

}
