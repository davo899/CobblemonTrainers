package com.selfdot.cobblemontrainers.fabric.server;

import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.fabric.FabricPermissionValidator;
import net.fabricmc.api.DedicatedServerModInitializer;

public class CobblemonTrainersFabricServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        CobblemonTrainers.INSTANCE.setPermissionValidator_(new FabricPermissionValidator());
    }

}
