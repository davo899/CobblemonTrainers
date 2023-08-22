package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.CobblemonTrainers;
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadCommand extends TrainerCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(src -> CobblemonTrainersPermissions.checkPermission(
                src, new CobblemonPermission("", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            ))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("reload").executes(this::execute)
            )
        );
    }

    protected int run(CommandContext<ServerCommandSource> context) {
        TrainerRegistry.getInstance().loadTrainersFromFile(CobblemonTrainers.TRAINER_DATA_FILENAME);
        context.getSource().sendMessage(Text.literal("Reloaded trainer file"));
        return 1;
    }

}
