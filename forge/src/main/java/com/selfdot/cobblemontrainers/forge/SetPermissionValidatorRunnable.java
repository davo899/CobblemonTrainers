package com.selfdot.cobblemontrainers.forge;

import com.selfdot.cobblemontrainers.CobblemonTrainers;
import net.minecraftforge.fml.DistExecutor;

public class SetPermissionValidatorRunnable implements DistExecutor.SafeRunnable {

    @Override
    public void run() {
        CobblemonTrainers.INSTANCE.setPermissionValidator_(new ForgePermissionValidator());
    }

}
