package com.selfdot.cobblemontrainers.fabric

import com.cobblemon.mod.common.api.permission.Permission
import com.selfdot.cobblemontrainers.command.permission.PermissionValidator
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.CommandSource

class FabricPermissionValidator : PermissionValidator {

    override fun hasPermission(source: CommandSource, permission: Permission) =
        Permissions.check(source, "selfdot." + permission.literal, permission.level.numericalValue)

}
