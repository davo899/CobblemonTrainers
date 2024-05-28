package com.selfdot.cobblemontrainers.fabric;

import com.cobblemon.mod.common.api.permission.Permission;
import com.selfdot.cobblemontrainers.command.permission.PermissionValidator;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;

public class FabricPermissionValidator implements PermissionValidator {

    @Override
    public boolean hasPermission(CommandSource source, Permission permission) {
        return Permissions.check(
            source, "selfdot." + permission.getLiteral(), permission.getLevel().getNumericalValue()
        );
    }

}
