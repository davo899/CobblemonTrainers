package com.selfdot.cobblemontrainers.command.permission;

import com.cobblemon.mod.common.api.permission.Permission;
import net.minecraft.command.CommandSource;

public interface PermissionValidator {

    boolean hasPermission(CommandSource source, Permission permission);

}
