package com.selfdot.cobblemontrainers.libs.minecraft;

import com.cobblemon.mod.common.api.permission.Permission;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.selfdot.cobblemontrainers.command.permission.PermissionValidator;
import com.selfdot.cobblemontrainers.libs.io.JsonFile;
import com.selfdot.cobblemontrainers.libs.io.ReadOnlyJsonFile;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class DisableableMod {

    private final String modId;
    private final boolean withReload;
    private volatile boolean disabled = false;
    private PermissionValidator permissionValidator;
    private MinecraftServer server;
    private final Set<Permission> permissions = new HashSet<>();
    private final List<ReadOnlyJsonFile> toLoad = new ArrayList<>();
    private final List<JsonFile> toSave = new ArrayList<>();

    public DisableableMod(String modId, boolean withReload) {
        this.modId = modId;
        this.withReload = withReload;
    }

    public DisableableMod(String modId) {
        this(modId, true);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public PermissionValidator getPermissionValidator() {
        return permissionValidator;
    }

    public void setPermissionValidator(PermissionValidator permissionValidator) {
        this.permissionValidator = permissionValidator;
    }

    private void updateCommandPermissions() {
        PlayerManager playerManager = server.getPlayerManager();
        if (playerManager == null) return;
        playerManager.getPlayerList().forEach(player -> server.getPlayerManager().sendCommandTree(player));
    }

    public void enable() {
        this.disabled = false;
        updateCommandPermissions();
    }

    public void disable() {
        this.disabled = true;
        updateCommandPermissions();
    }

    protected void addConfigFile(ReadOnlyJsonFile file) {
        toLoad.add(file);
    }

    protected void addDataFile(JsonFile file) {
        toLoad.add(file);
        toSave.add(file);
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void loadFiles() {
        toLoad.forEach(ReadOnlyJsonFile::load);
    }

    public void onInitialize() {
        LifecycleEvent.SERVER_STARTING.register(this::onServerStarting);
        LifecycleEvent.SERVER_STOPPING.register(this::onServerStopping);

        if (withReload) {
            CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
                    literal("reload")
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal(modId)
                        .executes(context -> {
                            enable();
                            loadFiles();
                            if (disabled) {
                                context.getSource().sendError(Text.literal(
                                    "An error occurred while reloading " + modId +
                                        ". Check the server log for details."
                                ));
                            } else {
                                context.getSource().sendMessage(Text.literal(
                                    "Reloaded " + modId + " successfully."
                                ));
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            );
        }
    }

    private void onServerStarting(MinecraftServer server) {
        this.server = server;
        loadFiles();
    }

    private void onServerStopping(MinecraftServer server) {
        if (!disabled) toSave.forEach(JsonFile::save);
    }

}
