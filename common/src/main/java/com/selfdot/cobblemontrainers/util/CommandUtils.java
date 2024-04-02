package com.selfdot.cobblemontrainers.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class CommandUtils {

    public static void executeCommandAsServer(String command, MinecraftServer server) {
        try {
            server.getCommandManager().getDispatcher().execute(command, server.getCommandSource());

        } catch (CommandSyntaxException e) {
            CobblemonTrainersLog.LOGGER.error("Could not run: " + command);
            CobblemonTrainersLog.LOGGER.error(e.getMessage());
        }
    }

}
