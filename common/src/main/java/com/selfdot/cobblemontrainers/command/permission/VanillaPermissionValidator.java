package com.selfdot.cobblemontrainers.command.permission;

import com.cobblemon.mod.common.api.permission.Permission;
import net.minecraft.command.CommandSource;

public class VanillaPermissionValidator implements PermissionValidator {

    @Override
    public boolean hasPermission(CommandSource source, Permission permission) {
        return source.hasPermissionLevel(permission.getLevel().ordinal());
    }

}
