package com.selfdot.cobblemontrainers.command.permission;

import com.cobblemon.mod.common.api.permission.Permission;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class CommandRequirementBuilder {

    private boolean requiresModEnabled = false;
    private final Set<Permission> requiredPermissions = new HashSet<>();
    private boolean requiresPlayerExecutor = false;

    public CommandRequirementBuilder modEnabled() {
        requiresModEnabled = true;
        return this;
    }

    public CommandRequirementBuilder needsPermission(Permission permission) {
        requiredPermissions.add(permission);
        return this;
    }

    public CommandRequirementBuilder executedByPlayer() {
        requiresPlayerExecutor = true;
        return this;
    }

    private boolean hasPermission(ServerCommandSource source, Permission permission) {
        return CobblemonTrainers.INSTANCE.getPermissionValidator().hasPermission(source, permission);
    }

    public Predicate<ServerCommandSource> build() {
        return source -> {
            if (requiresModEnabled && CobblemonTrainers.INSTANCE.isDisabled()) return false;
            if (requiresPlayerExecutor && !source.isExecutedByPlayer()) return false;
            if (hasPermission(source, TrainersPermissions.ALL)) return true;
            return requiredPermissions.stream().allMatch(permission -> hasPermission(source, permission));
        };
    }

}
