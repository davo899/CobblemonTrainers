package com.selfdot.cobblemontrainers.forge;

import com.cobblemon.mod.common.api.permission.Permission;
import com.selfdot.cobblemontrainers.command.permission.PermissionValidator;
import com.selfdot.cobblemontrainers.command.permission.TrainersPermissions;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.HashMap;
import java.util.Map;

public class ForgePermissionValidator implements PermissionValidator {

    private final Map<Identifier, PermissionNode<Boolean>> nodes = new HashMap<>();

    public ForgePermissionValidator() {
        MinecraftForge.EVENT_BUS.addListener(this::onPermissionGather);
    }

    private void onPermissionGather(PermissionGatherEvent.Nodes event) {
        TrainersPermissions.all().stream().map(permission -> {
            PermissionNode<Boolean> node = new PermissionNode<>(
                permission.getIdentifier(),
                PermissionTypes.BOOLEAN,
                (player, a, b) -> {
                    if (player == null) return false;
                    return player.hasPermissionLevel(permission.getLevel().getNumericalValue());
                }
            );
            nodes.put(permission.getIdentifier(), node);
            return node;
        }).forEach(event::addNodes);
    }

    @Override
    public boolean hasPermission(CommandSource source, Permission permission) {
        if (
            !nodes.containsKey(permission.getIdentifier()) ||
            !(source instanceof ServerCommandSource serverCommandSource) ||
            serverCommandSource.getPlayer() == null
        ) {
            return source.hasPermissionLevel(permission.getLevel().getNumericalValue());
        }
        return PermissionAPI.getPermission(serverCommandSource.getPlayer(), this.nodes.get(permission.getIdentifier()));
    }

}
