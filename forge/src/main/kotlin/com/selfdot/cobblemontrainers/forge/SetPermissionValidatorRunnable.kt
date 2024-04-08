package com.selfdot.cobblemontrainers.forge

import com.selfdot.cobblemontrainers.CobblemonTrainers
import net.minecraftforge.fml.DistExecutor

class SetPermissionValidatorRunnable : DistExecutor.SafeRunnable {
    override fun run() {
        CobblemonTrainers.permissionValidator = ForgePermissionValidator()
    }

}
