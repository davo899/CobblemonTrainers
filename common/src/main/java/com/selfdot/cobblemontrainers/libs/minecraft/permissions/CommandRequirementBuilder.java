package com.selfdot.cobblemontrainers.libs.minecraft.permissions;

import com.cobblemon.mod.common.api.permission.Permission;
import com.selfdot.cobblemontrainers.libs.minecraft.DisableableMod;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class CommandRequirementBuilder {

    private final DisableableMod mod;
    private boolean requiresModEnabled = false;
    private Permission opPermission = null;
    private final Set<Permission> requiredPermissions = new HashSet<>();
    private boolean requiresPlayerExecutor = false;

    public static CommandRequirementBuilder requirement(DisableableMod mod) {
        return new CommandRequirementBuilder(mod);
    }

    private CommandRequirementBuilder(DisableableMod mod) {
        this.mod = mod;
    }

    public CommandRequirementBuilder modEnabled() {
        requiresModEnabled = true;
        return this;
    }

    public CommandRequirementBuilder withOpPermission(Permission permission) {
        opPermission = permission;
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
        return mod.getPermissionValidator().hasPermission(source, permission);
    }

    public Predicate<ServerCommandSource> build() {
        return source -> {
            if (requiresModEnabled && mod.isDisabled()) return false;
            if (requiresPlayerExecutor && !source.isExecutedByPlayer()) return false;
            if (opPermission != null && hasPermission(source, opPermission)) return true;
            return requiredPermissions.stream().allMatch(permission -> hasPermission(source, permission));
        };
    }

}
