package com.selfdot.cobblemontrainers.command;

import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.cobblemontrainers.permissions.CobblemonTrainersPermissions;
import com.selfdot.cobblemontrainers.trainer.Trainer;
import com.selfdot.cobblemontrainers.trainer.TrainerRegistry;
import com.selfdot.cobblemontrainers.util.DataKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class AddTrainerCommand extends TrainerCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("trainers")
            .requires(src -> CobblemonTrainersPermissions.checkPermission(
                src, new CobblemonPermission("", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)
            ))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("add")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("name", string()).executes(this::execute)
                )
            )
        );
    }

    protected int run(CommandContext<ServerCommandSource> ctx) {
        String name = ctx.getArgument("name", String.class);

        if (!TrainerRegistry.getInstance().addTrainer(new Trainer(
            name, new ArrayList<>(), 0, DataKeys.UNGROUPED
        ))) {
            ctx.getSource().sendError(Text.literal("Trainer " + name + " already exists"));
            return -1;
        }
        ctx.getSource().sendMessage(Text.literal("Added new trainer " + name));
        return 1;
    }

}
