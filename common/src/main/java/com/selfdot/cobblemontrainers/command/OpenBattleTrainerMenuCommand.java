package com.selfdot.cobblemontrainers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.menu.BattleTrainerMenu;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class OpenBattleTrainerMenuCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        new BattleTrainerMenu(player);
        return SINGLE_SUCCESS;
    }

}
