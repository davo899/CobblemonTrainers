package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.selfdot.libs.minecraft.MinecraftMod;
import com.selfdot.libs.minecraft.command.CaughtCommand;
import com.selfdot.libs.minecraft.permissions.Permission;
import net.minecraft.server.command.ServerCommandSource;

public abstract class CobblemonTrainersCommand extends CaughtCommand {

    public CobblemonTrainersCommand(
        MinecraftMod mod, CommandDispatcher<ServerCommandSource> dispatcher, String root, Permission permission
    ) {
        super(mod, dispatcher, root, permission);
    }

    @Override
    protected LiteralArgumentBuilder<ServerCommandSource> node(LiteralArgumentBuilder<ServerCommandSource> root) {
        return LiteralArgumentBuilder.<ServerCommandSource>literal("trainers").then(root);
    }

}
