package com.selfdot.cobblemontrainers.command.permission;

import com.cobblemon.mod.common.api.permission.Permission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class TrainersPermission implements Permission {

    private final String literal;
    private final PermissionLevel level;

    public TrainersPermission(String permission, PermissionLevel level, boolean op) {
        this.literal = (op ? "" : "trainers.") + permission;
        this.level = level;
    }

    public TrainersPermission(String permission, PermissionLevel level) {
        this(permission, level, false);
    }

    @NotNull
    @Override
    public Identifier getIdentifier() {
        return new Identifier("selfdot", literal);
    }

    @NotNull
    @Override
    public PermissionLevel getLevel() {
        return level;
    }

    @NotNull
    @Override
    public String getLiteral() {
        return literal;
    }

}
