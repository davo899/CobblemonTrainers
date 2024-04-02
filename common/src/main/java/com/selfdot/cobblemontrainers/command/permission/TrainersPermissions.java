package com.selfdot.cobblemontrainers.command.permission;

import com.cobblemon.mod.common.api.permission.Permission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;

import java.util.List;

public class TrainersPermissions {

    public static final Permission ALL = new TrainersPermission(
        "op.trainers", PermissionLevel.ALL_COMMANDS, true
    );
    public static final Permission RELOAD = new TrainersPermission(
        "reload", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
    );
    public static final Permission EDIT = new TrainersPermission(
        "edit", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
    );
    public static final Permission BATTLE = new TrainersPermission(
        "battle", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
    );
    public static final Permission MAKEBATTLE = new TrainersPermission(
        "makebattle", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS
    );

    public static List<Permission> all() {
        return List.of(ALL, RELOAD, EDIT, BATTLE, MAKEBATTLE);
    }

}
